package org.example.bussinessLogic;

import org.example.GUI.View;
import org.example.model.Server;
import org.example.model.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationManager implements Runnable {
    /// PARAMETRII CITITI DE GUI
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int maxArrivalTime;
    private int minArrivalTime;
    private int numberOfServers;
    private int numberOfClients;
    private SelectionPolicy selectionPolicy;

    /// COMPONENTE
    private Scheduler scheduler;
    private View view;
    private List<Task> generatedTask;
    private BufferedWriter write;

    /// PARAMETRII SIMULATION RESULT
    public static int totalWaitingTime = 0;
    public static int totalServiceTime = 0;
    private Map<Integer, Integer> peakMap = new HashMap<>();


    public SimulationManager(int timeLimit, int maxProcessingTime,int minProcessingTime,
                             int maxArrivalTime,int minArrivalTime,int numberOfServers,
                             int numberOfClients, SelectionPolicy selectionPolicy, View view)
    {
       this.timeLimit = timeLimit;
       this.minArrivalTime = minArrivalTime;
       this.maxArrivalTime = maxArrivalTime;
       this.minProcessingTime = minProcessingTime;
       this.maxProcessingTime = maxProcessingTime;
       this.numberOfClients = numberOfClients;
       this.numberOfServers = numberOfServers;
       this.selectionPolicy = selectionPolicy;
       this.view = view;
       scheduler = new Scheduler(numberOfServers,100);
       scheduler.changeStrategy(selectionPolicy);

       generateNRandomTasks();
    }

    /// generare random de task uri
    private void generateNRandomTasks(){
        generatedTask = new ArrayList<>();
        for(int i = 1; i <= numberOfClients; i++){
            int arrival = ThreadLocalRandom.current().nextInt(minArrivalTime,maxArrivalTime);
            int service = ThreadLocalRandom.current().nextInt(minProcessingTime,maxProcessingTime);
            Task t = new Task(service,arrival,i);
            generatedTask.add(t);
            totalServiceTime += t.getServiceTime();
        }

        ///sorteaza dupa arrival time
        generatedTask.sort(Comparator.comparingInt(Task::getArrivalTime));

    }

    @Override
    public  void run(){

        ///deschide fisierul in care vrem sa scriem
        try{
            write = new BufferedWriter(new FileWriter("ex.txt"));
        } catch (IOException e){
            e.printStackTrace();
        }

        /// afisam task urile generate
        logl("Generated tasks:");
        for(Task t: generatedTask){
            log(t + "");
        }
        logl("");
        logl("");
        logl("");
        int currentTime = 0;
        while(currentTime < timeLimit){

            ///selectam ce task uri sosesc in momentul curent
            List<Task > arrivedNow = new ArrayList<>();
            for(Task t :generatedTask){
                if(t.getArrivalTime() == currentTime){
                    arrivedNow.add(t);
                }
            }

            ///trimitem task urile inainte de afisare
            for(Task t: arrivedNow){
                scheduler.dispatchTask(t);
            }
            generatedTask.removeAll(arrivedNow);

            ///AFISAM

            logl("Time: " + currentTime);

            StringBuilder waiting = new StringBuilder();
            waiting.append("Waiting clients: ");
            for(Task t: generatedTask){
                waiting.append(t).append("; ");
            }
            logl(waiting.toString().trim());

            int index = 1; // index pentru numerotarea cozilor
            for(Server s: scheduler.getServers()){
                StringBuilder sb = new StringBuilder();
                sb.append("Queue ").append(index).append(": ");

                Task currentTask = s.getCurrentTask();
                if(currentTask != null){
                    sb.append(currentTask).append("; ");
                }

                if(s.getTask().isEmpty() && currentTask == null){
                    sb.append("Close.");
                } else {
                    for(Task t : s.getTask()){
                        sb.append(t).append("; ");
                    }
                }
                logl(sb.toString().trim());
                index++;
            }
            logl("");

            ///salveaza in map cati clienti sunt la timpul t
            int totalClientsInQueue = 0;
            for(Server s: scheduler.getServers()){
                totalClientsInQueue += s.getTask().size();
                if(s.getCurrentTask() != null)
                { totalClientsInQueue++; }
            }
            peakMap.put(currentTime,totalClientsInQueue);

            /// update interfata
            if(view != null) {
                view.updateTime(currentTime);
                view.updateWaitingClients(generatedTask);
                view.updateQueues(scheduler.getServers());
            }

            currentTime++;

            try{
                Thread.sleep(500);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        double avgWaiting = (double) totalWaitingTime/ numberOfClients;
        double avgService = (double) totalServiceTime/numberOfClients;
        logl("Avrage waiting time: " + avgWaiting);
        logl("Avrage service time: " + avgService);

        ///ora de varf
        int peakTime = -1;
        int maxClients = -1;
        for(Map.Entry<Integer,Integer> entry: peakMap.entrySet()){
            if(entry.getValue() > maxClients){
                maxClients = entry.getValue();
                peakTime = entry.getKey();
            }
        }
        logl("Peak hour: " + peakTime + " with " + maxClients + " clients. ");

        if(view != null){
            view.updateAvgServiceTime(avgService);
            view.updateAvgWaitingTime(avgWaiting);
            view.updatePeakHour(peakTime,maxClients);
        }

        ///inchidere fisier
        try{
            write.close();
            System.out.println("SIMULATION RESULTS SAVED TO TEST1/TEST2/TEST3");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /// functie pentru afisare fara new line
    private void log(String message){
        try{
            write.write(message);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /// dunctie pentru afisare cu new line
    private void logl(String message){
        try{
            write.write(message);
            write.newLine();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

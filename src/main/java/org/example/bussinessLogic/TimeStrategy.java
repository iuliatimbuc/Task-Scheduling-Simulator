package org.example.bussinessLogic;

import org.example.model.Server;
import org.example.model.Task;

import java.util.List;

public class TimeStrategy implements  Strategy{
    @Override
    public void addTask(List<Server> servers, Task task){
        Server bestServer = null;
        int minWaitingTime = Integer.MAX_VALUE;

        ///coada complet libera, fara task uri in asteptare
        for(Server s: servers) {
            if (s.getTask().isEmpty() && s.getCurrentTask() == null) {
                bestServer = s;
                break;
            }
        }
        ///daca nu gasim un server liber il cautam pe cel mai rapid
        if(bestServer == null){
            for(Server s: servers){
                int currentWaiting = 0;
                /// daca server ul proceseaza un task adaugam timpul lui de service
                Task c = s.getCurrentTask();
                if(c != null){
                    currentWaiting += c.getServiceTime();
                }
                /// adunam timpul de service al tuturor task urilor din coada
                for(Task t: s.getTask()){
                    currentWaiting += t.getServiceTime();
                }
                if(currentWaiting < minWaitingTime){
                    minWaitingTime =currentWaiting;
                    bestServer = s;
                }
            }
        }
        if(bestServer != null) {
           /// calculeaza minim waitingTime
           int waitingTime = 0;
           Task current = bestServer.getCurrentTask();
           if(current != null) {
               waitingTime += current.getServiceTime();
           }
           for(Task t: bestServer.getTask()){
               waitingTime += t.getServiceTime();
           }
           SimulationManager.totalWaitingTime += waitingTime;
           System.out.println("task " + task + " waiting time = " + waitingTime);
           bestServer.addTask(task);
           bestServer.getWaitingPeriod().addAndGet(task.getServiceTime());

       }
    }
}

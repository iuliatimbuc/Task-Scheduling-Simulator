package org.example.bussinessLogic;

import org.example.model.Server;
import org.example.model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task task){
        Server bestServer = null;
        int min = Integer.MAX_VALUE;

        /// coada complet libera
        for(Server s: servers){
            if(s.getCurrentTask() == null && s.getTask().isEmpty()){
                bestServer = s;
                break;
            }
        }
        /// daca nu s-a gasit coada libera atunci cautam cea mai scurta coada
        if(bestServer == null){
            for(Server s: servers){
                int queueSize = s.getTask().size();
                if(queueSize < min){
                    min = queueSize;
                    bestServer = s;
                }
            }
        }
        if(bestServer != null) {

            int waitingTime = 0;
            Task current = bestServer.getCurrentTask();
            if(current != null) {
                waitingTime += current.getServiceTime();
            }
            for(Task t: bestServer.getTask()){
                if(!t.equals(task)) {
                    waitingTime += t.getServiceTime();
                }
            }
            SimulationManager.totalWaitingTime += waitingTime;
            System.out.println("task " + task + "waiting time = " + waitingTime);
            bestServer.addTask(task);
            bestServer.getWaitingPeriod().addAndGet(task.getServiceTime());
        }
    }
}

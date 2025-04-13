package org.example.bussinessLogic;

import org.example.model.Server;
import org.example.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler( int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();

        // creaza si porneste toate coziile
        for(int i = 0; i < maxNoServers; i++){
            BlockingDeque<Task> t = new LinkedBlockingDeque<>();
            AtomicInteger waitingTime = new AtomicInteger(0);

            Server server = new Server(t,waitingTime);
            Thread thread = new Thread(server);
            thread.start();
            servers.add(server);
        }
    }

    /// seteaza strategia aleasa: cea mai scurta coada sau cel mai mic timp
    public void changeStrategy(SelectionPolicy selectionPolicy){
        if(selectionPolicy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ShortestQueueStrategy();
        }else if(selectionPolicy == SelectionPolicy.SHORTEST_TIME){
            strategy = new TimeStrategy();
        }
    }

    /// trimite un task catre un server
    public void dispatchTask(Task t){
        if(strategy != null){
            strategy.addTask(servers,t);
        }
    }

    public List<Server> getServers(){
        return servers;
    }

}

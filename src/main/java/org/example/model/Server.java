package org.example.model;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingDeque<Task> tasks;
    private AtomicInteger waitingPeriod;
    private volatile  Task currentTask = null;

    public Server(BlockingDeque<Task> tasks, AtomicInteger waitingPeriod) {
        this.tasks = tasks;
        this.waitingPeriod = waitingPeriod;
    }



    ///functie pentru a adauga task uri in blocking queue si a actualiza timpul total de asteptare
    public void addTask(Task newTask){
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public Task getCurrentTask(){
        return currentTask;
    }
    public BlockingDeque<Task> getTask() {
        return this.tasks;
    }
    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }
    public Collection<Task> getTasks(){
        return tasks;
    }
    public void setTasks(BlockingDeque<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        while(true){
            try{
                ///asteapta daca lista e goala si asteapta pana un task apare
                Task currentTask = tasks.takeFirst();
                this.currentTask = currentTask;

                /// daca clientul nu a fost complet servit asteapta jumatate de secunda si scade
                while(currentTask.getServiceTime() > 0){
                    Thread.sleep(500);
                    int newTime = currentTask.getServiceTime() -1;
                    currentTask.setServiceTime(newTime);
                }
                this.currentTask = null;
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


}

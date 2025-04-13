package org.example.model;

public class Task {
    private int id;
    private int arrivalTime;
    private int serviceTime;

    public Task(int serviceTime, int arrivalTime, int id) {
        this.serviceTime = serviceTime;
        this.arrivalTime = arrivalTime;
        this.id = id;
    }

    public int getServiceTime() {
        return serviceTime;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
    public String toString(){
        return "(" + id + "," + arrivalTime + "," + serviceTime +")";
    }
}

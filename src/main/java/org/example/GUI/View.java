package org.example.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

import org.example.bussinessLogic.SelectionPolicy;
import org.example.bussinessLogic.SimulationManager;
import org.example.model.Server;
import org.example.model.Task;

public class View extends JFrame {
    private JLabel timeLabel, avgWaitingLabel, avgServiceLabel, peakHourLabel;
    private JTextArea waitingArea;
    private JPanel queuesPanel;
    private ArrayList<JTextArea> queueDisplays = new ArrayList<>();
    private JPanel inputPanel, resultPanel;

    ///componente input
    private JTextField timeLimitField, maxProcessingTime, minProcessingTime, maxArrivalTime, minArrivalTime, numberOfServersField, numberOfClients;
    private JComboBox <SelectionPolicy> policyJComboBox;
    private JButton startButton;

    private int nrOfServers;

    public View(){
        setTitle("Queue Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800,600);
        setLayout(new BorderLayout());

        buildInputPanel();
        add(inputPanel, BorderLayout.CENTER);
        setVisible(true);

    }
    private void buildInputPanel(){
        inputPanel = new JPanel(new GridLayout(9,2,10,10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        inputPanel.add(new JLabel("Time limit:"));
        timeLimitField = new JTextField();
        inputPanel.add(timeLimitField);

        inputPanel.add(new JLabel("Minim arrival time:"));
        minArrivalTime = new JTextField();
        inputPanel.add(minArrivalTime);

        inputPanel.add(new JLabel("Maxim arrival time:"));
        maxArrivalTime = new JTextField();
        inputPanel.add(maxArrivalTime);

        inputPanel.add(new JLabel("Minim processing time:"));
        minProcessingTime = new JTextField();
        inputPanel.add(minProcessingTime);

        inputPanel.add(new JLabel("Maxim processing time:"));
        maxProcessingTime = new JTextField();
        inputPanel.add(maxProcessingTime);

        inputPanel.add(new JLabel("Number of clients:"));
        numberOfClients = new JTextField();
        inputPanel.add(numberOfClients);

        inputPanel.add(new JLabel("Number of servers:"));
        numberOfServersField = new JTextField();
        inputPanel.add(numberOfServersField);

        inputPanel.add(new JLabel("Section policy:"));
        policyJComboBox = new JComboBox<>(SelectionPolicy.values());
        inputPanel.add(policyJComboBox);

        startButton = new JButton("Start Simulation");
        inputPanel.add(new JLabel());
        inputPanel.add(startButton);

        startButton.addActionListener(e->startSimulation());

    }
    private void startSimulation(){
        try {
            int time = Integer.parseInt(timeLimitField.getText());
            int minA = Integer.parseInt(minArrivalTime.getText());
            int maxA = Integer.parseInt(maxArrivalTime.getText());
            int minP = Integer.parseInt(minProcessingTime.getText());
            int maxP = Integer.parseInt(maxProcessingTime.getText());
            int clients = Integer.parseInt(numberOfClients.getText());
            nrOfServers = Integer.parseInt(numberOfServersField.getText());
            SelectionPolicy policy = (SelectionPolicy) policyJComboBox.getSelectedItem();

            ///validare
            if (minA >= maxA) {
                JOptionPane.showInputDialog(this, "Min arrival time must be less than max arrival time.", "Invalid input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(minP >= maxP){
                JOptionPane.showInputDialog(this, "Min processing time must be less than max processing time.", "Invalid input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(time <= 0 || clients <= 0 || nrOfServers <= 0){
                JOptionPane.showInputDialog(this, "Enter pozitive numbers.", "Invalid input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ///eliminam formularul si contruim interfata cu simularea
            remove(inputPanel);
            buildResultPanel(nrOfServers);
            add(resultPanel, BorderLayout.CENTER);
            revalidate();
            repaint();

            SimulationManager simulationManager = new SimulationManager(
                    time, maxP, minP, maxA, minA, nrOfServers, clients, policy, this);

            new Thread(simulationManager).start();
        }catch(NumberFormatException e){
            JOptionPane.showInputDialog(this, "Enter only valid integer numbers in all fields!","Format Invalid", JOptionPane.ERROR_MESSAGE);
        }

    }

    /// afisarea cozilor in ui
    private void buildResultPanel(int nrOfServer){

        resultPanel = new JPanel(new BorderLayout());

        /// time label
        timeLabel = new JLabel("Simulation time: 0", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Times New Roman", Font.BOLD,25));
        resultPanel.add(timeLabel,  BorderLayout.NORTH);


        ///panel pentru queues
        queuesPanel = new JPanel(new GridLayout(0,3,10,10));
        for(int i = 0; i < nrOfServer ; i++){
            JTextArea queueArea = new JTextArea();
            queueArea.setFont(new Font("Times New Roman", Font.ITALIC,14));
            queueArea.setEditable(false);
            queueArea.setBorder(BorderFactory.createTitledBorder("Queue "+ (i+1)));
            queueArea.setPreferredSize(new Dimension(250,150));
            queueDisplays.add(queueArea);
            queuesPanel.add(new JScrollPane(queueArea));
        }
        JScrollPane centerScroll = new JScrollPane(queuesPanel);
        resultPanel.add(centerScroll,BorderLayout.CENTER);

        avgServiceLabel = new JLabel("Average service time: 0");
        avgServiceLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        avgWaitingLabel = new JLabel("Average waiting time: 0");
        avgWaitingLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        peakHourLabel = new JLabel("Peak hour: 0 whith 0 clients");
        peakHourLabel.setFont(new Font("Times New Roman", Font.BOLD, 14));
        JPanel statusPanel = new JPanel(new GridLayout(1,3));
        statusPanel.add(avgServiceLabel);
        statusPanel.add(avgWaitingLabel);
        statusPanel.add(peakHourLabel);

        waitingArea = new JTextArea(3,20);
        waitingArea.setEditable(false);
        waitingArea.setFont(new Font("Times New Roman", Font.ITALIC,14));
        waitingArea.setBorder(BorderFactory.createTitledBorder("Waiting clients: "));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(statusPanel);
        bottomPanel.add(new JScrollPane(waitingArea));
        resultPanel.add(bottomPanel, BorderLayout.SOUTH);

       add(resultPanel, BorderLayout.CENTER);

    }


    public void updateTime(int time){
        SwingUtilities.invokeLater(()-> timeLabel.setText("Simulation time: "+ time));
    }
    public void updateAvgWaitingTime(double time){
        SwingUtilities.invokeLater(() -> avgWaitingLabel.setText("Average waiting time: " + String.format("%.2f",time)));
    }
    public void updateAvgServiceTime(double time){
        SwingUtilities.invokeLater(() -> avgServiceLabel.setText("Average service time: " + String.format("%.2f",time)));
    }
    public void updatePeakHour(int peakTime, int maxClients){
        SwingUtilities.invokeLater(()-> peakHourLabel.setText("Peak hour: " + peakTime + " with " + maxClients + " clients. "));

    }
    public void updateWaitingClients(List<Task> waitingTask){
        StringBuilder sb = new StringBuilder();
        for(Task t: waitingTask){
            sb.append(t.toString()).append("; ");
        }
        SwingUtilities.invokeLater(() -> waitingArea.setText(sb.toString()));

    }

    public void updateQueues(List<Server> servers){

        for(int i = 0; i < servers.size(); i++){
            Server server = servers.get(i);
            BlockingDeque<Task> t = server.getTask();
            StringBuilder sb = new StringBuilder();
            Task current = server.getCurrentTask();

            if(current == null && t.isEmpty()){
                sb.append("CLOSE");
            }else {
                /// daca exista un task curent il afisam primul, iar dupa afisam task urile din coada
                if (current != null) {
                    sb.append(current.toString()).append("\n");

                }
                for (Task task : t) {
                    sb.append(task.toString()).append("\n");
                }
            }
            int finalI = i;
            SwingUtilities.invokeLater(() -> {
                if(finalI < queueDisplays.size()){
                    queueDisplays.get(finalI).setText(sb.toString());
                }
            });
        }

    }


}

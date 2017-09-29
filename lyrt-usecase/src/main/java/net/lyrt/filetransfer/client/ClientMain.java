package net.lyrt.filetransfer.client;

import net.lyrt.filetransfer.server.Command;
import net.lyrt.filetransfer.server.ui.SmartScroller;

import javax.swing.*;
import java.awt.*;

public class ClientMain extends JFrame{
    private JTextArea txtMsg;
    private JButton btnSend;
    private JTextField txtCommand;
    private ClientService service;
    private JLabel lblName;

    public ClientMain(){
        txtCommand = new JTextField(20);
        btnSend = new JButton("Send");
        btnSend.addActionListener(e -> send());
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(txtCommand, BorderLayout.WEST);
        commandPanel.add(btnSend, BorderLayout.EAST);

        txtMsg = new JTextArea(20, 10);
        JScrollPane scrollPane = new JScrollPane(txtMsg);
        new SmartScroller(scrollPane);

        JPanel panel = new JPanel();
        //panel.setPreferredSize(new Dimension(30*10, 30*10));
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane);

        JButton btnConnect = new JButton("Connect");
        btnConnect.addActionListener(e -> connect());

        JButton btnDisconnect = new JButton("Discontect");
        btnDisconnect.addActionListener(e-> disconnect());

        lblName = new JLabel("Client :");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(lblName, BorderLayout.NORTH);
        buttonPanel.add(btnConnect, BorderLayout.WEST);
        buttonPanel.add(btnDisconnect, BorderLayout.EAST);
        buttonPanel.add(commandPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
//        add(btnConnect, BorderLayout.WEST);
//        add(btnDisconnect, BorderLayout.EAST);


        setTitle("Client: File Transfer App");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
    }

    public static void main(String... args){
        ClientMain client = new ClientMain();
        client.setVisible(true);
    }

    public void send(){
        String commands = txtCommand.getText();
        service.sendCommand(commands);
//        if(commands.contains(Command.GET)){
//            service.sendRequest(commands);
//        }
    }

    public void connect(){
        service = new ClientService(txtMsg);
        service.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        String n = service.client.getLocalAddress() + ":" + service.client.getLocalPort();
        lblName.setText("Client: " + service.getClientName());
    }

    public void disconnect() {
        service.sendCommand(Command.QUIT);
    }
}

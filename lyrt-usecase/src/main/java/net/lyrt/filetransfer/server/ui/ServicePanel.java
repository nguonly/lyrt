package net.lyrt.filetransfer.server.ui;

import net.lyrt.filetransfer.server.AppState;

import javax.swing.*;
import java.awt.*;

public class ServicePanel extends JPanel{
    public ServicePanel(){
        JButton btnStartServer = new JButton("Start Server");
        JButton btnStopServer = new JButton("Stop Server");

        Dimension btnSize = new Dimension(100, 30);
        btnStartServer.setPreferredSize(btnSize);
        btnStopServer.setPreferredSize(btnSize);

        btnStartServer.addActionListener(e -> AppState.startServerService());

        btnStopServer.addActionListener(e -> AppState.stopServerService());

        add(btnStartServer);
        add(btnStopServer);

        setBorder(BorderFactory.createTitledBorder("Server Service"));
        setPreferredSize(new Dimension(30*10, 6*10));
    }
}

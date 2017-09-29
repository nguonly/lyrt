package net.lyrt.filetransfer.server.ui;

import net.lyrt.filetransfer.server.AppState;

import javax.swing.*;
import java.awt.*;

public class StartingServerPanel extends JPanel{
    private JRadioButton withoutTranquility;
    private JRadioButton withTranquility;

    public StartingServerPanel(){

//        withoutTranquility = new JRadioButton("Without Tranquility");
//        withTranquility = new JRadioButton("With Tranquility");
//        ButtonGroup btnTranquility = new ButtonGroup();
//        btnTranquility.add(withoutTranquility);
//        btnTranquility.add(withTranquility);
//
//        withoutTranquility.setSelected(true);

//        JButton btnStartServer = new JButton("Start");
//        JButton btnStopServer = new JButton("Stop");
//
//
//        btnStartServer.addActionListener(e -> {
//            AppState.setTranquilState(withTranquility.isSelected());
//
//            AppState.startServerService();
//        });
//
//        btnStopServer.addActionListener(e -> AppState.stopServerService());
//
////        add(withoutTranquility);
////        add(withTranquility);
//        add(btnStartServer);
//        add(btnStopServer);

        ServicePanel pnlService = new ServicePanel();
        add(pnlService);

        TranquilityPanel pnlTranquility = new TranquilityPanel();
        add(pnlTranquility);

        ClassReloadingPanel pnlClassReloading = new ClassReloadingPanel();
        add(pnlClassReloading);

//        setBorder(BorderFactory.createTitledBorder("Service"));
        setPreferredSize(new Dimension(20*10, 7*10));

    }
}

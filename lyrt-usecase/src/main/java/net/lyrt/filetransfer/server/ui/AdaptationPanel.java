package net.lyrt.filetransfer.server.ui;

import net.lyrt.filetransfer.server.AppState;

import javax.swing.*;
import java.awt.*;

public class AdaptationPanel extends JPanel {
    public AdaptationPanel() throws Throwable{
        JButton btnEncryptionAdaptation = new JButton("Adapt(AES)");
        JButton btnCompressionAdaptation = new JButton("Adapt(Compression)");
        JButton btnEncCompAdaptation = new JButton("Adapt(AES -> Compression)");
        JButton btnCompEncAdaptation = new JButton("Adapt(Compression -> AES)");
        JButton btnResetAdaptation = new JButton("Reset Adaptation");
//        JButton btnUnanticipatedAdaptation = new JButton("Unanticipated Adaptation");
//        JButton btnRTMonitor = new JButton("Runtime Monitor");

        Dimension btnSize = new Dimension(280, 30);
        btnResetAdaptation.setPreferredSize(btnSize);
        btnEncryptionAdaptation.setPreferredSize(btnSize);
        btnCompressionAdaptation.setPreferredSize(btnSize);
        btnEncCompAdaptation.setPreferredSize(btnSize);
        btnCompEncAdaptation.setPreferredSize(btnSize);
//        btnUnanticipatedAdaptation.setPreferredSize(btnSize);
//        btnRTMonitor.setPreferredSize(btnSize);

        btnCompressionAdaptation.addActionListener(e -> AppState.adaptCompression());
        btnEncryptionAdaptation.addActionListener(e -> AppState.adaptEncryption());
        btnEncCompAdaptation.addActionListener(e -> AppState.adaptEncryptionCompression());
        btnCompEncAdaptation.addActionListener(e -> AppState.adaptCompressionEncryption());
        btnResetAdaptation.addActionListener(e -> AppState.resetAdaptation());
//        btnUnanticipatedAdaptation.addActionListener(e -> {
//            UnanticipatedAdaptationForm uf = new UnanticipatedAdaptationForm();
//            uf.setVisible(true);
//        });
//        btnRTMonitor.addActionListener(e->SwingUtilities.invokeLater(new MainRTMonitorUI()));

        add(btnCompressionAdaptation);
        add(btnEncryptionAdaptation);
        add(btnEncCompAdaptation);
        add(btnCompEncAdaptation);
        add(btnResetAdaptation);
//        add(btnUnanticipatedAdaptation);
//        add(btnRTMonitor);

//        ClassReloadingPanel pnlClassReloading = new ClassReloadingPanel();
//        add(pnlClassReloading);

        JLabel lblClientList = new JLabel("Select connected clients to be adapted");
        add(lblClientList);
        AppState.listModel = new DefaultListModel<>();
        AppState.listClient = new JList(AppState.listModel);
        JScrollPane listScroller = new JScrollPane(AppState.listClient);
        listScroller.setPreferredSize(new Dimension(265, 70));
        add(listScroller);

        setBorder(BorderFactory.createTitledBorder("Adaptation Panel"));
        //setBackground(Color.CYAN);
        setPreferredSize(new Dimension(32*10, 30*10));
    }
}

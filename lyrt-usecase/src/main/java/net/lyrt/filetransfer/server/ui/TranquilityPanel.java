package net.lyrt.filetransfer.server.ui;

import net.lyrt.filetransfer.server.AppState;

import javax.swing.*;
import java.awt.*;

public class TranquilityPanel extends JPanel {
    public TranquilityPanel(){
        JRadioButton rdoEnable = new JRadioButton("Enable");
        JRadioButton rdoDisable = new JRadioButton("Disable");

        ButtonGroup btnClassReloading = new ButtonGroup();
        btnClassReloading.add(rdoDisable);
        btnClassReloading.add(rdoEnable);

        rdoDisable.setSelected(true);

        rdoDisable.addActionListener(e -> AppState.setTranquilState(false));
        rdoEnable.addActionListener(e -> AppState.setTranquilState(true));

        add(rdoDisable);
        add(rdoEnable);

        setBorder(BorderFactory.createTitledBorder("Tranquility"));
        setPreferredSize(new Dimension(20*10, 6*10));
    }
}

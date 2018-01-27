package net.lyrt.filetransfer.server.ui;

import net.lyrt.Registry;

import javax.swing.*;
import java.awt.*;

public class ClassReloadingPanel extends JPanel {
    public ClassReloadingPanel(){
        JRadioButton rdoEnable = new JRadioButton("Enable");
        JRadioButton rdoDisable = new JRadioButton("Disable");

        ButtonGroup btnClassReloading = new ButtonGroup();
        btnClassReloading.add(rdoDisable);
        btnClassReloading.add(rdoEnable);

        rdoDisable.setSelected(true);

        rdoDisable.addActionListener(e -> Registry.getRegistry().isUnanticipated=false);
        rdoEnable.addActionListener(e -> {
            Registry reg = Registry.getRegistry();
            reg.isUnanticipated=true;
        });

        add(rdoDisable);
        add(rdoEnable);

        setBorder(BorderFactory.createTitledBorder("Dynamic Class Reloading"));
        setPreferredSize(new Dimension(20*10, 6*10));
    }
}

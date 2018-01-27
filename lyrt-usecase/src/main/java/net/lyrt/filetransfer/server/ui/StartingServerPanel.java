package net.lyrt.filetransfer.server.ui;

import javax.swing.*;
import java.awt.*;

public class StartingServerPanel extends JPanel{

    public StartingServerPanel(){

        ServicePanel pnlService = new ServicePanel();
        add(pnlService);

        TranquilityPanel pnlTranquility = new TranquilityPanel();
        add(pnlTranquility);

        ClassReloadingPanel pnlClassReloading = new ClassReloadingPanel();
        add(pnlClassReloading);

        setPreferredSize(new Dimension(20*10, 7*10));

    }
}

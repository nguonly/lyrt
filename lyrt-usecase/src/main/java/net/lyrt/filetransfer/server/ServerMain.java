package net.lyrt.filetransfer.server;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.filetransfer.server.ui.AdaptationPanel;
import net.lyrt.filetransfer.server.ui.MessagePanel;
import net.lyrt.filetransfer.server.ui.StartingServerPanel;
import net.lyrt.unanticipation.ClassReloader;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ServerMain extends JFrame {
    private StartingServerPanel serverPanel;
    private MessagePanel messagePanel;
    private AdaptationPanel adaptationPanel;

    public ServerMain() throws Throwable{
//        Thread.setDefaultUncaughtExceptionHandler(new BugSensor());

//        ByteBuddyAgent.install();

        Registry reg = Registry.getRegistry();
        ClassReloader.classPath = "lyrt-usecase" + File.separator + "target" + File.separator + "classes";

        AppState.channel = reg.newCore(Channel.class);

//        AppState.compartment = reg.newCompartment(Compartment.class);
//        comp.activate();
        AppState.txtMsg = new JTextArea();

        serverPanel = new StartingServerPanel();
        messagePanel = new MessagePanel();
        adaptationPanel = new AdaptationPanel();

        add(serverPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.WEST);
        add(adaptationPanel, BorderLayout.EAST);

        setTitle("Server: File Transfer App");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
    }

    public static void main(String... args) throws Throwable{
        JFrame main = new ServerMain();
        main.setVisible(true);
    }
}

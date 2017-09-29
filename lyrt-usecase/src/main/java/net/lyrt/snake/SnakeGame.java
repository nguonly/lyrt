package net.lyrt.snake;

import net.lyrt.Compartment;
import net.lyrt.Registry;
import net.lyrt.helper.DumpHelper;
import net.lyrt.unanticipation.FileWatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by nguonly on 6/25/17.
 */
public class SnakeGame extends JFrame implements ActionListener {
    final static int ROW_COUNT = 30;
    final static int COL_COUNT = 30;
    final static int DOT_SIZE = 15;
    final int DELAY = 200;
    final int DELAY_STEP = 10;

    static final Font MEDIUM_FONT = new Font("Tahoma", Font.BOLD, 16);

    /**
     * The small font to draw with.
     */
    static final Font SMALL_FONT = new Font("Tahoma", Font.BOLD, 12);

    int speed = 0;

    BoardPanel boardPanel;
    StatusPanel statusPanel;
    StatisticsPanel statisticsPanel;

    Timer timer;

    Board board;
    Snake snake;
    Router router;

    Registry registry = Registry.getRegistry();

    Compartment compartment;

    public SnakeGame(Compartment compartment, Board board, Snake snake, Router router){
        this.board = board;
        this.snake = snake;
        this.router = router;
        this.compartment = compartment;

        this.compartment.activate();

//        boardPanel = new BoardPanel(this);
        boardPanel = registry.newCore(BoardPanel.class, this);
//        boardPanel = RegistryManager.getInstance().newPlayer(BoardPanel.class, new Class[]{SnakeGame.class}, new Object[]{this});
        statusPanel = new StatusPanel();
        statisticsPanel = new StatisticsPanel(this);

        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        add(statisticsPanel, BorderLayout.NORTH);

        setResizable(false);
        pack();

        setTitle("EzSnake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        start();
    }

    public void start(){
        router.setDirection(Router.DIRECTION_NONE);
        board.generateFood();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void reset(){
        router.reset();
        timer.setDelay(DELAY);
        speed = 0;
        timer.start();
    }

    public void pause(){
        router.setDirection(Router.DIRECTION_NONE);
    }

    public boolean isPause(){
        return router.getDirection() == Router.DIRECTION_NONE;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        router.update();
        if(router.gameOver) timer.stop();
        boardPanel.repaint();
        statisticsPanel.repaint();
    }

    public int getFoodEaten() {
        //return foodEaten;
        return snake.getFoodEaten();
    }

    public int getSpeed() {
        return speed+1;
        //return timer.getDelay();
    }

    public void increaseSpeed(){
        if(speed<9) {
            speed++;
            timer.setDelay(timer.getDelay() - DELAY_STEP);
        }
    }

    public void decreaseSpeed(){
        if(speed>0) {
            speed--;
            timer.setDelay(timer.getDelay() + DELAY_STEP);
        }
    }

    public static void main(String[] args){
        Registry reg = Registry.getRegistry();
//        Board board = new Board(SnakeGame.ROW_COUNT, SnakeGame.COL_COUNT);
//        //Board board = Player.initialize(Board.class, new Class[]{int.class, int.class}, new Object[]{SnakeGame.ROW_COUNT, SnakeGame.COL_COUNT});
//        Board board = RegistryManager.getInstance().newPlayer(Board.class, new Class[]{int.class, int.class}, new Object[]{SnakeGame.ROW_COUNT, SnakeGame.COL_COUNT});

//        Board board = reg.newCore(Board.class, SnakeGame.ROW_COUNT, SnakeGame.COL_COUNT);
//
//        Snake snake = new Snake(new Cell(5, 6));
//        Snake snake = new Snake(board, 5, 6);
//        Router router = new Router(snake, board);
//        Router router = reg.newCore(Router.class, snake, board);
//
//        JFrame ex = new SnakeGame(board, snake, router);
//        ex.setVisible(true);
        Compartment comp = reg.newCompartment(Compartment.class);

        EventQueue.invokeLater(() -> {
            comp.activate();
            Board board = reg.newCore(Board.class, SnakeGame.ROW_COUNT, SnakeGame.COL_COUNT);
            Snake snake = new Snake(board, 5, 6);
            Router router = reg.newCore(Router.class, snake, board);
            JFrame ex = new SnakeGame(comp, board, snake, router);
            ex.setVisible(true);
        });
//
        comp.activate();
        //Test binding and dump

        Thread watchService = new WatchService(comp);
        watchService.start();

        do{
            System.out.println(":::: Console command :::: ");
            Scanner keyboard = new Scanner(System.in);
            String key = keyboard.nextLine();
            if(key.equalsIgnoreCase("dumpRelation")){
                DumpHelper.dumpRelations(comp);
            }else if(key.equalsIgnoreCase("dumpCore")){
                DumpHelper.dumpCores();
            }else if(key.equalsIgnoreCase("dumpCompartment")) {
                DumpHelper.dumpCompartments();
            }else if(key.equalsIgnoreCase("dumpLifting")){
                DumpHelper.dumpLifting(comp);
            }else {
                System.out.println("Invalid command. Try: dumpCore, dumpCompartment, dumpRelation");
            }
        }while(true);
    }

    static class WatchService extends Thread{
        Compartment compartment;
        public WatchService(Compartment compartment){
            this.compartment = compartment;
        }
        public void run(){
            compartment.activate();
            try {
                String dir = System.getProperty("user.dir");
                Path p = Paths.get(dir + "/lyrt-usecase/src/main/java/net/lyrt/snake");
                FileWatcher fileWatcher = FileWatcher.getInstance();
                fileWatcher.register(p);
                fileWatcher.monitor("adaptation.xml");
                fileWatcher.processEvents();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}

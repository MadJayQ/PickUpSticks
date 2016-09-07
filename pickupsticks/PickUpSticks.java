/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 *
 * @author jakei_000
 */
 public class PickUpSticks extends Canvas implements MouseListener {

    /**
     * @param args the command line arguments
     */
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private int NUMBEROFSTICKS = 30;
    private final int SCALE = 5;
    
    private Player[] players;
    private Player activePlayer;
    private ScoreOverlay scoreOverlay;
    private int turnCounter = 0;
    private int sticksSelectedThisTurn = 0;
    private int sticksRemaining = 0;
    
    public String playerOneName;
    public String playerTwoName;
    
    public int playerOneType = -1;
    public int playerTwoType = -1;
    
    private static String[] options = {"AIPlayer", "Human Player"};
    
    private static SecureRandom rand;
    
    private Viewport viewPort;
    
    private static JFrame gameFrame;
    
    public static boolean isRunning = false;
    
    public static void main(String[] args) {
        rand = new SecureRandom();
        runGame();
    }
    
    public static <T> T getRandomObject(ArrayList<T> list) {
        if(list.size() > 0) {
            return list.get(rand.nextInt(list.size()));
        }
        else {
            return (T)null;
        }
    }
    
    public static void runGame() {
        PickUpSticks sticks = new PickUpSticks();
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(0, 1));
        JComboBox optionListOne = new JComboBox(options);
        JComboBox optionListTwo = new JComboBox(options);
        JTextField playerOneName = new JTextField(10);
        JTextField playerTwoName = new JTextField(10);
        optionPanel.add(optionListOne);
        optionPanel.add(new JLabel("Name:"));
        optionPanel.add(playerOneName);
        optionPanel.add(Box.createHorizontalStrut(20));
        optionPanel.add(optionListTwo);
        optionPanel.add(new JLabel("Name:"));
        optionPanel.add(playerTwoName);
        optionPanel.add(Box.createHorizontalStrut(20));
        int result = JOptionPane.showConfirmDialog(null, optionPanel, "Pre-Launch Options", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        
        sticks.playerOneName = playerOneName.getText();
        sticks.playerTwoName = playerTwoName.getText();
        sticks.playerOneType = optionListOne.getSelectedIndex();
        sticks.playerTwoType = optionListTwo.getSelectedIndex();
        
        gameFrame = new JFrame("Pick Up Sticks");
        gameFrame.setSize(new Dimension(800, 800));
        gameFrame.addMouseListener(sticks);
        gameFrame.add(sticks);
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sticks.saveFiles();
            }
        });
        gameFrame.setVisible(true);
        sticks.run();
    }
    
    public void run() {
        init();
        while(isRunning) {
            long time = System.currentTimeMillis(); //Might come up with a logic timer limiter or something
            update();
            draw();
        }
    }
    
    void init() {
        this.addMouseListener(this);
        isRunning = true;
        viewPort = new Viewport(WIDTH / SCALE, HEIGHT / SCALE, SCALE, this);
        setBackground(Color.WHITE);
        scoreOverlay = new ScoreOverlay(2);
        
        /*
        players = new Player[2];
        HumanPlayer p1 = new HumanPlayer("A");
        //AIPlayer p1 = new AIPlayer("Bob", NUMBEROFSTICKS);
        //AIPlayer p2 = new AIPlayer("Phil", NUMBEROFSTICKS);
        HumanPlayer p2 = new HumanPlayer("Jake");
        */
        
        Player p1 = (playerOneType == 0) ? new AIPlayer(playerOneName, NUMBEROFSTICKS) : new HumanPlayer(playerOneName);
        Player p2 = (playerTwoType == 0) ? new AIPlayer(playerTwoName, NUMBEROFSTICKS) : new HumanPlayer(playerTwoName);
        
        players = new Player[2];
        players[0] = p1;
        players[1] = p2;
        
        newGame();
        
    }
    
    void update() {
        viewPort.Update();
        scoreOverlay.Update(turnCounter, sticksRemaining);
        if(activePlayer instanceof AIPlayer && scoreOverlay.getWinningPlayer() == -1) {
            AIPlayer bot = (AIPlayer)activePlayer;
            ArrayList<ScreenObject> potentialSticks = (viewPort.getAllObjectsOfType(ScreenObject.ScreenObjectAction.ACTION_PICK_UP_STICK));
            ArrayList<Stick> sticks = new ArrayList<>(); //Going to filter out any garbage that may have gotten in here
            int aiNumberOfSticks = bot.getNumberOfSelectedSticks();
            if(aiNumberOfSticks > sticksRemaining) {
                aiNumberOfSticks = sticksRemaining;
            }
            for(ScreenObject obj : potentialSticks) {
                if(obj instanceof Stick) {
                    sticks.add((Stick)obj);
                }
            }
            for(int i = 0; i < aiNumberOfSticks; i++) {
                Stick s = sticks.remove(0);
                if(s == null) continue;
                viewPort.UnRegisterScreenObject(s);
                sticksRemaining--;
                scoreOverlay.updatePlayerNumSticks(turnCounter);
            }
            endTurn();
        }
        if(sticksSelectedThisTurn == 3) {
            endTurn();
        }
    }
    
    void draw() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(2); //If there is no buffer strategy, create a standard double-buffer strategy
            return; //Do not draw on the frame that we create our backbuffer
        }
        Graphics g = bs.getDrawGraphics();
        viewPort.ClearViewport(0xFFFFFF);
        //Do draw code -- Kind of emulates directX which I am familiar with 
        viewPort.BeginDraw(g);
        viewPort.Draw(); //Draw all objects that are assigned to the viewport to be updated by the renderer
        scoreOverlay.Draw(g);
        viewPort.EndDraw();
        bs.show();
    }
    
    private void newGame() {
        
        viewPort.ClearViewport(0xFFFFFF);
        viewPort.clearAllObjects();
        sticksRemaining = NUMBEROFSTICKS;
        
        scoreOverlay.setWinningPlayer(-1);
        
        
        scoreOverlay.setPlayerOne(players[0]);
        scoreOverlay.setPlayerTwo(players[1]);
        
        players[0].newGame();
        players[1].newGame();
        
        
        activePlayer = players[0];
        
        scoreOverlay.newGame();
        
        int row = 0;
        for(int index = 0; index < NUMBEROFSTICKS; index ++) {
            if(index % 10 == 0) {
                row++;
            }
            Stick stick = new Stick(((index % 10) * 4) + ((index % 10)* 4) + 38, ((row - 1) * 40) + 15);
            viewPort.RegisterScreenObject(stick);
        }
        
        for(ScreenObject obj : viewPort.getAllObjectsOfType(ScreenObject.ScreenObjectAction.ACTION_CANVAS_BUTTON)) {
            if(obj instanceof Button) {
                if(((Button) obj).buttonAction == Button.ButtonAction.BUTTON_NEW_GAME) {
                    viewPort.UnRegisterScreenObject(obj);
                }
            }
        }
        
        Button b = new Button(65, 139, 20, 7);
        b.setBackgroundColor(Color.cyan.getRGB());
        b.setBorderColor(Color.cyan.getRGB());
        b.setBorderColor(Color.black.getRGB());
        b.setPressedColor(Color.yellow.getRGB());
        b.setButtonAction(Button.ButtonAction.BUTTON_PASS);
        b.attachText("PASS", 4, 2);
        if(players[0] instanceof HumanPlayer || players[1] instanceof HumanPlayer) viewPort.RegisterScreenObject(b);
        
        activePlayer.doTurn(sticksRemaining);
    }
    
    private void endGame() {
        
        for(ScreenObject obj : viewPort.getAllObjectsOfType(ScreenObject.ScreenObjectAction.ACTION_CANVAS_BUTTON)) {
            if(obj instanceof Button) {
                if(((Button) obj).buttonAction == Button.ButtonAction.BUTTON_PASS) {
                    viewPort.UnRegisterScreenObject(obj);
                }
            }
        }
        if(players[0] instanceof AIPlayer && players[1] instanceof AIPlayer) {
            newGame();
        }
        
        Button b = new Button(65, 139, 28, 7);
        b.setBackgroundColor(Color.cyan.getRGB());
        b.setBorderColor(Color.cyan.getRGB());
        b.setBorderColor(Color.black.getRGB());
        b.setPressedColor(Color.yellow.getRGB());
        b.setButtonAction(Button.ButtonAction.BUTTON_NEW_GAME);
        b.attachText("NEW GAME", 4, 2);
        if(players[0] instanceof HumanPlayer || players[1] instanceof HumanPlayer) viewPort.RegisterScreenObject(b);
    }
    
    public void saveFiles() {
        gameFrame.dispose();
        for(Player p : players) {
            if(p instanceof AIPlayer) {
                try {
                ((AIPlayer) p).saveChoicesToFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        isRunning = false;
    }
    
    private void endTurn() {
        turnCounter = (turnCounter == 0) ? 1 : 0;
        this.sticksSelectedThisTurn = 0;
        if(sticksRemaining == 0) {
            int winner = (turnCounter == 0) ? 0 : 1;
            scoreOverlay.setWinningPlayer(winner);
            if(players[winner] instanceof AIPlayer) {
                AIPlayer p = (AIPlayer)(players[winner]);
                p.saveChoices();
            }
            endGame();
        } else {
            activePlayer = players[turnCounter];
            activePlayer.doTurn(this.sticksRemaining);
        }
    }
    


    @Override
    public void mouseClicked(MouseEvent me) {
        
        if(activePlayer instanceof HumanPlayer || scoreOverlay.getWinningPlayer() != -1) {
            Vector2 pos = new Vector2(me.getX(), me.getY());
            int action = viewPort.executeMouseClick(pos);
            if(action == ScreenObject.ScreenObjectAction.ACTION_PICK_UP_STICK.ordinal() && scoreOverlay.getWinningPlayer() == -1) {
                scoreOverlay.updatePlayerNumSticks(turnCounter);
                this.sticksSelectedThisTurn++;
                this.sticksRemaining--;
                if(sticksRemaining == 0) {
                    endTurn();
                }
            } 
            if(action == ScreenObject.ScreenObjectAction.ACTION_CANVAS_BUTTON.ordinal()) {
                ScreenObject obj = viewPort.getLastSelectedObject();
                if(!(obj instanceof Button)) {
                    //Okay now we really screwed up
                } else {
                     Button b = (Button)(obj);
                    if(b.getButtonAction() == Button.ButtonAction.BUTTON_PASS.ordinal() && sticksSelectedThisTurn > 0) {
                        this.endTurn();
                    }
                    if(b.getButtonAction() == Button.ButtonAction.BUTTON_NEW_GAME.ordinal()) {
                        this.newGame();
                    }
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }
}

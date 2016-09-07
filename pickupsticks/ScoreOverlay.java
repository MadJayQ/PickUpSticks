/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author jakei_000
 */

public class ScoreOverlay {
    
    private Player[] players;
    private Font font;
    
    private int activePlayer;
    private int sticksRemaining;
    private int playerOneWins;
    private int playerTwoWins;
    private int winningPlayer = -1;
    
    public ScoreOverlay(int numberOfPlayers) {
        font = new Font();
        players = new Player[numberOfPlayers];
        playerOneWins = 0;
        playerTwoWins = 0;
    }
    
    public void setPlayers(Player[] p) {
        players = p;
    }
    
    public void setPlayerOne(Player p) {
        players[0] = p;
    }
    
    public void setPlayerTwo(Player p) {
        players[1] = p;
    }
    
    public void Update(int currPlayer, int sticks) {
        activePlayer = currPlayer;
        sticksRemaining = sticks;
    }
    
    public void updatePlayerNumSticks(int pID) {
        if(pID > players.length) {
            //Throw an exception
        } else {
            players[pID].numberOfSticks++;
        }
    }
    public int getWinningPlayer() {
        return winningPlayer;
    }
    
    public void newGame() {
        players[0].numberOfSticks = 0;
        players[1].numberOfSticks = 0;
    }
    
    public void setWinningPlayer(int pID) {
        if(pID == 0) {
            playerOneWins++;
        }
        else if(pID == 1) {
            playerTwoWins++;
        }
        winningPlayer = pID;
    }
    
    public void Draw(Graphics g) {
        
        if(players[0] == null || players[1] == null || g == null) {
            return;
        }
        
        font.RenderText(players[0].playerName.toUpperCase() + " :" + players[0].numberOfSticks, 0, 0, (activePlayer == 0) ? Color.yellow.getRGB() : Color.red.getRGB(), g);
        font.RenderText("WINS: " + playerOneWins, 0, 50, Color.red.getRGB(), Color.black.getRGB(), g);
        font.RenderText(players[1].playerName.toUpperCase() + " :" + players[1].numberOfSticks, 600, 0, (activePlayer == 1) ? Color.yellow.getRGB() : Color.red.getRGB(), g);
        font.RenderText("WINS: " + playerTwoWins, 600, 50, Color.red.getRGB(), Color.black.getRGB(), g);
        font.RenderText("STICKS REMAINING: " + sticksRemaining, 250, 0, Color.yellow.getRGB(), g);
        if(winningPlayer != -1) {
            font.RenderText("WINNING PLAYER: " + players[winningPlayer].playerName.toUpperCase(), 0, 100, Color.green.getRGB(), g);
        }
    }
    
    
}

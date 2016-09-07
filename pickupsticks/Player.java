/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

/**
 *
 * @author jakei_000
 */
public abstract class Player {
    
    public int numberOfSticks;
    public String playerName;
    
    public Player(String name) {
        numberOfSticks = 0;
        playerName = name;
    }
    public abstract void doTurn(int sticksRemaining);
    public abstract void newGame();
    
}

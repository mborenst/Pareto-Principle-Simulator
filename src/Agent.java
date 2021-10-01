/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Random;

/**
 *
 * @author Borenste_848114
 */
public class Agent {
    private long wealth;
    private long id;

    public Agent(long initWealth, long id) {
        wealth = initWealth;
        this.id = id;
    }
    
    public boolean isPlayable() {
        return wealth > 0;
    }
    
    public void trade(Agent other) {
        if (wealth < 0 || other.getValue() < 0) {
            System.out.println("Trade between: \n"+this+"\nAnd \n"+other);
        }
        Random rando = new Random();
        boolean trade = rando.nextBoolean();
        if (trade) {
            other.lose();
            win();
        } else {
            other.win();
            lose();
        }
    }
    
    public void win() {
        wealth++;
    }
    
    public void lose() {
        wealth--;
    }
    
    public long getValue() {
        return wealth;
    }
    
    public long getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return "Agent "+id+": $"+wealth;
    }
}

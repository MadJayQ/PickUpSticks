/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pickupsticks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author jakei_000
 */
public class AIPlayer extends Player {
    
    private ArrayList<ArrayList<Integer>> choices;
    private ArrayList<Integer> choicesMade;
    private int maxNumberOfSticks;
    private int numberOfSticks;

    public AIPlayer(String name, int numOfSticks) {
        super(name);
        maxNumberOfSticks = numOfSticks;
        choices = new ArrayList<>(maxNumberOfSticks);
        choicesMade = new ArrayList<>(maxNumberOfSticks);
        for (int i = 0; i < numOfSticks; i++) {
            ArrayList<Integer> choice = new ArrayList<>(Arrays.asList(1, 2, 3));
            choices.add(choice);
            choicesMade.add(0);
        }
        
        File choiceFile = new File(playerName+".txt");
        if(choiceFile.exists()) {
            try {
                loadChoicesFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    @Override
    public void newGame() {
        for(int i = 0; i < maxNumberOfSticks; i++) {
            choicesMade.set(i, 0);
        }
    }

    @Override
    public void doTurn(int sticksRemaining) {
        ArrayList<Integer> subList = choices.get(sticksRemaining - 1);
        SecureRandom rn = new SecureRandom();
        numberOfSticks = PickUpSticks.getRandomObject(subList);
        choicesMade.set(sticksRemaining - 1, numberOfSticks);
    }
    
    public int getNumberOfSelectedSticks() {
        return numberOfSticks;
    }
    
    public void saveChoices() {
        for(int i = 0; i < choices.size(); i++) {
            int choice = choicesMade.get(i);
            if(choice != 0) {
                choices.get(i).add(choice);
            }
        }
    }
    
    public synchronized void saveChoicesToFile() throws IOException {
        File choiceFile = new File(playerName+".txt");
        if(!choiceFile.exists()) {
            choiceFile.createNewFile(); 
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(choiceFile, false));
        for(ArrayList<Integer> choiceList : choices) {
            for(Integer choice : choiceList) {
                bw.write(choice.toString());
            }
            bw.newLine();
        }
        
        bw.close();
    }
    
    private synchronized void loadChoicesFromFile() throws IOException {
        try (BufferedReader br = new BufferedReader( new FileReader(playerName + ".txt"))) {
            String line = null;
            for(int i = 0; i < choices.size(); i++) {
                line = br.readLine();
                if(line == null) {
                    System.out.println("Line null");
                    continue;
                } 
                ArrayList<Integer> availableChoices = new ArrayList<>();
                for(int j = 0; j < line.length(); j++) {
                    if(Character.isDigit(line.charAt(j))) {
                        availableChoices.add(Character.getNumericValue(line.charAt(j)));
                    } 
                }
                choices.set(i, availableChoices);
            }
        }
    }
    
}

package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Deck {
    private ArrayList<Domino> deck;

    public Deck() {
        this.deck = new ArrayList<>();
        createDeck();
        shuffle();
    }

    private void createDeck() {
        Set<String> usedCombinations = new HashSet<>();
        
        for (int left = 0; left <= 6; left++) {
            for (int right = left; right <= 6; right++) {
                String combinationKey = left + "|" + right;
                if (usedCombinations.contains(combinationKey)) {
                    continue;
                }

                String type = "Standard";
                if (left == right) {
                    type = (left % 2 == 0) ? "Double Bonus" : "Standard";
                } else if (left + right > 10) {
                    type = "Blocking";
                } else if (left + right < 5) {
                    type = "Dynamic";
                }

                usedCombinations.add(combinationKey);
                Domino newDomino = new Domino(left, right, type);
                deck.add(newDomino);
            }
        }
        System.out.println(" Pioche créée avec " + deck.size() + " dominos");
    }

    public void shuffle() {
        ArrayList<Domino> tempDeck = new ArrayList<>();
        
        while (!deck.isEmpty()) {
            int randomIndex = (int)(Math.random() * deck.size());
            tempDeck.add(deck.get(randomIndex));
            deck.remove(randomIndex);
        }
        
        deck = tempDeck;
        System.out.println(" Pioche mélangée");
    }

    public Domino draw() {
        if (deck.size() == 0) {
            System.out.println("La pioche est vide!");
            return null;
        }
        
        Domino drawnDomino = deck.get(0);
        deck.remove(0);
        System.out.println("Pioche d'un domino: " + drawnDomino);
        return drawnDomino;
    }

    public ArrayList<Domino> getDeck() {
        return deck;
    }

    public int getRemainingDominoes() {
        return deck.size();
    }

    public String toString() {
        return "Deck: " + deck.size() + " dominoes remaining";
    }


}

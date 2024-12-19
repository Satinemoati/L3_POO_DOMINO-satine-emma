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
                if (!usedCombinations.contains(combinationKey)) {
                    String type = determineType(left, right);
                    Domino newDomino = new Domino(left, right, type);
                    deck.add(newDomino);
                    usedCombinations.add(combinationKey);
                }
            }
        }
        System.out.println("Pioche créée avec " + deck.size() + " dominos");
    }

    private String determineType(int left, int right) {
        if (left == right) return "Double"; 
        if ((left + right) % 5 == 0) return "Double Bonus"; 
        if (Math.random() < 0.2) return "Dynamique";
        if (Math.random() < 0.2) return "Bloquant";
        return "Standard"; 
    }

    public void shuffle() {
        ArrayList<Domino> tempDeck = new ArrayList<>();
        while (!deck.isEmpty()) {
            int randomIndex = (int) (Math.random() * deck.size());
            tempDeck.add(deck.remove(randomIndex));
        }
        deck = tempDeck;
        System.out.println("Pioche mélangée");
    }

    public Domino draw() {
        if (deck.isEmpty()) {
            System.out.println("La pioche est vide !");
            return null;
        }
        Domino drawnDomino = deck.remove(0);
        System.out.println("Pioche d'un domino: " + drawnDomino);
        return drawnDomino;
    }

    public int getRemainingDominoes() {
        return deck.size();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }
}
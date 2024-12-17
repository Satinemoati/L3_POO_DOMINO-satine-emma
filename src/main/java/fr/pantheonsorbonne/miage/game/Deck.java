package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Deck {
    // The list containing all our dominoes
    private ArrayList<Domino> deck;

    // Constructor
    public Deck() {
        this.deck = new ArrayList<>();
        createDeck();
        shuffle();
    }

    // Creates all dominoes in the deck
    private void createDeck() {
        // Set pour vérifier l'unicité des combinaisons
        Set<String> usedCombinations = new HashSet<>();
        
        // Creating all possible dominoes (from 0-0 to 6-6)
        for (int left = 0; left <= 6; left++) {
            for (int right = left; right <= 6; right++) {
                // Créer une clé unique pour cette combinaison de valeurs
                String combinationKey = left + "|" + right;
                
                // Vérifier si cette combinaison existe déjà
                if (usedCombinations.contains(combinationKey)) {
                    continue;
                }
                
                String type = "Standard"; // Par défaut
                
                // Attribution spéciale des types
                if (left == right) {
                    // Un seul type pour chaque double
                    type = (left % 2 == 0) ? "Double Bonus" : "Standard";
                } else if (left + right > 10) {
                    type = "Blocking";
                } else if (left + right < 5) {
                    type = "Dynamic";
                }
                
                // Ajouter la combinaison à notre set
                usedCombinations.add(combinationKey);
                
                // Créer le domino
                Domino newDomino = new Domino(left, right, type);
                deck.add(newDomino);
            }
        }
        System.out.println("🎲 Pioche créée avec " + deck.size() + " dominos");
    }

    // Shuffles the dominoes
    public void shuffle() {
        // Create a temporary list
        ArrayList<Domino> tempDeck = new ArrayList<>();
        
        // While there are dominoes in the deck
        while (!deck.isEmpty()) {
            // Pick a random domino
            int randomIndex = (int)(Math.random() * deck.size());
            // Add it to the new list
            tempDeck.add(deck.get(randomIndex));
            // Remove it from the old list
            deck.remove(randomIndex);
        }
        
        // Replace the old deck with the new shuffled one
        deck = tempDeck;
        System.out.println("🔀 Pioche mélangée");
    }

    // Draw a domino
    public Domino draw() {
        // Check if deck is empty
        if (deck.size() == 0) {
            System.out.println("❌ La pioche est vide!");
            return null;
        }
        
        // Take the first domino and remove it from the deck
        Domino drawnDomino = deck.get(0);
        deck.remove(0);
        
        System.out.println("📤 Drew a domino: " + drawnDomino);
        return drawnDomino;
    }

    // Returns the list of dominoes
    public ArrayList<Domino> getDeck() {
        return deck;
    }

    // Counts remaining dominoes
    public int getRemainingDominoes() {
        return deck.size();
    }

    // Displays deck status
    public String toString() {
        return "Deck: " + deck.size() + " dominoes remaining";
    }
}
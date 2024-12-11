package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.List;

public final class Player {
    private final String name;
    private final List<Domino> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>(); // liste qui stocke tous les domino de la main du joueur
    }

    public String getName() {
        return name;
    }

    public List<Domino> getHand() {
        return hand;
    }

    
    public void addDomino(Domino domino) { // Ajoute un domino a la main du joueur
        hand.add(domino);
    }

    
    public void playDomino(Domino domino) { // Enleve un domino de la main du joueur
        hand.remove(domino);
    }

    @Override
    public String toString() {
        return "Player{" +
               "name='" + name + '\'' +
               ", hand=" + hand +
               '}';
    } 
}

package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;

public class Player {
    private String name;
    private String skill;
    private ArrayList<Domino> hand;

    public Player(String name, String skill) {
        this.name = name;
        this.skill = skill;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getSkill() {
        return skill;
    }

    public void addDomino(Domino domino) {
        hand.add(domino);
    }

    public void playDomino(Domino domino) {
        hand.remove(domino);
    }

    public ArrayList<Domino> getHand() {
        return hand;
    }

    public boolean canPlay(Board board) {
        for (Domino domino : hand) {
            if (board.canPlaceDomino(domino)) {
                return true;
            }
        }
        return false;
    }

    public Domino chooseDomino(Board board) {
        for (Domino domino : hand) {
            if (board.canPlaceDomino(domino)) {
                return domino;
            }
        }
        return null;
    }

    public Domino getHighestDouble() {
        Domino highestDouble = null;
        for (Domino domino : hand) {
            if (domino.isDouble() && (highestDouble == null || domino.getTotal() > highestDouble.getTotal())) {
                highestDouble = domino;
            }
        }
        return highestDouble;
    }

    public int calculateRemainingPoints() {
        return hand.stream().mapToInt(Domino::getTotal).sum();
    }
}


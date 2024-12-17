package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.List;

public final class Player {
    private final String name;
    private final List<Domino> hand;
    private final String skill;

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

    public List<Domino> getHand() {
        return hand;
    }

    public void addDomino(Domino domino) {
        hand.add(domino);
    }

    public void playDomino(Domino domino) {
        hand.remove(domino);
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
        switch (skill) {
            case "Aggressive":
                return chooseBestScore(board);
            case "Defensive":
                return chooseBlockingDomino(board);
            case "Opportunist":
                return chooseDouble(board);
            default:
                return chooseFirst(board);
        }
    }

    private Domino chooseBestScore(Board board) {
        Domino bestDomino = null;
        int bestScore = -1;

        for (Domino domino : hand) {
            if (board.canPlaceDomino(domino)) {
                int score = domino.getLeftValue() + domino.getRightValue();
                if (score > bestScore) {
                    bestScore = score;
                    bestDomino = domino;
                }
            }
        }
        return bestDomino != null ? bestDomino : chooseFirst(board);
    }

    private Domino chooseBlockingDomino(Board board) {
        for (Domino domino : hand) {
            if (domino.getType().equals("Blocking") && board.canPlaceDomino(domino)) {
                return domino;
            }
        }
        return chooseFirst(board);
    }

    private Domino chooseDouble(Board board) {
        for (Domino domino : hand) {
            if (domino.getLeftValue() == domino.getRightValue() && board.canPlaceDomino(domino)) {
                return domino;
            }
        }
        return chooseFirst(board);
    }

    private Domino chooseFirst(Board board) {
        for (Domino domino : hand) {
            if (board.canPlaceDomino(domino)) {
                return domino;
            }
        }
        return null;
    }

    public int calculateRemainingPoints() {
        int total = 0;
        for (Domino domino : hand) {
            total += domino.getLeftValue() + domino.getRightValue();
        }
        return total;
    }

    @Override
    public String toString() {
        return name + " (" + skill + ") - Hand: " + hand;
    }
}
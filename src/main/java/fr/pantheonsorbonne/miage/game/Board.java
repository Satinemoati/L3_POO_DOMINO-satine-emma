package fr.pantheonsorbonne.miage.game;

import java.util.LinkedList;

public class Board {
    private final LinkedList<Domino> dominos;

    public Board() {
        this.dominos = new LinkedList<>();
    }

    public LinkedList<Domino> getDominos() {
        return dominos;
    }

    public boolean canPlaceDomino(Domino domino) {
        if (dominos.isEmpty()) {
            return true; // Premier domino peut toujours être placé
        }
        Domino first = dominos.getFirst();
        Domino last = dominos.getLast();
        return domino.getLeftValue() == first.getLeftValue() || domino.getRightValue() == first.getLeftValue()
                || domino.getLeftValue() == last.getRightValue() || domino.getRightValue() == last.getRightValue();
    }

    public void placeDomino(Domino domino, boolean atStart) {
        if (atStart) {
            dominos.addFirst(domino);
        } else {
            dominos.addLast(domino);
        }
    }

    @Override
    public String toString() {
        return "Board: " + dominos;
    }
}
package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;

public class Board {
    private ArrayList<Domino> dominos;

    public Board() {
        this.dominos = new ArrayList<>();
    }

    public boolean isEmpty() {
        return dominos.isEmpty();
    }

    public boolean canPlaceDomino(Domino domino) {
        if (isEmpty() || domino.getType().equals("Dynamic")) {
            return true;
        }
        int first = getFirstValue();
        int last = getLastValue();
        return domino.getLeftValue() == first || domino.getRightValue() == first ||
               domino.getLeftValue() == last || domino.getRightValue() == last;
    }

    public boolean placeDomino(Domino domino, boolean atStart) {
        if (isEmpty()) {
            dominos.add(domino);
            return false;
        }

        if (domino.getType().equals("Dynamic")) {
            if (atStart) {
                domino.changeValues(getFirstValue(), domino.getRightValue());
                dominos.add(0, domino);
            } else {
                domino.changeValues(domino.getLeftValue(), getLastValue());
                dominos.add(domino);
            }
            return true;
        }

        if (atStart) {
            if (domino.getRightValue() == getFirstValue()) {
                dominos.add(0, domino);
            } else if (domino.getLeftValue() == getFirstValue()) {
                dominos.add(0, new Domino(domino.getRightValue(), domino.getLeftValue(), domino.getType()));
            }
        } else {
            if (domino.getLeftValue() == getLastValue()) {
                dominos.add(domino);
            } else if (domino.getRightValue() == getLastValue()) {
                dominos.add(new Domino(domino.getRightValue(), domino.getLeftValue(), domino.getType()));
            }
        }
        return true;
    }

    public int getFirstValue() {
        return isEmpty() ? -1 : dominos.get(0).getLeftValue();
    }

    public int getLastValue() {
        return isEmpty() ? -1 : dominos.get(dominos.size() - 1).getRightValue();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Plateau vide";
        }
        StringBuilder plateau = new StringBuilder("\nPlateau de jeu:\n================\n");
        for (Domino domino : dominos) {
            plateau.append(domino).append(" ");
        }
        return plateau.append("\n================").toString();
    }

    public static Board fromString(String str) {
        Board board = new Board();

        if (str.isEmpty() || str.contains("Plateau vide")) {
            return board;
        }

        str = str.replace("\nPlateau de jeu:\n", "")
                 .replace("================\n", "")
                 .trim();

        String[] dominoStrings = str.split(" ");
        for (String dominoStr : dominoStrings) {
            if (!dominoStr.isEmpty()) {
                Domino domino = Domino.fromString(dominoStr.trim());
                board.dominos.add(domino);
            }
        }

        return board;
    }
}
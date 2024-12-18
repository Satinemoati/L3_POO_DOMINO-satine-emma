package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;

public class Board {
    private ArrayList<Domino> dominos;

    public Board() {
        this.dominos = new ArrayList<>();
    }

    public ArrayList<Domino> getDominos() {
        return dominos;
    }

    public boolean isEmpty() {
        return dominos.isEmpty(); 
    }

    public int getFirstValue() {
        return isEmpty() ? -1 : dominos.get(0).getLeftValue();
    }

    public int getLastValue() {
        return isEmpty() ? -1 : dominos.get(dominos.size() - 1).getRightValue();
    }

    public boolean canPlaceDomino(Domino domino) {
        if (isEmpty() || domino.getType().equals("Dynamic")) {
            return true;
        }
        int debut = getFirstValue();
        int fin = getLastValue();
        
        return domino.getLeftValue() == debut || domino.getRightValue() == debut ||
               domino.getLeftValue() == fin || domino.getRightValue() == fin;
    }

    public boolean placeDomino(Domino domino, boolean auDebut) {
        if (isEmpty()) {
            dominos.add(domino);
            return false;
        }

        if (domino.getType().equals("Dynamic")) {
            if (auDebut) {
                domino.changeValues(getFirstValue(), domino.getRightValue());
                dominos.add(0, domino);
            } else {
                domino.changeValues(domino.getLeftValue(), getLastValue());
                dominos.add(domino);
            }
            return auDebut;
        }

        if (auDebut) {
            if (domino.getRightValue() == getFirstValue()) {
                dominos.add(0, domino);
            } else if (domino.getLeftValue() == getFirstValue()) {
                Domino retourne = new Domino(domino.getRightValue(), domino.getLeftValue(), domino.getType());
                dominos.add(0, retourne);
            }
        } else {
            if (domino.getLeftValue() == getLastValue()) {
                dominos.add(domino);
            } else if (domino.getRightValue() == getLastValue()) {
                Domino retourne = new Domino(domino.getRightValue(), domino.getLeftValue(), domino.getType());
                dominos.add(retourne);
            }
        }
        return auDebut;
    }

    @Override
    public String toString() {
        if (dominos.isEmpty()) {
            return "Plateau vide";
        }
        
        StringBuilder plateau = new StringBuilder("\nPlateau de jeu:\n");
        plateau.append("================\n");
        
        for (Domino domino : dominos) {
            plateau.append(domino.toString()).append(" ");
        }
        
        plateau.append("\n================");
        return plateau.toString();
    }

    public static Board fromString(String str) {
        Board board = new Board();
        if (!str.isEmpty()) {
            // Parse le string et ajoute les dominos au board
            // Pour l'instant, on retourne juste un board vide
        }
        return board;
    }
}


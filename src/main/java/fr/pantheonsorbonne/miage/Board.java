package fr.pantheonsorbonne.miage;

import java.util.ArrayList;

import fr.pantheonsorbonne.miage.game.Domino;

public class Board {
    
    // Liste qui contient tous les dominos posés sur le plateau
    private ArrayList<Domino> dominos;

    // Crée un plateau vide
    public Board() {
        this.dominos = new ArrayList<>();
    }

    // Renvoie la liste des dominos
    public ArrayList<Domino> getDominos() {
        return dominos;
    }

    // Vérifie si le plateau est vide
    public boolean isEmpty() {
        return dominos.isEmpty(); 
    }

    // Renvoie la valeur du premier domino (-1 si vide)
    public int getFirstValue() {
        return isEmpty() ? -1 : dominos.get(0).getLeftValue();
    }

    // Renvoie la valeur du dernier domino (-1 si vide) 
    public int getLastValue() {
        return isEmpty() ? -1 : dominos.get(dominos.size() - 1).getRightValue();
    }

    // Vérifie si un domino peut être placé
    public boolean canPlaceDomino(Domino domino) {
        // Si plateau vide ou domino dynamique, on peut toujours placer
        if (isEmpty() || domino.getType().equals("Dynamic")) {
            return true;
        }

        // Sinon on vérifie si une des valeurs correspond aux extrémités
        int debut = getFirstValue();
        int fin = getLastValue();
        
        return domino.getLeftValue() == debut || 
               domino.getRightValue() == debut ||
               domino.getLeftValue() == fin || 
               domino.getRightValue() == fin;
    }

    // Place un domino sur le plateau
    public void placeDomino(Domino domino, boolean auDebut) {
        if (isEmpty()) {
            dominos.add(domino);
            return;
        }

        // Si domino dynamique, on adapte sa valeur
        if (domino.getType().equals("Dynamic")) {
            if (auDebut) {
                domino.changeValues(getFirstValue(), domino.getRightValue());
                dominos.add(0, domino);
            } else {
                domino.changeValues(domino.getLeftValue(), getLastValue());
                dominos.add(domino);
            }
            return;
        }

        // Pour un domino normal
        if (auDebut) {
            // On vérifie quelle extrémité correspond et on place en conséquence
            if (domino.getRightValue() == getFirstValue()) {
                dominos.add(0, domino);
            } else if (domino.getLeftValue() == getFirstValue()) {
                // On retourne le domino
                Domino retourne = new Domino(domino.getRightValue(), domino.getLeftValue(), domino.getType());
                dominos.add(0, retourne);
            }
        } else {
            if (domino.getLeftValue() == getLastValue()) {
                dominos.add(domino);
            } else if (domino.getRightValue() == getLastValue()) {
                // On retourne le domino
                Domino retourne = new Domino(domino.getRightValue(), domino.getLeftValue(), domino.getType());
                dominos.add(retourne);
            }
        }
    }

    // Affiche le plateau avec tous les dominos
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
}
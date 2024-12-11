package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Deck {
        private final List<Domino> dominos;
    
        public Deck() { // Crée un ensemble complet de tuiles de dominos (0|0 à 6|6)
            this.dominos = new ArrayList<>();
            for (int i = 0; i <= 6; i++) {
                for (int j = i; j <= 6; j++) {
                    dominos.add(new Domino(i, j));
                }
            }
            shuffle();
        }
    
        
        public void shuffle() { //mélange la pioche
            Collections.shuffle(dominos); //mélanger aléatoirement les éléments d'une liste
        }
    
        public Domino draw() {  // Tirer un domino a partir de la pioche 
            if (dominos.isEmpty()) {
                throw new IllegalStateException("La pioche est vide :(");
            }
            return dominos.remove(0);
        }
    
        
        public List<Domino> getDominos() { // Obtenir les dominos restants de la pioche 

            return dominos;
        }
    
        @Override
        public String toString() {
            return "Deck{" +
                   "dominos=" + dominos +
                   '}';
        }
    }

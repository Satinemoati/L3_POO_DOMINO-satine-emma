package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Deck {
    private final List<Domino> dominos;

    public Deck() {
        this.dominos = new ArrayList<>();
        String[] types = {"Standard", "Dynamique", "Bloquant", "Double Bonus"};
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                String type = types[(i + j) % types.length];
                dominos.add(new Domino(i, j, type));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(dominos);
    }

    public Domino draw() {
        if (dominos.isEmpty()) {
            throw new IllegalStateException("La pioche est vide :(");
        }
        return dominos.remove(0);
    }

    public List<Domino> getDominos() {
        return dominos;
    }

    @Override
    public String toString() {
        return "Deck{" + "dominos=" + dominos + '}';
    }
}
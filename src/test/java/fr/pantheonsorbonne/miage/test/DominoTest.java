package fr.pantheonsorbonne.miage.test;

import fr.pantheonsorbonne.miage.game.*;

import java.util.ArrayList;
import java.util.List;

public class DominoTest {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Alice", "Aggressif"));
        players.add(new Player("Bob", "Défensif"));
        players.add(new Player("Charlie", "Opportuniste"));

        Game game = new Game(players);
        game.start();
    }
}
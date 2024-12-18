package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.game.Game;
import fr.pantheonsorbonne.miage.Player;
import fr.pantheonsorbonne.miage.game.*;

import java.util.ArrayList;
import java.util.List;

public class DominoTest {
    public static void main(String[] args) {
        afficherBienvenue();
        List<Player> joueurs = creerJoueurs();
        afficherJoueurs(joueurs);
        demarrerPartie(joueurs);
    }

    private static void afficherBienvenue() {
        System.out.println("=> Demarrage du jeu de Dominos");
        System.out.println("================================");
        System.out.println("Legende des dominos speciaux:");
        System.out.println("D[X|Y] = Domino Dynamique");
        System.out.println("B[X|Y] = Domino Bloquant");
        System.out.println("*[X|Y] = Domino Double Bonus");
        System.out.println("================================");
    }

    private static List<Player> creerJoueurs() {
        List<Player> joueurs = new ArrayList<>();
        joueurs.add(new Player("Emma", "Aggressif"));
        joueurs.add(new Player("Satine", "Défensif"));
        joueurs.add(new Player("Nicolas", "Opportuniste"));
        return joueurs;
    }

    private static void afficherJoueurs(List<Player> joueurs) {
        System.out.println("\n -> Joueurs participants:");
        for (Player joueur : joueurs) {
            System.out.println("- " + joueur.getName() + " (" + joueur.getSkill() + ")");
        }
    }

    private static void demarrerPartie(List<Player> joueurs) {
        Game partie = new Game(joueurs);
        System.out.println("\n -> Début de la partie!");
        System.out.println("================================");

        while (!partie.isGameOver()) {
            partie.playTurn();
            faireUnePause();
        }
    }

    private static void faireUnePause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(" -> Interruption du jeu");
        }
    }
}

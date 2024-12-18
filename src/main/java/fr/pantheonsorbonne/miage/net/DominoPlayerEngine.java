package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.PlayerFacade;
import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

import java.util.*;

public class DominoPlayerEngine extends DominoNetworkEngine {

    private final List<String> hand;  // La main du joueur

    /**
     * Constructeur pour le joueur.
     * @param game Le jeu en cours
     * @param facade La façade qui gère la communication réseau
     */
    public DominoPlayerEngine(Game game, PlayerFacade facade) {
        super(game, facade);
        this.hand = new ArrayList<>();
    }

    /**
     * Démarre la partie pour le joueur.
     * Connexion au jeu et gestion des commandes reçues pendant le jeu.
     */
    @Override
    public void start() {
        System.out.println("=> Connexion au jeu en mode réseau (Joueur)");
        boolean gameRunning = true;

        while (gameRunning) {
            GameCommand command = receiveCommand();

            switch (command.name()) {
                case "initialHand":
                    System.out.println("Main initiale reçue : " + command.body());
                    break;

                case "yourTurn":
                    System.out.println("C'est mon tour de jouer !");
                    // Simule l'envoi d'un domino joué
                    facade.sendGameCommandToPlayer(game, "playDomino", "D[1|2]");
                    break;

                case "gameOver":
                    System.out.println("Partie terminée : " + command.body());
                    gameRunning = false;
                    break;
            }
        }
    }
}

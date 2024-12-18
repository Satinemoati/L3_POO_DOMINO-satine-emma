package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.HostFacade;
import fr.pantheonsorbonne.miage.game.*;
import fr.pantheonsorbonne.miage.model.Game;

import java.util.*;

public class DominoHostEngine extends DominoNetworkEngine {

    private final Board board;  // Le plateau de jeu
    private final Deck deck;    // Le jeu de dominos

    /**
     * Constructeur pour l'hôte du jeu.
     * @param game Le jeu en cours
     * @param facade La façade qui gère la communication réseau
     */
    public DominoHostEngine(Game game, HostFacade facade) {
        super(game, facade);
        this.board = new Board();
        this.deck = new Deck();
    }

    /**
     * Démarre le jeu en mode hôte.
     * Initialise le deck, distribue les dominos, puis lance la partie.
     */
    @Override
    public void start() {
        System.out.println("=> Démarrage du jeu en mode réseau (Hôte)");
        deck.shuffle();  // Mélange du deck

        sendCommandToAll("gameStarted", "La partie a commencé!");

        // Distribution initiale des dominos
        for (String player : game.getPlayers()) {
            ArrayList<Domino> hand = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                hand.add(deck.draw());  // Donne un domino à chaque joueur
            }
            sendCommandToPlayer(player, "initialHand", hand.toString());
        }

        playGame();  // Démarre la boucle de jeu
    }

    /**
     * Méthode principale pour jouer la partie.
     * Chaque joueur prend son tour de jouer, en recevant et jouant un domino.
     */
    private void playGame() {
        while (!isGameOver()) {  // Tant que le jeu n'est pas terminé
            for (String player : game.getPlayers()) {
                sendCommandToPlayer(player, "yourTurn", "Jouez un domino!");

                // Recevoir et simuler la commande de domino joué
                GameCommand command = receiveCommand();
                if (command.name().equals("playDomino")) {
                    // Simule l'action de jouer un domino
                    System.out.println(player + " a joué : " + command.body());
                    // Logique pour vérifier et placer le domino (non implémentée ici)
                }
            }
        }

        declareWinner();  // Déclare le gagnant
    }

    /**
     * Vérifie si le jeu est terminé.
     * @return true si le jeu est terminé, false sinon
     */
    private boolean isGameOver() {
        return deck.getRemainingDominoes() == 0;  // Le jeu est terminé lorsque tous les dominos ont été joués
    }

    /**
     * Déclare le gagnant de la partie.
     */
    private void declareWinner() {
        sendCommandToAll("gameOver", "La partie est terminée!");
    }
}

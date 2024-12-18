package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.DominoNetworkEngine;
import fr.pantheonsorbonne.miage.PlayerFacade;
import fr.pantheonsorbonne.miage.game.*;
import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

import java.util.*;

public class DominoPlayerEngine extends DominoNetworkEngine {

    private final List<Domino> hand;   // La main du joueur
    private Board localBoard;          // Le plateau local
    private final String playerName;   // Nom du joueur
    private final String skill;        // Compétence du joueur

    public DominoPlayerEngine(Game game, PlayerFacade facade, String playerName, String skill) {
        super(game, facade);
        this.hand = new ArrayList<>();
        this.localBoard = new Board();
        this.playerName = playerName;
        this.skill = skill;
    }

    @Override
    public void start() {
        System.out.println("=> Connexion au jeu de dominos en tant que joueur réseau...");
        boolean gameRunning = true;

        while (gameRunning) {
            GameCommand command = receiveCommand();
            if (command == null) continue;

            switch (command.name()) {
                case "gameStarted":
                    System.out.println("Partie commencée : " + command.body());
                    break;

                case "initialHand":
                    initializeHand(command.body());
                    break;

                case "boardState":
                    updateBoard(command.body());
                    displayState();
                    break;

                case "yourTurn":
                    System.out.println("C'est votre tour !");
                    playTurn();
                    break;

                case "gameOver":
                    System.out.println("Partie terminée ! Résultat : " + command.body());
                    gameRunning = false;
                    break;

                case "gameBlocked":
                    System.out.println("La partie est bloquée !");
                    gameRunning = false;
                    break;

                default:
                    System.out.println("Commande inconnue : " + command.name());
            }
        }
    }

    /**
     * Initialise la main du joueur avec les dominos reçus.
     */
    private void initializeHand(String handStr) {
        hand.clear();
        Arrays.stream(handStr.replaceAll("[\\[\\]]", "").split(", "))
              .map(Domino::fromString)
              .forEach(hand::add);
        System.out.println("Votre main initiale : " + hand);
    }

    /**
     * Met à jour le plateau local reçu depuis l'hôte.
     */
    private void updateBoard(String boardState) {
        localBoard = Board.fromString(boardState);
    }

    /**
     * Gère le tour de jeu du joueur.
     */
    private void playTurn() {
        Domino dominoToPlay = selectValidDomino();

        if (dominoToPlay != null) {
            sendCommandToPlayer(playerName, "playDomino", dominoToPlay.toString());
            hand.remove(dominoToPlay);
            System.out.println("Vous avez joué : " + dominoToPlay);

            // Compétence Opportuniste : rejouer immédiatement si c'est un double
            if (skill.equals("Opportuniste") && dominoToPlay.isDouble()) {
                System.out.println("Compétence Opportuniste activée : Vous rejouez !");
                playTurn();
            }
        } else {
            // Pioche ou passe le tour
            if (!drawDomino()) {
                System.out.println("Impossible de jouer ou de piocher. Vous passez votre tour.");
                sendCommandToPlayer(playerName, "pass", "");
            }
        }
    }

    /**
     * Sélectionne un domino valide à jouer en tenant compte des compétences et types de dominos.
     */
    private Domino selectValidDomino() {
        for (Domino domino : hand) {
            if (localBoard.canPlaceDomino(domino)) {
                if (domino.getType().equals("Blocking")) {
                    System.out.println("Domino Bloquant activé : Le prochain joueur sera bloqué !");
                }
                return domino;
            }
        }
        return null;
    }

    /**
     * Tente de piocher un domino jusqu'à en trouver un jouable.
     */
    private boolean drawDomino() {
        System.out.println("Aucun domino jouable, tentative de pioche...");
        while (true) {
            sendCommandToPlayer(playerName, "drawDomino", "");
            GameCommand command = receiveCommand();
            if (command != null && command.name().equals("dominoDrawn")) {
                Domino drawnDomino = Domino.fromString(command.body());
                hand.add(drawnDomino);
                System.out.println("Domino pioché : " + drawnDomino);
                if (localBoard.canPlaceDomino(drawnDomino)) {
                    return true;
                }
            } else {
                break; // Arrêter si la pioche est vide
            }
        }
        return false;
    }

    /**
     * Affiche l'état actuel du jeu.
     */
    private void displayState() {
        System.out.println("\nÉtat actuel du jeu :");
        System.out.println("Plateau : " + localBoard);
        System.out.println("Votre main : " + hand);
    }
}
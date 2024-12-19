package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.game.Board;
import fr.pantheonsorbonne.miage.game.Domino;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DominoPlayerEngine {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private List<Domino> hand = new ArrayList<>();
    private Board localBoard = new Board();

    public DominoPlayerEngine(String playerName, String skill, String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);

        // Envoyer les informations du joueur au serveur
        out.println(playerName + "|" + skill);
    }

    /**
     * Démarre la boucle principale pour gérer les messages du serveur.
     */
    public void start() {
        try {
            while (true) {
                String serverMessage = in.readLine();
                if (serverMessage == null) {
                    System.out.println("Connexion au serveur interrompue.");
                    break;
                }

                handleServerMessage(serverMessage);
            }
        } catch (IOException e) {
            System.err.println("Erreur de communication avec le serveur : " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    /**
     * Gère les messages reçus du serveur.
     */
    private void handleServerMessage(String message) throws IOException {
        if (message == null || message.isEmpty()) {
            System.out.println("Message vide ou invalide reçu.");
            return;
        }

        String[] parts = message.split(":", 2); // Sépare uniquement en deux parties
        String command = parts[0];
        String data = (parts.length > 1) ? parts[1] : ""; // Si pas de données, chaîne vide

        switch (command) {
            case "gameStarted":
                System.out.println("=> La partie commence !");
                break;

            case "initialHand":
                if (!data.isEmpty()) {
                    hand = parseHand(data);
                    System.out.println("Votre main : " + hand);
                } else {
                    System.out.println("Erreur : main initiale vide ou non reçue.");
                }
                break;

            case "boardState":
                if (!data.isEmpty()) {
                    localBoard = Board.fromString(data);
                    displayBoardState();
                } else {
                    System.out.println("Erreur : état du plateau vide ou non reçu.");
                }
                break;

            case "yourTurn":
                playTurn();
                break;

            case "dominoPlayed":
                if (!data.isEmpty()) {
                    System.out.println(data);
                } else {
                    System.out.println("Erreur : information sur le domino joué manquante.");
                }
                break;

            case "playerDrew":
                if (!data.isEmpty()) {
                    System.out.println(data);
                } else {
                    System.out.println("Erreur : information sur la pioche manquante.");
                }
                break;

            case "drawnDomino":
                if (!data.isEmpty()) {
                    Domino drawnDomino = Domino.fromString(data);
                    hand.add(drawnDomino);
                    System.out.println("Vous avez pioché : " + drawnDomino);
                } else {
                    System.out.println("Erreur : domino pioché manquant.");
                }
                break;

            case "playerBlocked":
                if (!data.isEmpty()) {
                    System.out.println(data);
                } else {
                    System.out.println("Erreur : information sur le blocage manquante.");
                }
                break;

            case "gameOver":
                if (!data.isEmpty()) {
                    System.out.println("=> " + data);
                    System.exit(0); // Fin de la partie
                } else {
                    System.out.println("Erreur : message de fin de partie mal formé.");
                }
                break;

            default:
                System.out.println("Message reçu : " + message);
                break;
        }
    }

    /**
     * Affiche l'état actuel du plateau.
     */
    private void displayBoardState() {
        System.out.println("\nPlateau de jeu actuel :");
        System.out.println(localBoard);
        System.out.println("========================");
    }

    /**
     * Parse la main du joueur à partir d'une chaîne envoyée par le serveur.
     */
    private List<Domino> parseHand(String handStr) {
        String[] dominos = handStr.replace("[", "").replace("]", "").split(", ");
        List<Domino> hand = new ArrayList<>();
        for (String d : dominos) {
            hand.add(Domino.fromString(d));
        }
        return hand;
    }

    /**
     * Permet au joueur de choisir un domino jouable.
     */
    private Domino chooseDominoToPlay() {
        for (Domino domino : hand) {
            if (localBoard.canPlaceDomino(domino)) {
                return domino; // Retourne le premier domino jouable
            }
        }
        return null; // Aucun domino jouable
    }

    /**
     * Effectue le tour du joueur.
     */
    private void playTurn() throws IOException {
        System.out.println("C'est votre tour !");
        Domino chosenDomino = chooseDominoToPlay();

        if (chosenDomino != null) {
            hand.remove(chosenDomino);
            out.println("playDomino:" + chosenDomino);
            System.out.println("Vous avez joué : " + chosenDomino);
        } else {
            System.out.println("Aucun domino jouable. Vous piochez...");
            out.println("draw");
        }
    }

    /**
     * Point d'entrée principal pour exécuter un joueur.
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Entrez votre nom : ");
            String name = scanner.nextLine();
            System.out.println("Entrez votre compétence (Aggressif, Défensif, Opportuniste) : ");
            String skill = scanner.nextLine();

            DominoPlayerEngine player = new DominoPlayerEngine(name, skill, "localhost", 12345);
            player.start();
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
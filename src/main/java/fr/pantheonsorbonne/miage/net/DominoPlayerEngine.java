package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.game.Domino;
import fr.pantheonsorbonne.miage.game.Board;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class DominoPlayerEngine {

    private final String playerName;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private List<Domino> hand = new ArrayList<>();
    private Board localBoard = new Board();

    public DominoPlayerEngine(String playerName, String skill, String host, int port) throws IOException {
        this.playerName = playerName;
        this.socket = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        out.println(playerName + "|" + skill); // Envoyer les informations du joueur
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

                if (serverMessage.startsWith("gameStarted")) {
                    System.out.println("=> La partie commence !");
                } else if (serverMessage.startsWith("initialHand")) {
                    hand = parseHand(serverMessage.split(":")[1]);
                    System.out.println("Votre main : " + hand);
                } else if (serverMessage.startsWith("boardState")) {
                    localBoard = Board.fromString(serverMessage.split(":")[1]);
                    displayBoardState();
                } else if (serverMessage.startsWith("yourTurn")) {
                    playTurn();
                } else if (serverMessage.startsWith("dominoPlayed")) {
                    System.out.println(serverMessage.split(":")[1]);
                } else if (serverMessage.startsWith("gameOver")) {
                    System.out.println("=> " + serverMessage.split(":")[1]);
                    break;
                } else {
                    System.out.println("Message inconnu reçu : " + serverMessage);
                }
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
    private void playTurn() {
        System.out.println("C'est votre tour !");
        Domino chosenDomino = chooseDominoToPlay();

        if (chosenDomino != null) {
            hand.remove(chosenDomino);
            out.println("playDomino:" + chosenDomino);
            System.out.println("Vous avez joué : " + chosenDomino);
        } else {
            System.out.println("Aucun domino jouable. Vous passez votre tour.");
            out.println("pass");
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
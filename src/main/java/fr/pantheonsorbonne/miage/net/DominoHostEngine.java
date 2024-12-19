package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.game.Board;
import fr.pantheonsorbonne.miage.game.Deck;
import fr.pantheonsorbonne.miage.game.Domino;
import fr.pantheonsorbonne.miage.game.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DominoHostEngine {

    private final Board board;
    private final Deck deck;
    private final Map<String, List<Domino>> playerHands;
    private final ServerSocket serverSocket;
    private final List<Player> players;
    private final List<Socket> playerSockets;

    public DominoHostEngine(int port) throws IOException {
        this.board = new Board();
        this.deck = new Deck();
        this.playerHands = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.players = new ArrayList<>();
        this.playerSockets = new ArrayList<>();
        System.out.println("Serveur démarré sur le port " + port);
    }

    public void start() {
        try {
            System.out.println("=> En attente de connexions des joueurs...");
            acceptPlayers(3);
            System.out.println("=> Tous les joueurs sont connectés. Démarrage de la partie !");
            distributeInitialHands();
            playGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptPlayers(int minPlayers) throws IOException {
        while (players.size() < minPlayers) {
            Socket socket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String playerInfo = in.readLine();
            if (playerInfo != null) {
                String[] info = playerInfo.split("\\|");
                String playerName = info[0];
                String skill = info[1];

                Player player = new Player(playerName, skill);
                players.add(player);
                playerSockets.add(socket);

                System.out.println("Joueur connecté : " + playerName + " (" + skill + ")");
                out.println("playerAccepted:Bienvenue " + playerName);
            }
        }
        broadcastMessage("gameStarted:La partie commence !");
    }

    private void distributeInitialHands() throws IOException {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Socket socket = playerSockets.get(i);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            List<Domino> hand = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                hand.add(deck.draw());
            }
            playerHands.put(player.getName(), hand);
            out.println("initialHand:" + hand);
        }
    }

    private void playGame() throws IOException {
        int currentPlayerIndex = 0;
        int consecutivePasses = 0; // Compteur pour détecter une partie bloquée

        while (!isGameOver(consecutivePasses)) {
            Player currentPlayer = players.get(currentPlayerIndex);
            Socket currentSocket = playerSockets.get(currentPlayerIndex);
            PrintWriter out = new PrintWriter(currentSocket.getOutputStream(), true);

            System.out.println("\nTour de : " + currentPlayer.getName());
            broadcastMessage("boardState:" + board);
            out.println("yourTurn");

            BufferedReader in = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
            String command = in.readLine();

            if (command != null && command.startsWith("playDomino")) {
                String dominoStr = command.split(":")[1];
                Domino domino = Domino.fromString(dominoStr);

                if (board.canPlaceDomino(domino)) {
                    board.placeDomino(domino, true);
                    playerHands.get(currentPlayer.getName()).remove(domino);
                    consecutivePasses = 0;

                    System.out.println(currentPlayer.getName() + " a joué : " + domino);
                    broadcastMessage("dominoPlayed:" + currentPlayer.getName() + " a joué : " + domino);
                } else {
                    out.println("invalidMove:Le coup est invalide. Réessayez.");
                }
                displayBoardState();
            } else if (command != null && command.equals("pass")) {
                consecutivePasses++;
                System.out.println(currentPlayer.getName() + " passe son tour.");
                broadcastMessage("playerPassed:" + currentPlayer.getName() + " passe son tour.");
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        endGame();
    }

    private boolean isGameOver(int consecutivePasses) {
        boolean allHandsEmpty = playerHands.values().stream().allMatch(List::isEmpty);
        boolean allDominoesPlayed = deck.getRemainingDominoes() == 0;

        // La partie se termine si toutes les mains sont vides ou si tous les joueurs passent leur tour
        return allHandsEmpty || (consecutivePasses >= players.size());
    }

    private void endGame() throws IOException {
        System.out.println("=> Fin de la partie !");
        broadcastMessage("gameOver:La partie est terminée !");

        Map<String, Integer> scores = calculateScores();
        String winner = scores.entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("Aucun");

        System.out.println("Le gagnant est : " + winner);
        broadcastMessage("gameOver:Le gagnant est : " + winner);

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + " a un total de " + entry.getValue() + " points.");
        }
    }

    private Map<String, Integer> calculateScores() {
        Map<String, Integer> scores = new HashMap<>();
        for (Player player : players) {
            int score = playerHands.get(player.getName()).stream()
                    .mapToInt(Domino::getTotal)
                    .sum();
            scores.put(player.getName(), score);
        }
        return scores;
    }

    private void displayBoardState() {
        System.out.println("\nPlateau de jeu actuel :");
        System.out.println(board);
        System.out.println("========================");
    }

    private void broadcastMessage(String message) throws IOException {
        for (Socket socket : playerSockets) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
        }
    }

    public static void main(String[] args) {
        try {
            DominoHostEngine hostEngine = new DominoHostEngine(12345);
            hostEngine.start();
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }
}
package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.game.*;

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
    private boolean skipNextPlayer = false;

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

        while (!isGameOver()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            Socket currentSocket = playerSockets.get(currentPlayerIndex);
            PrintWriter out = new PrintWriter(currentSocket.getOutputStream(), true);

            if (skipNextPlayer) {
                System.out.println(currentPlayer.getName() + " est bloqué et ne peut pas jouer !");
                broadcastMessage("playerBlocked:" + currentPlayer.getName() + " est bloqué !");
                skipNextPlayer = false;
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                continue;
            }

            out.println("yourTurn");
            broadcastMessage("boardState:" + board.toString());

            BufferedReader in = new BufferedReader(new InputStreamReader(currentSocket.getInputStream()));
            String command = in.readLine();

            if (command != null && command.startsWith("playDomino")) {
                String dominoStr = command.split(":")[1];
                Domino domino = Domino.fromString(dominoStr);

                if (board.canPlaceDomino(domino)) {
                    handleDominoPlay(currentPlayer, domino);
                    playerHands.get(currentPlayer.getName()).remove(domino);
                } else {
                    out.println("invalidMove:Ce coup n'est pas valide. Réessayez.");
                    continue;
                }
            } else if (command != null && command.equals("draw")) {
                handleDraw(currentPlayer, out);
                continue;
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        determineWinner();
    }

    private void handleDominoPlay(Player player, Domino domino) throws IOException {
        if (domino.getType().equals("Blocking")) {
            System.out.println(player.getName() + " a joué un domino bloquant !");
            skipNextPlayer = true;
        } else if (domino.getType().equals("Dynamic")) {
            domino.changeValues(board.getFirstValue(), board.getLastValue());
            System.out.println("Domino Dynamique modifié par " + player.getName() + " : " + domino);
        } else if (domino.getType().equals("Double Bonus")) {
            int sum = board.getFirstValue() + board.getLastValue();
            if (sum % 5 == 0) {
                System.out.println(player.getName() + " a joué un Double Bonus et reçoit un bonus !");
            }
        }

        board.placeDomino(domino, true);
        System.out.println(player.getName() + " a joué : " + domino);
        broadcastMessage("dominoPlayed:" + player.getName() + " a joué : " + domino);
    }

    private void handleDraw(Player player, PrintWriter out) throws IOException {
        if (deck.getRemainingDominoes() > 0) {
            Domino newDomino = deck.draw();
            playerHands.get(player.getName()).add(newDomino);
            out.println("drawnDomino:" + newDomino);
            broadcastMessage("playerDrew:" + player.getName() + " a pioché.");
        } else {
            out.println("noDraw:La pioche est vide.");
        }
    }

    private boolean isGameOver() {
        return playerHands.values().stream().allMatch(List::isEmpty) || deck.isEmpty();
    }

    private void determineWinner() throws IOException {
        Map<String, Integer> scores = new HashMap<>();
        for (Player player : players) {
            int score = playerHands.get(player.getName()).stream()
                    .mapToInt(Domino::getTotal)
                    .sum();
            scores.put(player.getName(), score);
        }

        Map.Entry<String, Integer> winner = scores.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);

        if (winner != null) {
            broadcastMessage("gameOver:" + winner.getKey() + " gagne avec " + winner.getValue() + " points !");
        }
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
            e.printStackTrace();
        }
    }
}
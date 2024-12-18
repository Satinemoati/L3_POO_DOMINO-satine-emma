package fr.pantheonsorbonne.miage.net;

import fr.pantheonsorbonne.miage.DominoNetworkEngine;
import fr.pantheonsorbonne.miage.HostFacade;
import fr.pantheonsorbonne.miage.game.Board;
import fr.pantheonsorbonne.miage.game.Deck;
import fr.pantheonsorbonne.miage.game.Domino;
import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

import java.util.*;

public class DominoHostEngine extends DominoNetworkEngine {

    private final Board board;
    private final Deck deck;
    private final Map<String, List<Domino>> playerHands;

    public DominoHostEngine(Game game, HostFacade facade) {
        super(game, facade);
        this.board = new Board();
        this.deck = new Deck();
        this.playerHands = new HashMap<>();
    }

    @Override
    public void start() {
        System.out.println("=> Démarrage du jeu en tant qu'hôte réseau...");
        deck.shuffle();
        sendCommandToAll("gameStarted", "La partie de Domino commence!");

        distributeInitialHands();
        playGame();
    }

    private void distributeInitialHands() {
        for (String player : game.getPlayers()) {
            List<Domino> hand = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                hand.add(deck.draw());
            }
            playerHands.put(player, hand);
            sendCommandToPlayer(player, "initialHand", hand.toString());
        }
    }

    private void playGame() {
        int consecutivePasses = 0;

        while (!isGameOver()) {
            for (String player : game.getPlayers()) {
                sendCommandToAll("boardState", board.toString());
                sendCommandToPlayer(player, "yourTurn", "");

                GameCommand command = receiveCommand();

                if (command == null) continue;

                if ("playDomino".equals(command.name())) {
                    handleDominoPlayed(player, command.body());
                    consecutivePasses = 0;
                } else if ("pass".equals(command.name())) {
                    consecutivePasses++;
                    sendCommandToAll("playerPassed", player);
                    if (consecutivePasses >= game.getPlayers().size()) {
                        declareGameBlocked();
                        return;
                    }
                }
            }
        }

        findAndDeclareWinner();
    }

    private void handleDominoPlayed(String player, String dominoStr) {
        Domino domino = Domino.fromString(dominoStr);
        List<Domino> playerHand = playerHands.get(player);

        if (playerHand != null && playerHand.contains(domino) && board.canPlaceDomino(domino)) {
            board.placeDomino(domino, true);
            playerHand.remove(domino);
            sendCommandToAll("dominoPlayed", player + ":" + domino);

            if (playerHand.isEmpty()) {
                declareWinner(player);
            }
        } else {
            sendCommandToPlayer(player, "invalidMove", "Coup invalide, réessayez.");
        }
    }

    private boolean isGameOver() {
        return playerHands.values().stream().allMatch(List::isEmpty) || deck.getRemainingDominoes() == 0;
    }

    private void declareWinner(String winner) {
        sendCommandToAll("gameOver", "Le gagnant est " + winner + "!");
    }

    private void declareGameBlocked() {
        sendCommandToAll("gameBlocked", "La partie est bloquée!");
        findAndDeclareWinner();
    }

    private void findAndDeclareWinner() {
        String winner = null;
        int minPoints = Integer.MAX_VALUE;

        for (Map.Entry<String, List<Domino>> entry : playerHands.entrySet()) {
            int points = entry.getValue().stream()
                    .mapToInt(domino -> domino.getLeftValue() + domino.getRightValue())
                    .sum();
            if (points < minPoints) {
                minPoints = points;
                winner = entry.getKey();
            }
        }

        declareWinner(winner);
    }
}
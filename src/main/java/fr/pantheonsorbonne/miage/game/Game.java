package fr.pantheonsorbonne.miage.game;

import java.util.List;

import fr.pantheonsorbonne.miage.game.Deck;
import fr.pantheonsorbonne.miage.game.Domino;

public final class Game {
    private Board board;
    private List<Player> players; 
    private Deck deck;
    private int currentPlayerIndex;

    public Game(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("La liste des joueurs ne peut pas être vide.");
        }

        this.players = players;
        this.deck = new Deck();
        this.board = new Board();
        giveStartingDominoes();
        this.currentPlayerIndex = players.indexOf(findFirstPlayer());
    }

    private Player findFirstPlayer() {
        for (Player player : players) {
            for (Domino d : player.getHand()) {
                if (d.getLeftValue() == 6 && d.getRightValue() == 6) {
                    return player;
                }
            }
        }

        for (int i = 5; i >= 0; i--) {
            for (Player player : players) {
                for (Domino d : player.getHand()) {
                    if (d.getLeftValue() == i && d.getRightValue() == i) {
                        return player;
                    }
                }
            }
        }

        return players.get(0);
    }

    private void giveStartingDominoes() {
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addDomino(deck.draw());
            }
        }
    }

    public void playTurn() {
        Player player = players.get(currentPlayerIndex);
        System.out.println("\n------------------------");
        System.out.println("Tour de " + player.getName());
        System.out.println("Dominos en main : " + player.getHand());
        System.out.println("Dominos restants dans la pioche : " + deck.getRemainingDominoes());

        boolean hasPlayed = tryToPlay(player);
        if (!hasPlayed) {
            System.out.println("Pas de domino jouable, tentative de pioche...");
            tryToDraw(player);
        }

        System.out.println(board);
        System.out.println("------------------------");
    }

    private boolean tryToPlay(Player player) {
        if (board.isEmpty()) {
            Domino highestDouble = player.getHighestDouble();
            if (highestDouble != null) {
                board.placeDomino(highestDouble, true);
                player.playDomino(highestDouble);
                System.out.println(player.getName() + " joue le premier domino : " + highestDouble);
                moveToNextPlayer();
                return true;
            }
            return false;
        }

        if (!player.canPlay(board)) {
            return false;
        }

        Domino domino = player.chooseDomino(board);
        if (domino == null) {
            return false;
        }

        boolean placeAtStart = chooseBestEnd(domino, board);
        if (board.placeDomino(domino, placeAtStart)) {
            player.playDomino(domino);
            System.out.println(player.getName() + " place " + domino + 
                (placeAtStart ? " à gauche" : " à droite"));
            moveToNextPlayer();
            return true;
        }
        return false;
    }

    private boolean chooseBestEnd(Domino domino, Board board) {
        int leftValue = board.getFirstValue();
        int rightValue = board.getLastValue();

        boolean fitsLeft = domino.getRightValue() == leftValue || domino.getLeftValue() == leftValue;
        boolean fitsRight = domino.getLeftValue() == rightValue || domino.getRightValue() == rightValue;

        if (fitsLeft && fitsRight) {
            return domino.getLeftValue() > domino.getRightValue();
        }

        return fitsLeft;
    }

    private void tryToDraw(Player player) {
        if (deck.getRemainingDominoes() == 0) {
            System.out.println("La pioche est vide, " + player.getName() + " passe son tour.");
            moveToNextPlayer();
            return;
        }

        Domino newDomino = deck.draw();
        player.addDomino(newDomino);
        System.out.println(player.getName() + " pioche " + newDomino);

        if (board.placeDomino(newDomino, chooseBestEnd(newDomino, board))) {
            player.playDomino(newDomino);
            System.out.println(player.getName() + " joue le domino pioché.");
        } else {
            System.out.println("Le domino pioché ne peut pas être joué.");
            moveToNextPlayer();
        }
    }

    private void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isGameOver() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                showWinner();
                return true;
            }
        }

        if (deck.getRemainingDominoes() == 0) {
            boolean canPlay = players.stream().anyMatch(p -> p.canPlay(board));
            if (!canPlay) {
                showWinner();
                return true;
            }
        }
        return false;
    }

    public void start() {
        System.out.println("\n=== 🎮 Début de la Partie ===");
        System.out.println("\n👥 Joueurs :");
        players.forEach(p -> System.out.println("- " + p.getName() + " (" + p.getSkill() + ")"));

        while (!isGameOver()) {
            playTurn();
        }
    }

    private void showWinner() {
        Player winner = null;
        int bestScore = Integer.MAX_VALUE;

        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                winner = player;
                break;
            }
            int score = player.calculateRemainingPoints();
            if (score < bestScore) {
                bestScore = score;
                winner = player;
            }
        }

        if (winner != null) {
            System.out.println("\n🏆 Le gagnant est " + winner.getName() + " !");
            players.forEach(p -> 
                System.out.println(p.getName() + ": " + p.calculateRemainingPoints() + " points"));
        }
    }
}

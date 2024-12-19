package fr.pantheonsorbonne.miage.game;

import java.util.List;

public final class Game {
    private final Board board;
    private final List<Player> players;
    private final Deck deck;
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

    private void giveStartingDominoes() {
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addDomino(deck.draw());
            }
        }
    }

    private Player findFirstPlayer() {
        Player startingPlayer = null;
        Domino highestDouble = null;

        for (Player player : players) {
            for (Domino domino : player.getHand()) {
                if (domino.isDouble()) {
                    if (highestDouble == null || domino.getLeftValue() > highestDouble.getLeftValue()) {
                        highestDouble = domino;
                        startingPlayer = player;
                    }
                }
            }
        }

        if (startingPlayer == null) {
            startingPlayer = players.get(0);
        }
        System.out.println("Le premier joueur est : " + startingPlayer.getName());
        return startingPlayer;
    }

    public void playTurn() {
        Player player = players.get(currentPlayerIndex);
        System.out.println("\n------------------------");
        System.out.println("Tour de " + player.getName() + " (" + player.getSkill() + ")");
        System.out.println("Dominos en main : " + player.getHand());
        System.out.println("Dominos restants dans la pioche : " + deck.getRemainingDominoes());

        if (!player.canPlay(board)) {
            System.out.println(player.getName() + " ne peut pas jouer.");
            if (!tryToDraw(player)) {
                System.out.println("La pioche est vide, " + player.getName() + " passe son tour.");
            }
        } else {
            Domino domino = player.chooseDomino(board);
            if (domino != null) {
                boolean placeAtStart = shouldPlaceAtStart(domino);
                handleSpecialDominoEffects(player, domino);
                board.placeDomino(domino, placeAtStart);
                player.playDomino(domino);
                System.out.println(player.getName() + " place " + domino + (placeAtStart ? " à gauche" : " à droite"));
            } else {
                System.out.println(player.getName() + " n'a pas de domino valide à jouer.");
            }
        }

        System.out.println(board);

        if (player.getHand().isEmpty()) {
            System.out.println("\n=== 🎉 " + player.getName() + " a gagné en jouant tous ses dominos ! ===");
            return;
        }

        moveToNextPlayer();
    }

    private void handleSpecialDominoEffects(Player player, Domino domino) {
        if (domino.getType().equals("Blocking")) {
            System.out.println("Domino Bloquant joué par " + player.getName() + "! Le joueur suivant est bloqué.");
            moveToNextPlayer(); 
        } else if (domino.getType().equals("Double Bonus")) {
            System.out.println("Double Bonus joué ! Vérifiez les points bonus.");
        }
    }

    private boolean shouldPlaceAtStart(Domino domino) {
        int firstValue = board.getFirstValue();
        int lastValue = board.getLastValue();
        return (domino.getLeftValue() == firstValue || domino.getRightValue() == firstValue) &&
               !(domino.getLeftValue() == lastValue || domino.getRightValue() == lastValue);
    }

    private boolean tryToDraw(Player player) {
        if (deck.getRemainingDominoes() == 0) return false;

        Domino newDomino = deck.draw();
        player.addDomino(newDomino);
        System.out.println(player.getName() + " pioche un domino : " + newDomino);
        return true;
    }

    private void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isGameOver() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                return true;
            }
        }
    
        boolean allPlayersBlocked = true;
        for (Player player : players) {
            if (player.canPlay(board)) {
                allPlayersBlocked = false;
                break;
            }
        }
    
        return deck.isEmpty() && allPlayersBlocked;
    }
    
    public void start() {
        System.out.println("\n=== 🎮 Début de la Partie ===");
        while (!isGameOver()) {
            playTurn();
        }
    
        System.out.println("\n=== 🏁 Fin de la Partie ===");
    
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                return;
            }
        }
    
        showResults();
    }
    
    private void showResults() {
        System.out.println("\n=== Résultats Finaux ===");
    
        Player winner = null;
        int lowestScore = Integer.MAX_VALUE;
    
        for (Player player : players) {
            int score = player.calculateRemainingPoints();
            if (score < lowestScore) {
                lowestScore = score;
                winner = player;
            }
        }
    
        if (winner != null) {
            System.out.println("\n=== 🎉 " + winner.getName() + " a gagné avec le moins de points restants ! ===");
        }
    
        for (Player player : players) {
            int score = player.calculateRemainingPoints();
            System.out.println(player.getName() + " a un total de " + score + " points.");
        }
    }
}
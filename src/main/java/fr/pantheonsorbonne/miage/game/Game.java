package fr.pantheonsorbonne.miage.game;

import java.util.List;

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
            for (Domino d : player.getHand()) {
                if (d.isDouble()) {
                    if (highestDouble == null || d.getLeftValue() > highestDouble.getLeftValue()) {
                        highestDouble = d;
                        startingPlayer = player;
                    }
                }
            }
        }
        if (startingPlayer == null) startingPlayer = players.get(0);
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
            System.out.println(player.getName() + " ne peut pas jouer et essaie de piocher...");
            if (!tryToDraw(player)) {
                System.out.println("La pioche est vide, " + player.getName() + " passe son tour.");
            }
        } else {
            Domino domino = player.chooseDomino(board);
            if (domino != null) {
                boolean placeAtStart = shouldPlaceAtStart(domino);
                board.placeDomino(domino, placeAtStart);
                player.playDomino(domino);
                System.out.println(player.getName() + " place " + domino + (placeAtStart ? " à gauche" : " à droite"));
            } else {
                System.out.println(player.getName() + " n'a pas de domino valide à jouer.");
            }
        }

        System.out.println(board);

        // Vérifier si le joueur a gagné en vidant sa main
        if (player.getHand().isEmpty()) {
            System.out.println("\n=== 🎉 " + player.getName() + " a gagné en jouant tous ses dominos ! ===");
            return;
        }

        moveToNextPlayer();
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
        // Vérifier si un joueur a vidé sa main
        if (players.stream().anyMatch(p -> p.getHand().isEmpty())) {
            return true;
        }

        // Vérifier si la pioche est vide et que tous les joueurs sont bloqués
        boolean allPlayersBlocked = players.stream().noneMatch(p -> p.canPlay(board));
        return deck.getRemainingDominoes() == 0 && allPlayersBlocked;
    }

    public void start() {
        System.out.println("\n=== 🎮 Début de la Partie ===");
        while (!isGameOver()) {
            playTurn();
        }

        System.out.println("\n=== 🏁 Fin de la Partie ===");

        // Vérifier si un joueur a gagné en vidant sa main
        if (players.stream().anyMatch(p -> p.getHand().isEmpty())) {
            return; // Le message de victoire a déjà été affiché dans playTurn()
        }

        // Sinon, afficher les résultats et déterminer le gagnant par points
        showResults();
    }

    private void showResults() {
        System.out.println("\n=== Résultats Finaux ===");
        Player winner = players.stream()
                .min((p1, p2) -> Integer.compare(p1.calculateRemainingPoints(), p2.calculateRemainingPoints()))
                .orElse(null);

        if (winner != null) {
            System.out.println("\n=== 🎉 " + winner.getName() + " a gagné avec le moins de points restants ! ===");
        }

        for (Player player : players) {
            int score = player.calculateRemainingPoints();
            System.out.println(player.getName() + " a un total de " + score + " points.");
        }
    }
}
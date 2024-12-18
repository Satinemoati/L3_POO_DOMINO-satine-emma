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
                boolean placeAtStart = shouldPlaceAtStart(domino);  // Dynamique : déterminer où jouer
                board.placeDomino(domino, placeAtStart);
                player.playDomino(domino);
                System.out.println(player.getName() + " place " + domino + (placeAtStart ? " à gauche" : " à droite"));
            } else {
                System.out.println(player.getName() + " n'a pas de domino valide à jouer.");
            }
        }

        System.out.println(board);
        moveToNextPlayer();
    }

    private boolean shouldPlaceAtStart(Domino domino) {
        // Logique pour déterminer si on place le domino à gauche ou à droite
        int firstValue = board.getFirstValue();
        int lastValue = board.getLastValue();
        // Préférer placer au début si possible, sinon à la fin
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
        if (players.stream().anyMatch(p -> p.getHand().isEmpty())) return true;
        if (deck.getRemainingDominoes() == 0 && players.stream().noneMatch(p -> p.canPlay(board))) return true;
        return false;
    }

    public void start() {
        System.out.println("\n=== 🎮 Début de la Partie ===");
        while (!isGameOver()) {
            playTurn();
        }
    
        System.out.println("\n=== 🏁 Fin de la Partie ===");
    
        // Afficher le vainqueur
        showWinner();
        System.out.println(); // Ligne vide pour la lisibilité
    
        // Afficher les résultats finaux
        showResults();
    }
    

    
    private void showWinner() {
        Player winner = null;
    
        // Vérifier si un joueur a terminé ses dominos
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                System.out.println(player.getName() + " a gagné en ayant terminé tous ses dominos !");
                return; // Arrêter la méthode immédiatement si un joueur a gagné
            }
        }
    
        // Si la pioche est vide, déterminer le gagnant par les points
        if (deck.getRemainingDominoes() == 0) {
            winner = players.stream()
                    .min((p1, p2) -> Integer.compare(p1.calculateRemainingPoints(), p2.calculateRemainingPoints()))
                    .orElse(null);
        }
    
        // Afficher le gagnant basé sur les points ou message par défaut
        if (winner != null) {
            System.out.println(winner.getName() + " a gagné avec un total de " 
                               + winner.calculateRemainingPoints() + " points !");
        } else {
            System.out.println("Aucun gagnant clair n'a pu être déterminé !");
        }
    }
    
    

    private void showResults() {
        System.out.println("\n=== Fin de la Partie ===");
        System.out.println("Résultats finaux :");

        for (Player player : players) {
            int score = player.calculateRemainingPoints();
            System.out.println("le joeur qui gagne la partie est "+ player.getName() + " avec : " + score + " points");
        }
    }
}
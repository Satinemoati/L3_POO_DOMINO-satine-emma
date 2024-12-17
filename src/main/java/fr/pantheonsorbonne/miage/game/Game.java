package fr.pantheonsorbonne.miage.game;

import java.util.List;

public final class Game {
    private Board board;
    private List<Player> players; 
    private Deck deck;
    private int currentPlayerIndex;

    public Game(List<Player> players) {
        this.players = players;
        this.deck = new Deck();
        this.board = new Board();
        giveStartingDominoes();
        this.currentPlayerIndex = players.indexOf(findFirstPlayer());
    }

    private Player findFirstPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException("Aucun joueur dans la partie");
        }
        
        // Chercher d'abord le joueur avec le double 6
        for (Player player : players) {
            for (Domino d : player.getHand()) {
                if (d.getLeftValue() == 6 && d.getRightValue() == 6) {
                    return player;
                }
            }
        }
        
        // Si personne n'a le double 6, chercher le double le plus élevé
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
        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                p.addDomino(deck.draw());
            }
        }
    }

    public void playTurn() {
        Player player = players.get(currentPlayerIndex);
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎮 Tour de " + player.getName());
        System.out.println("💫 Type de joueur : " + player.getSkill());
        System.out.println("🎲 Dominos en main : " + player.getHand());
        System.out.println("📦 Pioche : " + deck.getRemainingDominoes() + " dominos restants");
        
        boolean hasPlayed = tryToPlay(player);
        if (!hasPlayed) {
            System.out.println("⚠️ Pas de domino jouable, pioche nécessaire");
            tryToDraw(player);
        }
        
        System.out.println(board);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private boolean tryToPlay(Player player) {
        if (board.isEmpty()) {
            Domino highestDouble = null;
            for (Domino d : player.getHand()) {
                if (d.isDouble()) {
                    if (highestDouble == null || d.getLeftValue() > highestDouble.getLeftValue()) {
                        highestDouble = d;
                    }
                }
            }
            
            if (highestDouble != null) {
                player.playDomino(highestDouble);
                board.placeDomino(highestDouble, true);
                System.out.println("🎯 Premier coup : " + player.getName() + " joue " + highestDouble);
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
        if (placeDomino(domino, board)) {
            player.playDomino(domino);
            System.out.println("🎯 " + player.getName() + " place " + domino + 
                             (placeAtStart ? " à gauche" : " à droite"));

            if (domino.getType().equals("Blocking")) {
                System.out.println("🚫 Domino bloquant ! Le joueur suivant passe son tour");
                moveToNextPlayer();
            }

            if (!player.getSkill().equals("Opportunist") || !domino.isDouble()) {
                moveToNextPlayer();
            }
            return true;
        }
        return false;
    }

    private boolean placeDomino(Domino domino, Board board) {
        if (domino.getType().equals("Dynamic")) {
            int leftEnd = board.getFirstValue();
            int rightEnd = board.getLastValue();
            
            // Essayer de placer le domino dynamique avec ses valeurs actuelles
            if (domino.getRightValue() == leftEnd) {
                // On retourne le domino pour que les valeurs identiques soient côte à côte
                domino = new Domino(domino.getRightValue(), domino.getLeftValue(), "Dynamic");
                board.placeDomino(domino, true);
                return true;
            } else if (domino.getLeftValue() == rightEnd) {
                // Déjà dans le bon ordre pour que les valeurs identiques soient côte à côte
                board.placeDomino(domino, false);
                return true;
            }
            
            // Si pas possible, modifier les valeurs du domino dynamique
            if (domino.getLeftValue() == 0) {
                // On place le domino avec la valeur correspondante côté plateau
                domino = new Domino(domino.getRightValue(), leftEnd, "Dynamic");
                board.placeDomino(domino, true);
                return true;
            } else if (domino.getRightValue() == 0) {
                // On place le domino avec la valeur correspondante côté plateau
                domino = new Domino(rightEnd, domino.getLeftValue(), "Dynamic");
                board.placeDomino(domino, false);
                return true;
            }
        } else {
            // Logique pour les dominos normaux
            if (board.canPlaceDomino(domino)) {
                board.placeDomino(domino, chooseBestEnd(domino, board));
                return true;
            }
        }
        return false;
    }
    private boolean chooseBestEnd(Domino domino, Board board) {
        int leftMatch = 0;
        int rightMatch = 0;
        
        if (domino.getRightValue() == board.getFirstValue() || 
            domino.getLeftValue() == board.getFirstValue()) {
            leftMatch = domino.getLeftValue() + domino.getRightValue();
        }
        
        if (domino.getLeftValue() == board.getLastValue() || 
            domino.getRightValue() == board.getLastValue()) {
            rightMatch = domino.getLeftValue() + domino.getRightValue();
        }
        
        return leftMatch >= rightMatch;
    }

    private void tryToDraw(Player player) {
        if (deck.getRemainingDominoes() == 0) {
            System.out.println("📦 La pioche est vide, " + player.getName() + " passe son tour");
            moveToNextPlayer();
            return;
        }

        Domino newDomino = deck.draw();
        player.addDomino(newDomino);
        System.out.println("📦 " + player.getName() + " pioche " + newDomino);
        
        if (placeDomino(newDomino, board)) {
            player.playDomino(newDomino);
            System.out.println("✅ Le domino pioché a été joué");
        } else {
            System.out.println("❌ Le domino pioché ne peut pas être joué");
            moveToNextPlayer();
        }
    }

    private void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isGameOver() {
        for (Player p : players) {
            if (p.getHand().isEmpty()) {
                showWinner();
                return true;
            }
        }

        if (deck.getRemainingDominoes() == 0) {
            boolean canAnyonePlay = false;
            for (Player p : players) {
                if (p.canPlay(board)) {
                    canAnyonePlay = true;
                    break;
                }
            }
            if (!canAnyonePlay) {
                showWinner();
                return true;
            }
        }
        return false;
    }

    public void start() {
        System.out.println("\n=== 🎮 DÉBUT DE LA PARTIE ===");
        System.out.println("\n👥 Joueurs participants :");
        for (Player p : players) {
            System.out.println("- " + p.getName() + " (" + p.getSkill() + ")");
        }
        
        while (!isGameOver()) {
            playTurn();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void showWinner() {
        Player winner = null;
        int bestScore = Integer.MAX_VALUE;

        for (Player p : players) {
            if (p.getHand().isEmpty()) {
                winner = p;
                break;
            }
            int score = p.calculateRemainingPoints();
            if (score < bestScore) {
                bestScore = score;
                winner = p;
            }
        }

        if (winner != null) {
            System.out.println("\n🏆 Le gagnant est " + winner.getName() + " !");
            for (Player p : players) {
                System.out.println(p.getName() + ": " + p.calculateRemainingPoints() + " points");
            }
        }
    }
}
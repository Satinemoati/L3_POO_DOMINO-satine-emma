package fr.pantheonsorbonne.miage.game;


import java.util.List;

public final class Game {
    private final Board board;
    private final List<Player> players;
    private final Deck deck;
    private int currentPlayerIndex;

    public Game(List<Player> players) {
        this.players = players;
        this.deck = new Deck();
        this.board = new Board();
        this.currentPlayerIndex = 0;
        distributeDominos();
    }

    private void distributeDominos() {
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addDomino(deck.draw());
            }
        }
    }

    public void playTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);

        if (currentPlayer.canPlay(board)) {
            Domino dominoToPlay = currentPlayer.chooseDomino(board);
            currentPlayer.playDomino(dominoToPlay);
            board.placeDomino(dominoToPlay, true);
        } else {
            while (!deck.getDominos().isEmpty() && !currentPlayer.canPlay(board)) {
                currentPlayer.addDomino(deck.draw());
            }
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isGameOver() {
        return players.stream().anyMatch(player -> player.getHand().isEmpty()) || deck.getDominos().isEmpty();
    }

    public void start() {
        while (!isGameOver()) {
            playTurn();
        }
        System.out.println("La partie est terminée !");
    }
}
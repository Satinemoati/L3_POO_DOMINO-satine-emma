package fr.pantheonsorbonne.miage.game;
import java.util.HashSet;
import java.util.Set;

public final class Game {

    public static enum GameState {
        CREATED, IN_PROGRESS, COMPLETED
    }

    private String gameId;
    private String gameName;
    
    private Set<String> players;
    private GameState state;
    private Deck deck;

    //C
    public Game(String gameId, /*String gameName*/ HashSet<String> players, GameState state) {
        this.gameId = gameId;
        //this.gameName = gameName; regles supp maybe
       
        this.players = players;
        this.state = state;
        this.deck = new Deck(); // initialise la pioche qd le jeu est lancé
    }

    //gs
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Set<String> getPlayers() {
        return players;
    }

    public void setPlayers(Set<String> players) {
        this.players = new HashSet<>(players);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Deck getDeck() {
        return deck;
    }

    public void startGame() { //Methode pr commencer le jeu
        if (state != GameState.CREATED) {
            throw new IllegalStateException("Le jeu a deja commencé ou est terminé");
        }
        state = GameState.IN_PROGRESS;
    }

    public void endGame(){ //methode pr finir le jeu
        state = GameState.COMPLETED;
    }

    @Override
    public String toString() {
        return "Game{" +
               "gameId='" + gameId + '\'' +
               ", gameName='" + gameName + '\'' +
               ", players=" + players +
               ", state=" + state +
               ", deck=" + deck +
               '}';
    }
    
}

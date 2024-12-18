package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

/**
 * Classe de base pour la gestion des commandes réseau liées au jeu du domino.
 * Cette classe est utilisée à la fois par l'hôte et les joueurs pour envoyer et recevoir des commandes.
 */
public abstract class DominoNetworkEngine {
    
    protected final Game game;  // Le jeu en cours
    protected final Facade facade;  // La façade qui gère la communication réseau

    /**
     * Constructeur de la classe DominoNetworkEngine.
     * @param game Le jeu en cours
     * @param facade La façade permettant d'envoyer et recevoir des commandes réseau
     */
    public DominoNetworkEngine(Game game, Facade facade) {
        this.game = game;
        this.facade = facade;
    }

    /**
     * Méthode abstraite pour démarrer la partie.
     * Cette méthode doit être implémentée par les classes dérivées (comme l'hôte ou le joueur).
     */
    public abstract void start();

    /**
     * Méthode pour recevoir une commande réseau d'un joueur ou de l'hôte.
     * @return La commande reçue sous forme d'objet GameCommand.
     */
    protected GameCommand receiveCommand() {
        try {
            return facade.receiveGameCommand(game);
        } catch (Exception e) {
            System.err.println("Erreur lors de la réception de la commande: " + e.getMessage());
            return null;
        }
    }

    /**
     * Méthode pour envoyer une commande à un joueur spécifique.
     * @param playerName Le nom du joueur auquel la commande est envoyée
     * @param command Le nom de la commande (ex : "playDomino")
     * @param body Le contenu de la commande (ex : "D[1|2]")
     */
    protected void sendCommandToPlayer(String playerName, String command, String body) {
        try {
            GameCommand gameCommand = new GameCommand(command, body);
            facade.sendGameCommandToPlayer(game, playerName, gameCommand);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la commande à " + playerName + ": " + e.getMessage());
        }
    }

    /**
     * Méthode pour envoyer une commande à tous les joueurs du jeu.
     * @param command Le nom de la commande (ex : "gameStarted")
     * @param body Le contenu de la commande (ex : "La partie a commencé!")
     */
    protected void sendCommandToAll(String command, String body) {
        try {
            GameCommand gameCommand = new GameCommand(command, body);
            facade.sendGameCommandToAll(game, gameCommand);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la commande à tous les joueurs: " + e.getMessage());
        }
    }
}

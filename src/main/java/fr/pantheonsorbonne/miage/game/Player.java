package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;

public class Player {
    private String name;
    private String skill;
    private ArrayList<Domino> hand;

    public Player(String name, String skill) {
        this.name = name;
        this.skill = skill;
        this.hand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getSkill() {
        return skill;
    }

    public void addDomino(Domino domino) {
        hand.add(domino);
    }

    public void playDomino(Domino domino) {
        hand.remove(domino);
    }

    public ArrayList<Domino> getHand() {
        return hand;
    }

    public boolean canPlay(Board board) {
        return hand.stream().anyMatch(board::canPlaceDomino);
    }

    public Domino chooseDomino(Board board) {
        return hand.stream().filter(board::canPlaceDomino).findFirst().orElse(null);
    }

    public int calculateRemainingPoints() {
        return hand.stream().mapToInt(Domino::getTotal).sum();
    }
}
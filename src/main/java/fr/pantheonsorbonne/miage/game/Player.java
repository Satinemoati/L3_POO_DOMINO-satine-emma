package fr.pantheonsorbonne.miage.game;

import java.util.ArrayList;
import java.util.List;

public final class Player {
    private final String name;
    private final List<Domino> hand;
    private final String skill;

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

    public List<Domino> getHand() {
        return hand;
    }

    public void addDomino(Domino domino) {
        hand.add(domino);
    }

    public void playDomino(Domino domino) {
        hand.remove(domino);
    }

    public boolean canPlay(Board board) {
        return hand.stream().anyMatch(board::canPlaceDomino);
    }

    public Domino chooseDomino(Board board) {
        return hand.stream().filter(board::canPlaceDomino).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Player{" + "name='" + name + '\'' + ", hand=" + hand + ", skill='" + skill + '\'' + '}';
    }
}
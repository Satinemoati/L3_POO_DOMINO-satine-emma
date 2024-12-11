package fr.pantheonsorbonne.miage.game;

public class Domino {
    private int leftValue;//gauche
    private int rightValue;//droite

    public Domino(int leftValue, int rightValue){
        this.leftValue = leftValue; //partie gauche ex : tir gauche =2 
        this.rightValue = rightValue; //ex : dr = 1
    }
    public int getLeftValue() {
        return leftValue;
    }

    public int getRightValue() {
        return rightValue;
    }

    @Override
    public String toString() {
        return "[" + leftValue + "|" + rightValue + "]"; //Separer val gauche et droite
    }

    @Override
    public boolean equals(Object o) { // comparer les vals de droite et gauche avec autre
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domino domino = (Domino) o;
        return leftValue == domino.leftValue && rightValue == domino.rightValue;
    }

    @Override
    public int hashCode() { // left val gauche et right val a dr 
        int result = Integer.hashCode(leftValue);  // Utilise Integer.hashCode pour les entiers
        result = 31 * result + Integer.hashCode(rightValue);  // Fais de même pour rightValue
        return result;    
    }

}

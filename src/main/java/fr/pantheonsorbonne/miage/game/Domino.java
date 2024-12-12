package fr.pantheonsorbonne.miage.game;

public class Domino {
    private int leftValue;
    private int rightValue;
    private final String type;

    public Domino(int leftValue, int rightValue, String type) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.type = type;
    }

    public int getLeftValue() {
        return leftValue;
    }

    public int getRightValue() {
        return rightValue;
    }

    public String getType() {
        return type;
    }

    public void changeValue(int newLeft, int newRight) {
        if ("Dynamique".equals(type)) {
            this.leftValue = newLeft;
            this.rightValue = newRight;
        }
    }

    @Override
    public String toString() {
        return "[" + leftValue + "|" + rightValue + "] (" + type + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domino domino = (Domino) o;
        return leftValue == domino.leftValue && rightValue == domino.rightValue;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(leftValue);
        result = 31 * result + Integer.hashCode(rightValue);
        return result;
    }
}
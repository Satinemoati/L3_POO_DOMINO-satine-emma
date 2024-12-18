package fr.pantheonsorbonne.miage.game;

public class Domino {
    private int leftValue;
    private int rightValue;
    private final String type;
    private boolean effectUsed;

    public Domino(int leftValue, int rightValue, String type) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.type = type;
        this.effectUsed = false;
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

    public boolean isEffectUsed() {
        return effectUsed;
    }

    public void changeValues(int newLeft, int newRight) {
        if (type.equals("Dynamic") && !effectUsed) {
            leftValue = newLeft;
            rightValue = newRight;
            effectUsed = true;
            System.out.println("Domino modifié: [" + leftValue + "|" + rightValue + "]");
        }
    }

    public boolean isDouble() {
        return leftValue == rightValue;
    }

    public int getTotal() {
        return leftValue + rightValue;
    }

    @Override
    public String toString() {
        String prefix = "";
        if (type.equals("Dynamic")) prefix = "D";
        if (type.equals("Blocking")) prefix = "B"; 
        if (type.equals("Double Bonus")) prefix = "*";
        
        if (isDouble()) {
            return prefix + "[" + leftValue + "|" + rightValue + "]";
        }
        return prefix + "[" + leftValue + "|" + rightValue + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Domino)) return false;
        Domino domino = (Domino) other;
        return leftValue == domino.leftValue && 
               rightValue == domino.rightValue &&
               type.equals(domino.type);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * leftValue + rightValue) + type.hashCode();
    }
}
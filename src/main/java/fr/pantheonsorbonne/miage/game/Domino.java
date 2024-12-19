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
            System.out.println("Domino Dynamique modifié: [" + leftValue + "|" + rightValue + "]");
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
        return prefix + "[" + leftValue + "|" + rightValue + "]";
    }

    public static Domino fromString(String str) {
        str = str.replaceAll("[^0-9|]", "");
        String[] values = str.split("\\|");
        if (values.length == 2) {
            try {
                int leftValue = Integer.parseInt(values[0]);
                int rightValue = Integer.parseInt(values[1]);
                return new Domino(leftValue, rightValue, "Standard");
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Erreur de format pour le domino : " + str, e);
            }
        }
        throw new IllegalArgumentException("Format de domino invalide : " + str);
    }
}
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
        
        return prefix + "[" + leftValue + "|" + rightValue + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) 
            return true;
        if (!(other instanceof Domino))
            return false; 
        Domino otherDomino = (Domino) other;
        return this.leftValue == otherDomino.leftValue && this.rightValue == otherDomino.rightValue;
    }

    /**
     * Convertit une chaîne en objet Domino.
     * Exemple d'entrée : "[2|4]" ou "2|4"
     */
    public static Domino fromString(String str) {
        str = str.replaceAll("[^0-9|]", ""); // Nettoyer les caractères non numériques ou le séparateur "|"
        String[] values = str.split("\\|");
        if (values.length == 2) {
            try {
                int leftValue = Integer.parseInt(values[0]);
                int rightValue = Integer.parseInt(values[1]);
                return new Domino(leftValue, rightValue, "Normal");
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Erreur de format pour le domino : " + str, e);
            }
        }
        throw new IllegalArgumentException("Format de domino invalide : " + str);
    }
}
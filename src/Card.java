public class Card {

    private int value;
    private String suit;

    public Card(int value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        switch (getValue()) {
            case 11:
                return "J of " + getSuit();
            case 12:
                return "Q of " + getSuit();
            case 13:
                return "K of " + getSuit();
            case 14:
                return "A of " + getSuit();

            default:
                return getValue() + " of " + getSuit();
        }
    }
}

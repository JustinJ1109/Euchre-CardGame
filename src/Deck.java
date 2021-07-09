import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private ArrayList<Card> deck;
    private String[] suits = {"Spades", "Clubs", "Diamonds", "Hearts"};
    private int deckMin, deckMax;

    public Deck(GameName game) {
        deck = new ArrayList<>(0);

        switch (game) {
            case Euchre:
                deckMin = 9;
                deckMax = 14;
                break;
            default:
                deckMin = 2;
                deckMax = 14;
        }
        populateDeck(deckMin, deckMax, "all");
    }

    public void reshuffleDeck() {
        deck.clear();
        populateDeck(deckMin, deckMax, "all");
        Shuffle();
    }

    private void populateDeck(int min, int max, String suit) {

        for (int s = 0 ; s < suits.length; s++)
            if (suits[s].equals(suit) || suit.equalsIgnoreCase("all")) {
                for (int i = min; i <= max; i++) {
                    deck.add(new Card(i, suits[s]));
                }
            }
    }

    public void Shuffle() {
        Collections.shuffle(deck);
        Collections.shuffle(deck);
    }

    public Card drawTop() {
        Card drawCard = deck.get(0);
        deck.remove(0);
        return drawCard;
    }

    public ArrayList<Card> getCardsInDeck() {
        return deck;
    }

    public enum GameName {
        Euchre;
    }
}

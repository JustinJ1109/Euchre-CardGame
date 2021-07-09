import java.util.ArrayList;

public class playerHand {

    private ArrayList<Card> hand;
    public playerHand() {
        hand = new ArrayList<>(0);
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public void removeCardFromHand(Card card) {
        hand.remove(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
}

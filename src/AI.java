import java.util.ArrayList;

public class AI {

    private GameRules gr;
    private TurnController tc;

    public AI(GameRules gr, TurnController tc) {
        this.gr = gr;
        this.tc = tc;
    }

    /**
     * Logic to determine whether the player should pick up the card.
     *
     * @param card
     * @return
     */
    public boolean wantsToPickUpCard(Card card, ArrayList<Card> hand, boolean isSelfOrPartner, boolean canPass) {
        // if has 3 cards at least 10 of card suit, pickup card

        if (card.getSuit().equals(chooseTrumpSuit(canPass, hand))) {
            return true;
        }

        int numOfSuit = 0;

        // if isSelfOrPartner, add that card to hand
        for (int i = 0; i < hand.size(); i++) {
            Card tempCard = hand.get(i);

            // if on-suit
            if (tempCard.getSuit().equals(card.getSuit()))
                // if card is > 9
                numOfSuit++;
        }
        // if card will go to team
        if (isSelfOrPartner) {
            numOfSuit++;
        }

        if (numOfSuit > 3) {
            return true;
        }

        return false;

        //TODO:
        // if player would choose suit of drawn card anyways, and card goes to team, return true
    }



    /**
     * Logic to determine what, if any, suit the trump should be.
     * If they don't have a good hand, will pass.
     * If unable to pass, will choose best available.
     */
    public String chooseTrumpSuit(boolean canPass, ArrayList<Card> hand) {
        int[] numSuits = new int[]{0, 0, 0, 0};
        // diamonds, hearts, clubs, spades
        String[] suits = new String[]{"Diamonds", "Hearts", "Clubs", "Spades"};

        for (Card c : hand) {
            switch (c.getSuit()) {
                case "Diamonds":
                    if (c.getValue() == 11)
                        numSuits[1]++;
                    numSuits[0]++;
                    break;
                case "Hearts":
                    if (c.getValue() == 11)
                        numSuits[0]++;
                    numSuits[1]++;
                    break;
                case "Clubs":
                    if (c.getValue() == 11)
                        numSuits[3]++;
                    numSuits[2]++;
                    break;
                case "Spades":
                    if (c.getValue() == 11)
                        numSuits[2]++;
                    numSuits[3]++;
            }
        }

        int max = 0;
        for (int i = 1; i < numSuits.length; i++) {
            if (numSuits[i] < numSuits[max])
                max = i;
        }

        if (numSuits[max] > 3)
            return suits[max];
        else if (numSuits[max] <= 3)
            for (Card c : hand)
                // if they have 3 of a suit, and they are the 3 highest of that suit, they will choose that as trump
                if (c.getValue() <= 11)
                    if (canPass)
                        return "pass";


        return suits[max];
    }


    public void doCardSwap(Card card, ArrayList<Card> hand) {
        Card min = hand.get(0);
        int indexOfMin = 0;
        for (int i = 0; i < hand.size(); i++) {
            Card tempCard = hand.get(i);
            // card in hand is off-suit
            if (!tempCard.getSuit().equals(card.getSuit())) {
                // if Left or Bower, don't discard
                if (gr.isLeft(tempCard) || gr.isBower(tempCard))
                    continue;
                // get min of off-suit
                if (tempCard.getValue() < min.getValue()) {
                    min = tempCard;
                    indexOfMin = i;
                }
            }
        }
        // all are on-suit, find lowest
        if (indexOfMin == 0) {
            for (int i = 0; i < hand.size(); i++) {
                Card tempCard = hand.get(i);
                if (tempCard.getValue() < min.getValue()) {
                    min = tempCard;
                    indexOfMin = i;
                }
            }
        }
        hand.set(indexOfMin, card);
    }

    /**
     * Plays the best available card. return card played
     *
     * @return the card played
     */
    public Card playCard(ArrayList<Card> hand) {
        // if no cards have been played, play highest off-suit
        if (gr.getInPlay().size() == 0) {
            // highest off-suit
            Card highest = hand.get(0);
            for (Card c : hand) {
                if (!gr.isLeft(c) && !c.getSuit().equals(gr.getTrumpSuit()))
                    if (c.getValue() > highest.getValue())
                        highest = c;
            }
            return highest;
        }
        // check teammates hand
        if (gr.getInPlay().size() >= 2) {
            Card lowestTrick = null;
            Card lowestOff = null;
            Card lowestTrump = null;
            Card highestTrick = null;
            if (teammateIsWinning()) {
                for (Card c : hand) {
                    // teammate is winning, play lowest trick-suit
                    // if no tricksuit, play lowest offsuit
                    // if no offsuit, play lowest trump

                    // trick-suit
                    if (c.getSuit().equals(gr.getTrickSuit())) {
                        if (lowestTrick == null)
                            lowestTrick = c;
                        else if (c.getValue() < lowestTrick.getValue())
                            lowestTrick = c;
                    }
                    // offsuit
                    else if (!c.getSuit().equals(gr.getTrumpSuit())) {
                        if (lowestOff == null)
                            lowestOff = c;
                        else if (c.getValue() < lowestOff.getValue())
                            lowestOff = c;
                    }
                    // trump suit
                    else {
                        if (lowestTrump == null)
                            lowestTrump = c;
                        else if (c.getValue() < lowestTrump.getValue())
                            lowestTrump = c;
                    }
                }

                if (lowestTrick == null) {
                    if (lowestOff == null) {
                        return lowestTrump;
                    }
                    return lowestOff;
                }
                return lowestTrick;
            }
            // teammate not winning
            else {
                for (Card c : hand) {
                    if (c.getSuit().equals(gr.getTrickSuit()))
                        if (highestTrick == null)
                            highestTrick = c;
                        else if (c.getValue() > highestTrick.getValue())
                            highestTrick = c;
                    if (c.getSuit().equals(gr.getTrumpSuit()))
                        if (lowestTrump == null)
                            lowestTrump = c;
                        else if (c.getValue() < lowestTrump.getValue())
                            lowestTrump = c;
                    if (lowestOff == null)
                        lowestOff = c;
                    else if (c.getValue() < lowestOff.getValue())
                        lowestOff = c;
                }
            }

            if (highestTrick == null) {
                if (lowestTrump == null)
                    return lowestOff;
                return lowestTrump;
            }
            return highestTrick;
        }

        // teammate has not gone yet
        else {
            Card minGreaterTrick = null;
            Card lowestTrick = null;
            Card lowestTrump = null;
            Card lowestOff = null;

            for (Card c : hand) {
                // play minimum trick-suit > what first player played
                // if none, lowest trick

                if (c.getSuit().equals(gr.getTrickSuit())) {
                    // minimum trick to win
                    if (c.getValue() > gr.getInPlay().get(0).getValue() && (minGreaterTrick == null || c.getValue() < minGreaterTrick.getValue())) {
                        minGreaterTrick = c;
                    }
                    // lowest trick
                    if (lowestTrick == null || c.getValue() < lowestTrick.getValue())
                        lowestTrick = c;
                }

                // lowest trump
                else if (c.getSuit().equals(gr.getTrumpSuit())) {
                    if (lowestTrump == null || c.getValue() < lowestTrump.getValue())
                        lowestTrump = c;
                }

                else {
                    if (lowestOff == null || c.getValue() < lowestOff.getValue())
                        lowestOff = c;
                }
            }

            // minimum trick -> lowest trick -> lowest trump -> lowest off
            if (minGreaterTrick == null) {
                if (lowestTrick == null) {
                    if (lowestTrump == null) {
                        return lowestOff;
                    }
                    return lowestTrump;
                }
                return lowestTrick;
            }
            return minGreaterTrick;

        }
    }

    private boolean teammateIsWinning() {
        if (gr.getInPlay().size() == 2) {
            // teammate has trumpsuit
            if (gr.getInPlay().get(0).getSuit().equals(gr.getTrumpSuit())) {
                // opponent doesnt
                if (!gr.getInPlay().get(1).getSuit().equals(gr.getTrumpSuit()))
                    return true;
                    // opponent also has trumpsuit, which is bigger
                else return gr.getInPlay().get(0).getValue() > gr.getInPlay().get(1).getValue();
            }

            // teammate has tricksuit
            else if (gr.getInPlay().get(0).getSuit().equals(gr.getTrickSuit())) {
                if (gr.getInPlay().get(1).getSuit().equals(gr.getTrickSuit())) {
                    return gr.getInPlay().get(0).getValue() > gr.getInPlay().get(1).getValue();
                }
            }
        }

        if (gr.getInPlay().size() == 3) {
            // if player 1 or 3 are trump suit and 2 is not, return false
            Card p1Play = gr.getInPlay().get(0);
            Card p2Play = gr.getInPlay().get(1);
            Card p3Play = gr.getInPlay().get(2);

            if ((p1Play.getSuit().equals(gr.getTrumpSuit()) || p3Play.getSuit().equals(gr.getTrumpSuit())) && !p2Play.getSuit().equals(gr.getTrumpSuit()))
                return false;
            // if player 1 or 3 are trick suit and 2 is trick,
            if ((p1Play.getSuit().equals(gr.getTrickSuit()) || p3Play.getSuit().equals(gr.getTrickSuit())) && p2Play.getSuit().equals(gr.getTrickSuit()))
                return p1Play.getValue() <= p2Play.getValue() && p3Play.getValue() <= p2Play.getValue();
            // if player 1 or 3 are bigger than 2 return false

            // if player 1 or 3 are trick suit and 2 is trump, return true
        }
        return true;
    }
}

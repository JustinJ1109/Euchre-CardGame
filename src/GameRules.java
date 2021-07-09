import java.util.ArrayList;

public class GameRules {

    private String trickSuit, trumpSuit;
    private final TurnController tc;
    private ArrayList<Card> inPlay;
    private final String[] suits;

    private static int team1Score, team2Score;

    private int teamChoseTrump;

    public GameRules(TurnController tc) {
        this.tc = tc;
        trumpSuit = "pass";
        trickSuit = "none";
        inPlay = new ArrayList<>(0);

        team1Score = 0;
        team2Score = 0;
        teamChoseTrump = -1;

        suits = new String[] {"Hearts", "Diamonds", "Clubs", "Spades"};
    }

    public String[] getSuits() {
        return suits;
    }

    public int getTeamChoseTrump() {
        return teamChoseTrump;
    }

    public void setTeamChoseTrump(int team) {
        teamChoseTrump = team;
    }

    public void addInPlay(Card card) {
        inPlay.add(card);
    }

    public void clearInPlay() {
        inPlay.clear();
    }

    public ArrayList<Card> getInPlay() {
        return inPlay;
    }

    public void setTrickSuit(String trickSuit) {
        this.trickSuit = trickSuit;
    }

    public void setTrumpSuit(String trumpSuit) {
        this.trumpSuit = trumpSuit;
    }

    public String getTrickSuit() {
        return trickSuit;
    }

    public String getTrumpSuit() {
        return trumpSuit;
    }

    public boolean isPlayable(Card card, ArrayList<Card> hand) {

        if (card.getSuit().equals(getTrickSuit()))
            return true;

        for (Card c : hand) {
            if (!c.equals(card))
                if (c.getSuit().equals(getTrickSuit()) || isLeft(c))
                    if (tc.getTrickStartingPlayer() != tc.getCurrentPlayerTurn())
                        return false;
        }
        return true;
    }

    public int determineWinner() {

        int indexOfMax = 0;
        Card maxCard = inPlay.get(0);

        boolean sawLeft = false;
        boolean sawTrump = false;

        for (int i = 0; i < inPlay.size(); i++) {
            Card tempCard = inPlay.get(i);
            // is winner if
            // card is right bower
            // card is left bower
            // card is highest trump
            // card is higher trick

            // is right bower
            if (isBower(tempCard)) {
                indexOfMax = i;
                break;
            }
            // is left bower
            if (isLeft(tempCard)) {
                indexOfMax = i;
                sawLeft = true;
            }
            // left has not been seen, highest would be trump if present
            else if (!sawLeft && tempCard.getSuit().equals(getTrumpSuit())) {
                if (!getTrumpSuit().equals(getTrickSuit()) && maxCard.getSuit().equals(getTrickSuit())) {
                    maxCard = tempCard;
                    indexOfMax = i;
                    sawTrump = true;
                }
                if (tempCard.getValue() > maxCard.getValue()) {
                    maxCard = tempCard;
                    indexOfMax = i;
                    sawTrump = true;
                }

            }
            // left and trump has not been seen
            else if (!sawLeft && !sawTrump && tempCard.getSuit().equals(getTrickSuit())) {
                if (tempCard.getValue() > maxCard.getValue()) {
                    maxCard = tempCard;
                    indexOfMax = i;

                }
            }
        }

        int winningPlayer = (indexOfMax + tc.getTrickStartingPlayer()) % 4;
        if (winningPlayer == 0)
            winningPlayer = 4;

        return winningPlayer;

        //FIXME:
        // Trump: heart
        // Trick: heart
        // 10 heart beat K Heart?
    }

    public String getSisterSuit(String suit) {
        switch (suit) {
            case "Diamonds":
                return "Hearts";
            case "Hearts":
                return "Diamonds";
            case "Spades":
                return "Clubs";
            case "Clubs":
                return "Spades";
        }
        return "err";
    }

    public boolean isLeft(Card card) {
        return card.getValue() == 11 && getTrumpSuit().equals(getSisterSuit(card.getSuit()));
    }

    public boolean isBower(Card card) {
        return card.getValue() == 11 && getTrumpSuit().equals(card.getSuit());
    }

    public void addScore(int team1Tricks, int team2Tricks) {
        // team 1 chose trump
        //FIXME: not registering euchres
        if (getTeamChoseTrump() == 1) {
            // team 1 was euchred
            if (team1Tricks < team2Tricks) {
                System.out.println("Team 1 was euchred!");
                team2Score += 2;
                return;
            }
        }
        // team 2 chose trump
        else {
            // team 2 was euchred
            if (team2Tricks < team1Tricks) {
                System.out.println("Team 2 was euchred!");
                team1Score += 2;
                return;
            }
        }

        if (team1Tricks == 5) {
            team1Score += 2;
        } else if (team2Tricks == 5) {
            team2Score += 2;
        } else if (team1Tricks > team2Tricks)
            team1Score += 1;
        else
            team2Score += 1;

    }

    public void printScore() {
        System.out.println("-----------------------");
        System.out.println("\tTeam 1 : " + team1Score);
        System.out.println("\tTeam 2 : " + team2Score);
        System.out.println("-----------------------\n");
    }

}

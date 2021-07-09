import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Game {

    private Deck deck;
    private Scanner sc;

    private playerHand[] playerHands;
    TurnController tc;
    private AI ai;

    private int team1Score, team2Score;
    private int team1Tricks, team2Tricks;

    private boolean pickUp = false;
    private GameRules gr;

    /**
     * Game Constructor.
     * Create new deck for Euchre, set enum to menu, and set the current dealer to player 1
     */
    public Game() {
        deck = new Deck(Deck.GameName.Euchre);
        playerHands = new playerHand[4];

        tc = new TurnController(4);
        gr = new GameRules(tc);
        ai = new AI(gr, tc);

        team1Score = 0;
        team2Score = 0;

        team1Tricks = 0;
        team2Tricks = 0;
    }

    /**
     * Queries for player input when called, returns input
     *
     * @return
     */
    public String getInput() {
        if (sc == null)
            sc = new Scanner(System.in);

        return sc.nextLine();
    }

    /**
     * Handles bulk menu logic like option select
     */
    private void menuHandler() {
        String userIn = getInput();

        switch (userIn) {
            case "1":
                gameHandler();
            case "2":
                System.exit(1);
            default:
                System.out.println("Invalid input. Please select an input: ");
                System.out.println("1 - Start game");
                System.out.println("2 - Quit");
                menuHandler();
        }
    }

    /**
     * Show startup menu and then transfer to menuHandler()
     */
    public void startGame() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Welcome to the Euchre Game! Please select an input: ");
        System.out.println("1 - Start game");
        System.out.println("2 - Quit");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        menuHandler();
    }

    /**
     * Bulk of game logic
     * Shuffles decks, creates players, populates hands from deck
     */
    private void gameHandler() {
        deck.reshuffleDeck();

        dealCards();


        // announce dealer
        System.out.println((tc.getDealer() == 1 ? "You are dealing" : "Player " + tc.getDealer() + " is dealing") + "\n");
        // show player's hand to console
        showHandForPlayer(1);

        pauseFor(3);

        // draw top card
        Card turn = deck.drawTop();
        System.out.println("The " + turn.toString() + " has been flipped.\n");

        int rounds = 0;
        // first 8 rounds, choose to pickup card or choose trump after that
        while (!pickUp && rounds <= 7) {
            pauseFor(1);
            if (rounds < 4)
                doPickupCardRound(turn);
            else {
                if (!gr.getTrumpSuit().equals("pass"))
                    break;
                doChooseTrump(rounds != 7, turn);
            }
            tc.nextPlayer();
            rounds++;

        }
        gr.setTeamChoseTrump(tc.getCurrentPlayerTurn() % 2 + 1); // if 0, team 2, if 1, team 1
        // trump is chosen by now
        tc.setTrickStartingPlayer(tc.getLeftOfDealer());

        // while there are cards to play, take current players turn and then go to next player
        while (playerHands[tc.getDealer() - 1].getHand().size() > 0) {
            tc.setCurrentPlayerTurn(tc.getTrickStartingPlayer());
            while (gr.getInPlay().size() < 4) {
                pauseFor(1);
                Card lastPlayed = doCurrentPlayerTurn();

                // sets trickSuit
                if (gr.getInPlay().size() == 1) {
                    assert lastPlayed != null;
                    if (gr.isLeft(lastPlayed))
                        gr.setTrickSuit(gr.getSisterSuit(lastPlayed.getSuit()));
                    else
                        gr.setTrickSuit(lastPlayed.getSuit());

                    System.out.println("Trick: " + gr.getTrickSuit());
                }
                tc.nextPlayer();
            }

            int winningPlayer = gr.determineWinner();
            if (winningPlayer % 2 == 0) {
                team2Tricks++;
            } else {
                team1Tricks++;
            }
            System.out.println("Player " + winningPlayer + " has won a trick!\n");
            tc.setTrickStartingPlayer(winningPlayer);
            gr.clearInPlay();
            pauseFor(1);
        }

        gr.addScore(team1Tricks, team2Tricks);

        gr.printScore();
        startNextTurn();
    }

    private void dealCards() {
        // deal cards
        for (int i = 0; i < 4; i++) {
            // create new hand if none exists, else clear existing
            if (playerHands[i] == null)
                playerHands[i] = new playerHand();
            else {
                playerHands[i].clearHand();
            }
            // deal cards to all hands 5 times
            for (int j = 0; j < 5; j++) {
                playerHands[i].addCardToHand(deck.drawTop());
            }
        }
    }

    private void pauseFor(long time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startNextTurn() {
        if (team1Score == 10 || team2Score == 10)
            endGame();
        else {
            // set next dealer, set next left of dealer
            // set currentPlayer turn to left of new dealer
            // reset deck
            // reset trump and trick
            // reset pickUp
            System.out.println("Starting next Hand: \n\n");
            tc.setDealer(tc.getLeftOfDealer());
            tc.setCurrentPlayerTurn(tc.getLeftOfDealer());
            gr.setTrumpSuit("pass");
            gr.setTrickSuit("none");
            team1Tricks = 0;
            team2Tricks = 0;
            pickUp = false;
            gameHandler();
        }
    }

    private void endGame() {
        int winningTeam = -1;
        if (team1Score == 10)
            winningTeam = 1;
        else
            winningTeam = 2;
        System.out.println("Team " + winningTeam + " has won!");
        System.out.println("Play again? y / n");
        String userIn = getInput();

        switch (userIn) {
            case "y":
                resetGame();
                break;
            case "n":
                System.out.println("Thanks for playing!");
                System.exit(1);
            default:
                System.out.println("Invalid command. Please Try again");
                endGame();
        }
    }

    private void resetGame() {
        team1Score = 0;
        team2Score = 0;
        team1Tricks = 0;
        team2Tricks = 0;

        gr.clearInPlay();

        gameHandler();
    }

    private void doChooseTrump(boolean canPass, Card turn) {

        switch (tc.getCurrentPlayerTurn()) {
            case 1:
                System.out.println("What suit would you like to choose as trump?");
                ArrayList<String> validSuits = new ArrayList<>(0);
                int inc = 1;
                for (int i = 0; i < gr.getSuits().length; i++) {
                    if (!gr.getSuits()[i].equals(turn.getSuit())) {
                        validSuits.add(gr.getSuits()[i]);
                        System.out.println(inc + " - " + gr.getSuits()[i]);
                        inc++;
                    }
                }
                if (canPass) {
                    System.out.println(inc + " - Pass");
                }

                String userIn = getInput();

                try {
                    int selection = Integer.parseInt(userIn);
                    if (selection == 4) {
                        return;
                    }
                    gr.setTrumpSuit(validSuits.get(selection - 1));
                }
                catch (Exception e) {
                    System.out.println("You must choose a valid suit");
                    doChooseTrump(canPass, turn);
                }
                break;
            case 2:
            case 3:
            case 4:
                gr.setTrumpSuit(ai.chooseTrumpSuit(canPass, playerHands[tc.getCurrentPlayerTurn() - 2].getHand()));
                if (gr.getTrumpSuit().equals("pass")) {
                    System.out.println("Player " + tc.getCurrentPlayerTurn() + " has passed");
                } else {
                    System.out.println("\nPlayer " + tc.getCurrentPlayerTurn() + " has chosen " + gr.getTrumpSuit() + "\n");
                }
        }
    }

    private void doPickupCardRound(Card turn) {
        switch (tc.getCurrentPlayerTurn()) {
            case 1:
                System.out.println("Would you like " + (tc.getDealer() != 1 ? "player " + tc.getDealer() + " " : "") + "to pickup the " + turn.toString() + "? y / n");
                String userIn = getInput();
                if (userIn.equalsIgnoreCase("y")) {
                    if (tc.getDealer() == 1) {
                        System.out.println("Which card would you like to replace? ");
                        doPlayerCardSwap(turn);
                    } else {
                        ai.doCardSwap(turn, playerHands[tc.getDealer() - 1].getHand());
                        System.out.println("Player " + tc.getDealer() + " picks up the " + turn.toString() + "\n");
                    }
                    pickUp = true;
                    gr.setTrumpSuit(turn.getSuit());
                } else if (!userIn.equalsIgnoreCase("n")) {
                    System.out.print("\nUnknown input, ");
                    doPickupCardRound(turn);
                    return;
                }
                break;
            case 2:
            case 3:
            case 4:
                if (ai.wantsToPickUpCard(turn, playerHands[tc.getCurrentPlayerTurn() - 1].getHand(), areTeamMembers(tc.getCurrentPlayerTurn(), tc.getDealer()), false)) {
                    System.out.println("Player " + tc.getCurrentPlayerTurn() +
                            (tc.getDealer() != tc.getCurrentPlayerTurn() ? " tells player " + tc.getDealer() + " to pick up the " : " picks up the ") + turn.toString() + "\n");
                    ai.doCardSwap(turn, playerHands[tc.getDealer() - 1].getHand());
                    pickUp = true;
                    gr.setTrumpSuit(turn.getSuit());
                } else {
                    System.out.println("Player " + tc.getCurrentPlayerTurn() + " passes");
                }
                break;
        }
    }

    private boolean areTeamMembers(int one, int two) {
        if (one % 2 == two % 2) {
            return true;
        }
        return false;
    }

    private void doPlayerCardSwap(Card replaceWith) {
        String playerChoice = getInput();

        try {
            int indexOfDiscard = Integer.parseInt(playerChoice) - 1;
            playerHands[0].getHand().set(indexOfDiscard, replaceWith);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("You don't have that many cards! Try again");
            doPlayerCardSwap(replaceWith);
        } catch (NumberFormatException e) {
            System.out.println("Not a valid number! Try again");
            doPlayerCardSwap(replaceWith);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private Card doCurrentPlayerTurn() {
        // AI chooses best card to play, returns it to cardPlayed
        // print card played
        // add it to inPlay pile, remove it from hand.
        switch (tc.getCurrentPlayerTurn()) {
            case 1 -> {
                System.out.print("--------------------------\n" + "Your turn: What card do you want to play?");
                System.out.print(" (Trump: " + gr.getTrumpSuit() + ")\n");
                printHand();
                System.out.println("--------------------------");
                String userIn = getInput();
                try {
                    Card playedCard = playerHands[0].getHand().get(Integer.parseInt(userIn) - 1);
                    if (gr.isPlayable(playedCard, playerHands[0].getHand())) {
                        // remove card from hand, add to inplay.
                        gr.addInPlay(playedCard);
                        playerHands[0].removeCardFromHand(playedCard);
                        return playedCard;
                    } else {
                        System.out.println("Card is not playable. Must play a card of suit: " + gr.getTrickSuit());
                        return doCurrentPlayerTurn();
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Try again");
                    return doCurrentPlayerTurn();
                }
            }
            case 2, 3, 4 -> {
                Card cardPlayed = ai.playCard(playerHands[tc.getCurrentPlayerTurn() - 1].getHand());
                System.out.println("Player " + tc.getCurrentPlayerTurn() + " plays a " + cardPlayed.toString());
                gr.addInPlay(cardPlayed);
                playerHands[tc.getCurrentPlayerTurn() - 1].removeCardFromHand(cardPlayed);
                return cardPlayed;
            }
            default -> System.out.println("Something went wrong. Game.doCurrentPlayerTurn()");
        }
        return null;
    }

    private void printHand() {

        for (int i = 0; i < playerHands[0].getHand().size(); i++) {
            Card tempCard = playerHands[0].getHand().get(i);
            System.out.print("\n" + (i + 1) + " - " + tempCard.toString());

            if (gr.isLeft(playerHands[0].getHand().get(i))) {
                System.out.print(" (" + gr.getSisterSuit(tempCard.getSuit()) + ")");
            }
        }
        System.out.print("\n");
    }

    private void showHandForPlayer(int player) {
        System.out.println(player == 1 ? "Your hand:" : "Player " + player + "'s hand:");
        System.out.println("----------------------");
        for (Card card : playerHands[player - 1].getHand()) {
            System.out.println(card.toString());
        }
        System.out.println("----------------------");
    }

    public static void main(String[] args) {
        Game game = new Game();

        game.startGame();
    }
}

public class TurnController {

    private static int dealer;
    private static int currentPlayerTurn;
    private static int trickStartingPlayer;

    public TurnController() {
        dealer = 1;
        currentPlayerTurn = 2;
    }

    public TurnController(int dealer) {
        TurnController.dealer = dealer;
        currentPlayerTurn = dealer + 1 == 5 ? 1 : dealer + 1;
        trickStartingPlayer = currentPlayerTurn;
    }

    /**
     * Get current player number that's dealing
     * @return
     */
    public int getDealer() {
        return dealer;
    }

    /**
     * Set current player number of dealer
     * @param val
     */
    public void setDealer(int val) {
        dealer = val;
    }

    public int getLeftOfDealer() {
        return dealer + 1 == 5 ? 1 : dealer + 1;
    }

    /**
     * Get player number left of the dealer
     */
    public void nextPlayer() {
        if (currentPlayerTurn == 4) {
            currentPlayerTurn = 1;
        } else {
            currentPlayerTurn++;
        }
    }

    public int getTrickStartingPlayer() {
        return trickStartingPlayer;
    }

    public void setTrickStartingPlayer(int amt) {
        trickStartingPlayer = amt;
    }

    public void setCurrentPlayerTurn(int currentPlayerTurn) {
        TurnController.currentPlayerTurn = currentPlayerTurn;
    }

    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }
}

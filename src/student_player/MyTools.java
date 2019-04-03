package student_player;

import boardgame.Board;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class MyTools {

	/**
	 * Makes random moves for both players until the end of the game. Returns the number of
	 * wins resulting from n tries (where n is specified by the provided argument).
	 * 
	 * @param myColour - the StudentPlayer's colour
	 * @param boardState - the game board
	 * @param n - the number of times the default policy is allowed to run (resource-limited)
	 * @return - the likelihood that myColour won the game
	 */
    public static int defaultPolicy(int myColour, PentagoBoardState boardState, int n) {
    	int wins = 0;
    	
    	for (int i = 0; i < n; i++) {
	    	while (!boardState.gameOver()) {
	    		PentagoMove m = (PentagoMove) boardState.getRandomMove();
	    		boardState.processMove(m);
	    	}
	    	if (boardState.getWinner() == myColour) {
	    		wins++;
	    	} 
    	}
    	
    	return wins;
    }
}
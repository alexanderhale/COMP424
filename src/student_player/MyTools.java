package student_player;

import boardgame.Board;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class MyTools {

	/**
	 * Makes random moves for both players until the end of the game. Repeats 10 times, then
	 * returns a score. +1 for a win, no change for a draw, -1 for a loss.
	 * 
	 * @param myColour - the StudentPlayer's colour
	 * @param boardState - the game board
	 * @param n - the number of times the default policy is allowed to run (resource-limited)
	 * @return - the likelihood that myColour won the game
	 */
    public static int defaultPolicy(int myColour, PentagoBoardState boardState, int n) {
    	int score = 0;
    	
    	// run the default policy the specified number of times
    	for (int i = 0; i < n; i++) {
    		// play all the way until the game is over
	    	while (!boardState.gameOver()) {
	    		// make random moves for both players
	    		PentagoMove m = (PentagoMove) boardState.getRandomMove();
	    		boardState.processMove(m);
	    	}
	    	if (boardState.getWinner() == myColour) {
	    		score++;	// if we win, +1
	    	} else if (boardState.getWinner() == Board.DRAW) {
	    		// do nothing: if we draw, +0
	    	} else {
	    		score--;	// if we lose, -1
	    	}
    	}
    	
    	// return the resulting score
    	return score;
    }
}
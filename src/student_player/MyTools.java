package student_player;

import boardgame.Board;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

public class MyTools {

	// make random moves for both players until the end of the game
		// return 1 if argument myColour's team wins the game
		// return -1 if argument myColour's team loses the game
		// return 0 for a draw
    public static int defaultPolicy(int myColour, PentagoBoardState boardState) {
    	while (!boardState.gameOver()) {
    		PentagoMove m = (PentagoMove) boardState.getRandomMove();
    		boardState.processMove(m);
    	}
    	if (boardState.getWinner() == myColour) {
    		return 1;
    	} else if (boardState.getWinner() == Board.DRAW) {
    		return 0;
    	} else {
    		return -1;
    	}
    }
}
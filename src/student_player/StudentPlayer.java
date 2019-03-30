package student_player;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260672475");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {

        ArrayList<PentagoMove> allMoves = boardState.getAllLegalMoves();
        int myColour = boardState.getTurnPlayer();
        PentagoMove lastResort = null;
        
        // weak approach to start with (will probably be too computationally expensive):
        	// for each move in allMoves
        		// make the move
        		// make random moves for both players until endgame
        		// assign a value to that move (1 for win, -1 for loss, 0 for draw)
        			// as soon as a move is encountered that won, pick that move
        			// TODO later: make multiple random runs for each one and pick the one with the highest score (i.e. Monte-Carlo
        		// pick one of the moves that results in a win
        // TODO later: implement a-b pruning to make this faster (hopefully fast enough?)
        
        for (PentagoMove m : allMoves) {
        	PentagoBoardState movedBoard = ((PentagoBoardState)boardState.clone());
        	movedBoard.processMove(m);
        	
        	// check whether this move ends the game
        	if (movedBoard.gameOver()) {
        		if (movedBoard.getWinner() == myColour) {
        			return m;
        		} else if (movedBoard.getWinner() == Board.DRAW) {
        			// save this move as a last resort
        			lastResort = m;
        		}
        		// if this move resulted in a loss, we don't want it - move on
        	} else {
        		// if this move doesn't end the game, we need to determine whether it's a good move
            	int defaultPolicy = MyTools.defaultPolicy(myColour, movedBoard);
            	
            	if (defaultPolicy == 1) {
            		return m;
            	}
        	}
        }
        
        if (lastResort != null) {
        	// if we found a move that ended the game in a draw, and we're here, that's the best we've got - use it
        	return lastResort;
        } else {
        	// if no good move was found, return a random move
            return boardState.getRandomMove();
        }
    }
}
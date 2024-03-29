package student_player;

import java.util.ArrayList;

import boardgame.Board;
import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

	// list to store the time that each move took to decide
	ArrayList<Long> moveTimes = new ArrayList<Long>();
	
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
        // determine what colour we are
        int myColour = boardState.getTurnPlayer();
        
        // number of times the default policy is allowed to run to find a score
        int n = 50;
        
        // variables to keep track of the best move we've found
        PentagoMove bestMove = null;
        int bestMoveScore = 0 - n;	// initialize this n losses
        
        // try each of the available moves
        for (PentagoMove m : boardState.getAllLegalMoves()) {
        	// make the move
        	PentagoBoardState movedBoard = ((PentagoBoardState)boardState.clone());
        	movedBoard.processMove(m);
        	
        	// check whether this move ends the game
        	if (movedBoard.gameOver()) {       		
        		if (movedBoard.getWinner() == myColour) {
        			// this move wins the game for us, so we should make it
        			return m;
        		} else if (movedBoard.getWinner() == Board.DRAW) {
        			// this move's score is 0. If that's better than the current best move, save it
        			if (bestMoveScore < 0) {
        				bestMove = m;
        				bestMoveScore = 0;
        			}
        		}
        		// if this move resulted in a loss, we don't want it - move on
        	} else {
        		// if this move doesn't end the game, we need to determine whether it's a good move
        			// run the fast default policy from after this move to determine a score for this move
            	int score = MyTools.defaultPolicy(myColour, movedBoard, n);
            	
            	// if this move's score is the best we've seen so far, save it
            	if (score > bestMoveScore) {
            		bestMove = m;
            		bestMoveScore = score;
            	}
        	}
        }
        
        if (bestMove != null) {
        	// return the best move we've found
        	return bestMove;
        } else {
        	// if no good move was found, return a random move
            return boardState.getRandomMove();
        }
    }
}
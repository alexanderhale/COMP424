package student_player;

import java.util.ArrayList;

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
        	int defaultPolicy = MyTools.defaultPolicy(myColour, movedBoard);
        	
        	if (defaultPolicy == 1) {
        		return m;
        	}
        }
        
        // if no good move was found, return a random move
        return boardState.getRandomMove();
    }
}
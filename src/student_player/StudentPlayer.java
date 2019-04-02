package student_player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
     * 
     * TODO later: implement a-b pruning to make this faster (hopefully fast enough?)
     */
    public Move chooseMove(PentagoBoardState boardState) {
    	// start timer to record length of each move
    	long startTime = System.nanoTime();
        
        // determine what colour we are
        int myColour = boardState.getTurnPlayer();
        
        // number of times the default policy is allowed to run to find a score
        int n = 50;
	   	 /* TODO future improvement: Later in the game (when there are fewer moves to consider
	   	 * and the default policy is faster), increase N. */
        
        // depth 1 scores
        HashMap<PentagoMove, Integer> scoresD1 = new HashMap<PentagoMove, Integer>();
        
        // try each of the available moves
        for (PentagoMove m : boardState.getAllLegalMoves()) {
        	// make the move
        	PentagoBoardState movedBoard = ((PentagoBoardState)boardState.clone());
        	movedBoard.processMove(m);
        	
        	// check whether this move ends the game
        	if (movedBoard.gameOver()) {       		
        		if (movedBoard.getWinner() == myColour) {
            		// print time taken per move TODO remove printing, instead just use the info
            		moveTimes.add(System.nanoTime() - startTime);
            		for (int i = 1; i <= moveTimes.size(); i++) {
            			System.out.println("Move " + i + ": " + ((double)moveTimes.get(i - 1)/(double)1000000000) + "s");
            		}
            		
            		// this move is a winning one, make it!
        			return m;
        		} else if (movedBoard.getWinner() == Board.DRAW) {
        			// this move's score is 0. Save it as such
        			scoresD1.put(m, 0);
        		}
        		// if this move resulted in a loss, we don't want it - move on
        			// if all the moves result in a loss, the fall-through of making a random move is fine anyway
        	} else {
        		// if this move doesn't end the game, determine whether it's a good move
        			// run the fast default policy from after this move to determine a score for this move
        		scoresD1.put(m, MyTools.defaultPolicy(myColour, movedBoard, n));
        	}
        }
        
        // variables to keep track of the best move we've found
        PentagoMove bestMove = null;
        int bestMoveScore = 0 - n;	// initialize this at n losses
        
        // try each of the available moves
        for (Entry<PentagoMove, Integer> m : scoresD1.entrySet()) {
        	// only worth expanding this node if it has a chance of winning
        	if (m.getValue() > 1) {
	        	// make the move
	        	PentagoBoardState movedBoard = ((PentagoBoardState)boardState.clone());
	        	movedBoard.processMove(m.getKey());

	        	// for all possible random moves of the other agent (depth 2)
	        	for (PentagoMove d2 : movedBoard.getAllLegalMoves()) {
	        		// make the move
		        	PentagoBoardState movedBoardDepth2 = ((PentagoBoardState)boardState.clone());
		        	movedBoardDepth2.processMove(d2);
		        	
		        	// TODO check whether this move ends the game
		        	
	        		// for all possible moves of our player (depth 3)
	        		for (PentagoMove d3 : movedBoardDepth2.getAllLegalMoves()) {
	        			// make the move
			        	PentagoBoardState movedBoardDepth3 = ((PentagoBoardState)boardState.clone());
			        	movedBoardDepth3.processMove(d3);
			        	
			        	// check whether this move ends the game
			        	if (movedBoardDepth3.gameOver()) {       		
			        		if (movedBoardDepth3.getWinner() == myColour) {
			            		// print time taken per move TODO remove printing, instead just use the info
			            		moveTimes.add(System.nanoTime() - startTime);
			            		for (int i = 1; i <= moveTimes.size(); i++) {
			            			System.out.println("Move " + i + ": " + ((double)moveTimes.get(i - 1)/(double)1000000000) + "s");
			            		}
			            		
			            		// this (grandparent) move is a winning one, make it!
			        			return m.getKey();
			        		} else if (movedBoardDepth3.getWinner() == Board.DRAW) {
			        			// this move's score is 0. Value in hashmap remains constant
			        			scoresD1.replace(m.getKey(), m.getValue(), m.getValue() + 0);
			        		}
			        		// if this move resulted in a loss, we don't want it - move on
			        			// if all the moves result in a loss, the fall-through of making a random move is fine anyway
			        	} else {
			        		// if this move doesn't end the game, determine whether it's a good move
			        			// run the fast default policy from after this move to determine a score for this move
			        		scoresD1.replace(m.getKey(), m.getValue(), m.getValue() + MyTools.defaultPolicy(myColour, movedBoard, n));
			        	}
	        		}
	        	}
        	}
        	if (m.getValue() > bestMoveScore) {
        		bestMoveScore = m.getValue();
        		bestMove = m.getKey();
        	}
        }
        
        
        
        moveTimes.add(System.nanoTime() - startTime);
        
        if (bestMove != null) {
        	// return the best move we've found
        	return bestMove;
        } else {
        	// if no good move was found, return a random move
            return boardState.getRandomMove();
        }
    }
}
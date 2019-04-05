package student_player;

import java.util.ArrayList;
import java.util.PriorityQueue;
import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

	// list to store the time that each move took to decide
	ArrayList<Long> moveTimes = new ArrayList<Long>();
	
	// variables to fill on the first move of the game:
		// root node of the search tree
	    Node root = new Node(null);
    
	    // queue of nodes in consideration to be expanded
        PriorityQueue<Node> toExpand = new PriorityQueue<Node>();
	    
	    // which colour our player is playing for
	    int myColour;
	
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
    	
    	// number of Monte-Carlo trials to run
    	int trials = 50;
    	
    	// keep track of best move ("best" = best win average)
    	Node bestMove;
    	int bestMoveScore = 0;
    	
    	if (boardState.getTurnNumber() == 0) {
    		// determine what colour we are
            myColour = boardState.getTurnPlayer();
    		
    		// if we're playing first, the current boardState is an empty board
    		if (boardState.firstPlayer() == myColour) {
    			root = new Node(boardState);
    			toExpand.add(root);
    		} else {
    			// otherwise, we have to initialize the root as an empty board ourselves
    			root = new Node(null);
    			Node c = new Node(boardState);
    			root.addChild(c);
    			toExpand.add(c);
    		}
    		
    		// set root
    		root.setRoot();
            
            // construct as much search tree as we can in the first-move time-limit
            do {
            	Node n = toExpand.remove();
            	
            	for (PentagoMove m : n.getBoardState().getAllLegalMoves()) {
            		PentagoBoardState movedBoard = (PentagoBoardState) n.getBoardState().clone();
            		movedBoard.processMove(m);
            		Node c = new Node(movedBoard);
                	n.addChild(c);
                	c.addParent(n);
            		if (movedBoard.gameOver()) {
            			c.isLeaf();
            		} else {
            			c.addSimulations(MyTools.defaultPolicy(myColour, c.getBoardState(), trials), trials);
            			toExpand.add(c);
            		}
            	}
            } while (((double)(System.nanoTime() - startTime)/((double)1000000000) < (double)25) && !toExpand.isEmpty());
    	}
    	
    	/* TODO choose a move */    	
    	
    	System.out.println("Move time: " + ((double)(System.nanoTime() - startTime)/(double)1000000000) + "s");
    	
    	// TODO remove
        return boardState.getRandomMove();
    }
}
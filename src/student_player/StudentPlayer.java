package student_player;

import java.util.ArrayList;
import java.util.Collections;

import boardgame.Board;
import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

	// list to store the time that each move took to decide
	ArrayList<Long> moveTimes = new ArrayList<Long>();
	
	// root node of the search tree
		// TODO make the tree outside here, so that we can save search procedures between moves
	
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
        
        // make the root node
        Node root = new Node(null);
        root.setRoot();
        
        // list of nodes in consideration to be expanded
        ArrayList<Node> toExpand = new ArrayList<Node>();
        
        // add all possible depth-1 nodes to the tree AND run the default policy on them
        for (PentagoMove m : boardState.getAllLegalMoves()) {
        	// add all available depth 1 nodes to the tree
        	Node c = new Node(m);
        	c.addParent(root);
        	root.addChild(c);
        	
        	// make the move
        	PentagoBoardState movedBoard = ((PentagoBoardState)boardState.clone());
        	movedBoard.processMove(m);
        	
        	// check if this move ends the game before bothering to spend time on the default policy iterations
        	if (movedBoard.gameOver()) {
        		if (movedBoard.getWinner() == myColour) {
        			c.addSimulations(1, 1);
        		} else {
        			c.addSimulations(0, 1);
        		}
        	} else {
	        	// run the default policy starting from this node
	        	c.addSimulations(MyTools.defaultPolicy(myColour, movedBoard, n), n);
        	}
        	
        	// add this node to the list of nodes to explore
        	toExpand.add(c);
        }
        
        // pick the most promising node
        Collections.sort(toExpand);			// sort the list, with the best node first
        Node bestNode = toExpand.get(0);
        
        // continue exploring while we still have at least 0.25 seconds left
        while (System.nanoTime() - startTime < 1750000000) {
        	// expand the best move
        	PentagoBoardState movedBoard = ((PentagoBoardState)boardState.clone());
        	movedBoard.processMove(bestNode.getMove());
        	
        	for (PentagoMove m : movedBoard.getAllLegalMoves()) {
        		// add all nodes to the tree
            	Node c = new Node(m);
            	c.addParent(bestNode);
            	bestNode.addChild(c);
            	
            	// make the move
            	PentagoBoardState movedBoardDepth2 = ((PentagoBoardState)movedBoard.clone());
            	movedBoardDepth2.processMove(m);
            	
            	// check if this move ends the game before bothering to spend time on the default policy iterations
            	if (movedBoardDepth2.gameOver()) {
            		if (movedBoardDepth2.getWinner() == myColour) {
            			c.addSimulations(1, 1);
            		} else {
            			c.addSimulations(0, 1);
            		}
            	} else {
    	        	// run the default policy starting from this node
    	        	c.addSimulations(MyTools.defaultPolicy(myColour, movedBoardDepth2, n), n);
            	}
            	
            	// add this node to the list of nodes to explore
            	toExpand.add(c);
        	}
        	
        	Collections.sort(toExpand);		// sort the list, with the best node first
        	bestNode = toExpand.get(0);		// pick the most promising node
        }
        
        // find the most promising node at depth 1
        double bestWinAverage = 0;
        bestNode = null;
        for (Node depth1 : root.getChildren()) {
        	if (depth1.winAverage() > bestWinAverage) {
        		bestWinAverage = depth1.winAverage();
        		bestNode = depth1;
        	}
        }
        
        System.out.println("Move time: " + ((double)(System.nanoTime() - startTime)/(double)1000000000) + "s");
        
        if (bestNode != null) {
        	return bestNode.getMove();
        } else {
        	return boardState.getRandomMove();
        }
    }
}
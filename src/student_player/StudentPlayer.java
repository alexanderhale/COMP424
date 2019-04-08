package student_player;

import java.util.PriorityQueue;
import boardgame.Move;

import pentago_swap.PentagoPlayer;
import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {
	
	/* variables to fill on the first move of the game */
	// root node of the search tree
    Node root = new Node(null);

    // queue of nodes in consideration to be expanded
    PriorityQueue<Node> toExpand = new PriorityQueue<Node>();
    
    // which colour our player is playing for
    int myColour;
    
	// variable for this turn's time limit (initialize at 27s, change if not first turn)
	long timeLimit = (long)Math.pow(27, 9);
	
	// number of Monte-Carlo trials to run
	int trials = 50;
    
    // the last opponent boardState
    Node opponentLastNode;
	
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
    	
    	// keep track of best move ("best" = best win average)
    	Node bestNode = null;
    	PentagoMove bestMove = null;
    	double bestNodeScore = 0;
    	
    	if (boardState.getTurnNumber() == 0) {
    		// determine what colour we are
            myColour = boardState.getTurnPlayer();
    		
    		// if we're playing first, the current boardState is an empty board
    		if (boardState.firstPlayer() == myColour) {
    			root = new Node(boardState);
    			toExpand.add(root);
    			opponentLastNode = null;
    		} else {
    			// otherwise, we have to initialize the root as an empty board ourselves
    			root = new Node(null);			// TODO might not want this to be null
    			Node c = new Node(boardState);
    			root.addChild(c);
    			toExpand.add(c);
    			opponentLastNode = root;
    		}
    		
    		// set root
    		root.setRoot();
    	} else {
    		// lower time limit
    		timeLimit = (long)Math.pow(1.75, 9);
    		
    		// refresh priority queue
    		toExpand = new PriorityQueue<Node>();
    	}
    	
    	/* choose a move */
    	
    	// step 1: find this boardState node in the tree
    		// this boardState must be a child of opponentLastNode
    	Node startNode = null;
    	
    	if (opponentLastNode != null) {
	    	for (Node n : opponentLastNode.getChildren()) {	// TODO never entering this loop because opponentLastNode is null
	    		if (n.getBoardState().toString().equals(boardState.toString())) {
	    			// the node exists and we found it
	    			startNode = n;
	    		}
	    	}
    	}
    	if (startNode == null) {
    		// the node didn't exist yet, so create it
    		startNode = new Node(boardState);
    		
    		if (opponentLastNode != null) {
    			opponentLastNode.addChild(startNode);
    			startNode.addParent(opponentLastNode);
    		}
    	}
    	
    	// step 2: expand the tree downward from this node to pick the best move
    		// construct as much search tree as we can in the time-limit
    	toExpand.add(startNode);
    	
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
        			
        			// if this move wins the game, return immediately
        			if (movedBoard.getWinner() == myColour) {
        				return m;
        			}
        			
        		} else {
        			c.addSimulations(MyTools.defaultPolicy(myColour, c.getBoardState(), trials), trials);
        			toExpand.add(c);
        		}
        		
        		// keep track of the best node based on c's win rate
        		if (c.winAverage() > bestNodeScore) {
        			bestNodeScore = c.winAverage();
        			bestNode = c;
        			bestMove = m;
        		}
        	}
        } while (((System.nanoTime() - startTime) < timeLimit) && !toExpand.isEmpty());
    	
        // TODO remove
    	System.out.println("Move time: " + ((double)(System.nanoTime() - startTime)/(double)1000000000) + "s");
    	
    	// save spot in graph for next turn use
    	PentagoBoardState interim = (PentagoBoardState) bestNode.getBoardState().clone();
    	interim.processMove(bestMove);
    	opponentLastNode = new Node(interim);	// TODO make sure this node doesn't already exist in the graph
    	bestNode.addChild(opponentLastNode);	// TODO make sure this link doesn't already exist
    	opponentLastNode.addParent(bestNode);	// TODO make sure this link doesn't already exist
    	
    	// return move
    	return bestMove;
    }
}
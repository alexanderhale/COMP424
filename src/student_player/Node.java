package student_player;

import java.util.ArrayList;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;

/**
 * Implements a node in the game search tree. Tracks the node's parent(s), child(ren), 
 * total simulations won, and total simulations made.
 * 
 * @author Alex
 *
 */
public class Node implements Comparable<Node> {
	
	private ArrayList<Node> parents;
	private ArrayList<Node> children = new ArrayList<Node>();
	private int simulationsWon;
	private int totalSimulations;
	private PentagoBoardState boardState;
	private boolean root;
	private boolean leaf;
	private boolean myColour;
	private PentagoMove incomingMove;
	
	public Node(PentagoBoardState boardState, PentagoMove incomingMove) {
		this.parents = new ArrayList<Node>();
		this.children = new ArrayList<Node>();
		this.simulationsWon = 0;
		this.totalSimulations = 0;
		this.boardState = boardState;
		this.root = false;
		this.leaf = false;
		this.incomingMove = incomingMove;
	}
	
	public void setRoot() {
		this.root = true;
	}
	
	public boolean isRoot() {
		return this.root;
	}
	
	public boolean isLeaf() {
		return this.leaf;
	}
	
	public void addParent(Node p) {
		this.parents.add(p);
		
		/*if (this.depth() % 2 == 1) {
			this.myColour = true;
		} else {
			this.myColour = false;
		}*/
	}
	
	public void removeParent(Node p) {
		this.parents.remove(p);
	}
	
	public ArrayList<Node> getParents() {
		return this.parents;
	}
	
	public Node getParent(int n) {
		return this.parents.get(n);
	}
	
	public void addChild(Node c) {
		this.children.add(c);
	}
	
	public void removeChild(Node c) {
		this.children.remove(c);
	}
	
	public ArrayList<Node> getChildren() {
		return this.children;
	}
	
	public Node getChild(int n) {
		return this.children.get(n);
	}
	
	public void addSimulations(int wins, int plays) {
		this.simulationsWon += wins;
		this.totalSimulations += plays;
		
		if (!this.isRoot()) {
			for (Node p : this.getParents()) {
				p.addSimulations(wins, plays);
			}
		}
	}
	
	public double upperConfidenceValue() {
		// upper confidence formula from Lecture 8 slide 17
			// don't really have a value for n(s,a) so used win rate instead
		double winRate = (double)this.simulationsWon / (double)this.totalSimulations;
		return winRate + (Math.sqrt(2))*(Math.sqrt((Math.log(this.totalSimulations))/(winRate)));
	}
	
	public int winColour() {
		if (this.isLeaf()) {
			return this.getBoardState().getWinner();
		}
		return 0;
	}
	
	public PentagoBoardState getBoardState() {
		return this.boardState;
	}
	
	public int depth() {
		if (this.isRoot()) {
			return 0;
		} else {
			return this.getParent(0).depth() + 1;
		}
	}
	
	public boolean myColour() {
		return this.myColour;
	}
	
	public PentagoMove incomingMove() {
		return this.incomingMove;
	}
	
	public int compareTo(Node n) {
		double av1 = this.upperConfidenceValue();
		double av2 = n.upperConfidenceValue();
		
		if (av1 < av2) {
			return -1;
		} else if (av1 > av2) {
			return 1;
		} else {
			return 0;
		}
	}
}
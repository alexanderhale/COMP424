package student_player;

import java.util.ArrayList;

import pentago_swap.PentagoBoardState;

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
	
	public Node(PentagoBoardState boardState) {
		this.parents = new ArrayList<Node>();
		this.children = new ArrayList<Node>();
		this.simulationsWon = 0;
		this.totalSimulations = 0;
		this.boardState = boardState;
		this.root = false;
		this.leaf = false;
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
	
	public double winAverage() {
		// TODO change this to a proper metric to compare the goodness of nodes
			// do some research to see how to calculate this
		return ((double)this.simulationsWon / (double)this.totalSimulations);
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
	
	public int compareTo(Node n) {
		double av1 = this.winAverage();
		double av2 = n.winAverage();
		
		if (av1 < av2) {
			return -1;
		} else if (av1 > av2) {
			return 1;
		} else {
			return 0;
		}
	}
}
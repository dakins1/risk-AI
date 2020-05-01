package net.yura.domination.engine.ai.logic;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class Node implements Comparable<Node> {

	// TODO create the applyMove() function
	// TODO add probability features
	// TODO test
	//		so far i've tested creating a node and adding children, and that all works
	
	public int winCount = 0;
	public int simsCount = 0;
	public int heuristic;
	public Node parent; 
	public RiskGame game; //the resulting board/game state of move
	public Player player;
	public boolean terminalState;
	public boolean isExpanded;
	public Move move; //the move made that created this board/game state
	public ArrayList<Node> children = new ArrayList<Node>();
	
	public Node(RiskGame game) { //for when this is root node
		this.game = game;
		this.player = this.game.getCurrentPlayer();
		this.heuristic = new AIHeuristic(this.game, this.player).getRating();
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) terminalState = true;
		else terminalState = false;
		isExpanded = false;
	}
	
	public Node(RiskGame game, Move move, Node parent) {
		this.game = cloneGame(game);
		this.player = this.game.getCurrentPlayer();
		this.move = move;
		applyMove(this.game, this.move);
		this.parent = parent;
		this.heuristic = new AIHeuristic(this.game, this.player).getRating();
		System.out.println("New node; heuristic: " + this.heuristic);
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) terminalState = true;
		else terminalState = false;
		isExpanded = false;
	}
	
	private void applyMove(RiskGame g, Move m) {
		// TODO 
		//this will apply the move m to game g
		//should mutate this copy of the game
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Move> getPossibleMoves() {
		ArrayList<Move> possibs = new ArrayList<Move>();
		for (Country us : (Vector<Country>) player.getTerritoriesOwned()) {
			for (Country them : (Vector<Country>) us.getNeighbours()) {
				if (them.getOwner() != player) {
					for (MoveStrength s : MoveStrength.values()) {
						if (us.getArmies() >= 2) {
							System.out.println("Adding move");
							possibs.add(new Move(us, them, s));
						}
					}
				}
			}
		}
		return possibs;
	}
	
	private RiskGame cloneGame(RiskGame gameToClone) { 
		RiskGame copy = null;
		//perhaps we shouldn't catch this exception, so we really know when a copy hasn't been made. 
		try {
			return gameToClone.deepCopy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ABORT - COPY NOT SUCCESSFULLY MADE");
			e.printStackTrace();
		}
		return copy;
	}
	
	public double winRatio() {
		//might need to add UCB here
		if (simsCount == 0) return 0.0;
		else return Double.valueOf(winCount) / Double.valueOf(simsCount);
	}
	
	public double ucb() {
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) return -1;
		if (simsCount == 0 || parent == null) return Double.MAX_VALUE; 
		else {
			int c = 3; //set to 3 based off slides
			// TODO change this math to probability
			return winRatio() + (c * Math.sqrt(2 * Math.log(parent.simsCount) / simsCount));
		}
	}
	
	public Node addChildFromMove(Move newMove) {
		Node newNode = new Node(game, newMove, this);
		children.add(newNode);
		return newNode;
	}
	
	public Node bestValueChild() {
		Node winner = Collections.max(children);
		return winner;
	}
	
	public Node bestWinChild() {
		class WinComp implements Comparator<Node> {
			public int compare(Node n1, Node n2) {
				return Double.compare(n1.winRatio(), n2.winRatio());
			}
		}
		return Collections.max(children, new WinComp());
	}
	
	@Override 
	public int compareTo(Node n2) {
		//might need to add UCB here
		return Double.compare(ucb(), n2.ucb());		
	}
}

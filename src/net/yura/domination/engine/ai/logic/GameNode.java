package net.yura.domination.engine.ai.logic;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class GameNode implements Comparable<GameNode> {

	// TODO create the applyMove() function
	// TODO add probability features
	// TODO test
	//		so far i've tested creating a node and adding children, and that all works
	
	public int simsCount = 0;
	public double p; //probability that we get to this state
	public int heuristic;
	public double prob;
	public double totalChildValue = 0;
	
	public PNode parent; 
	public RiskGame game; //the resulting board/game state of move
	public Player player;
	public boolean terminalState;
	public boolean isExpanded;
	public Move move; //the move made that created this board/game state
	public ArrayList<PNode> children = new ArrayList<PNode>();
	public double[] strengths = { .2, .4, .6, .8, 1.0 };
	
	public GameNode(RiskGame game) { //for when this is root node
		System.out.println("Brooo what the fuuuckkk");
		this.game = game;
		this.player = this.game.getCurrentPlayer();
		this.heuristic = new AIHeuristic(this.game, this.player).getRating();
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) terminalState = true;
		else terminalState = false;
		isExpanded = false;
	}
	
	//Add probability parameter to this?
	public GameNode(RiskGame game, Move move, PNode parent) {
		System.out.println("I hate github");
		this.game = cloneGame(game);
		this.player = this.game.getCurrentPlayer();
		this.move = move;
		applyMove(this.game, this.move);
		this.parent = parent;
		this.heuristic = new AIHeuristic(this.game, this.player).getRating();
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) terminalState = true;
		else terminalState = false;
		isExpanded = false;
	}
	
	private void applyMove(RiskGame g, Move m) {
		//if its a winning move, moves troop into defending territory and changes ownership
		// TODO 
		//this will apply the move m to game g
		//should mutate this copy of the game
		
	}
	
	public void generateChildren() {
		for (Move m : getPossibleMoves()) {
			children.add(new PNode(game, m, this));
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Move> getPossibleMoves() {
		ArrayList<Move> possibs = new ArrayList<Move>();
		for (Country us : (Vector<Country>) player.getTerritoriesOwned()) {
			for (Country them : (Vector<Country>) us.getNeighbours()) {
				if (them.getOwner() != player) {
					for (double s : strengths) {
						int size = (int) ((int) (us.getArmies()-1)*s);
						if (size > 0) {
							possibs.add(new Move(us, them, size, them.getArmies()));
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
	
	public double weightedValue() {
		if (simsCount == 0) return 0.0;
		else return ((heuristic * prob)+totalChildValue) / Double.valueOf(simsCount);
	}
	
	public double ucb() {
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) return -1;
		if (simsCount == 0 || parent == null) return Double.MAX_VALUE; 
		else {
			int c = 3; //set to 3 based off slides
			// TODO change this math to probability
			return weightedValue() + (c * Math.sqrt(2 * Math.log(parent.simsCount) / simsCount));
		} 
	}
	
	public PNode addChildFromMove(Move newMove) {
		PNode newNode = new PNode(game, newMove, this);
		children.add(newNode);
		return newNode;
	}
	
	public PNode bestValueChild() {
		PNode winner = Collections.max(children);
		return winner;
	}
	
	public GameNode bestWinChild() {
		class WinComp implements Comparator<GameNode> {
			public int compare(GameNode n1, GameNode n2) {
				return Double.compare(n1.weightedValue(), n2.weightedValue());
			}
		}
		//temporary fix to get errors to frigg off
		return this;
				//Collections.max(children, new WinComp());
	}
	
	@Override 
	public int compareTo(GameNode n2) {
		//might need to add UCB here
		return Double.compare(ucb(), n2.ucb());		
	}
}

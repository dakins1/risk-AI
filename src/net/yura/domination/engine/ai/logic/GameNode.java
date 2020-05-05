package net.yura.domination.engine.ai.logic;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class GameNode extends Node {

	// TODO create the applyMove() function
	// TODO add probability features
	// TODO test
	//		so far i've tested creating a node and adding children, and that all works
	
	
	public RiskGame game; //the resulting board/game state of move
	public Player player;
	public boolean terminalState;
	//the move made that created this board/game state
//	public double[] strengths = { .2, .4, .6, .8, 1.0 };
	public double[] strengths = { 1.0 };
	
	public GameNode(RiskGame game) { //for when this is root node
		simsCount = 0;
		this.game = game;
		this.player = this.game.getCurrentPlayer();
		this.heuristic = new AIHeuristic(this.game, this.player).getRating();
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) terminalState = true;
		else terminalState = false;
		isExpanded = false;
		children = new ArrayList<Node>();
		totalChildValue = 0;
		this.heuristic = new AIHeuristic(this.game, this.player).getRating();
		//isVisited = false;
	}
	
	public GameNode(RiskGame game, Move move, PNode parent) {
		this.game = cloneGame(game);
		this.player = this.game.getCurrentPlayer();
		this.move = move;
		copyCountriesToMove(this.game, this.move);
		applyMove(this.game, this.move);
		this.parent = parent;
		simsCount = 1;
		this.heuristic = new AIHeuristic(this.game, this.player).getRating();
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) terminalState = true;
		else terminalState = false;
		isExpanded = false;
		children = new ArrayList<Node>();
		totalChildValue = 0;
		//isVisited = false;
	}
	
	private void applyMove(RiskGame g, Move m) {
		if (m.atkArmy == 0) { //simulating a loss
			m.attacker.removeArmies(m.originalAtkArmy); 
			//possibly add something for defending army, but only for later heuristics
		} else { //simulating victory
			m.defender.removeArmies(m.defender.getArmies());
			m.defender.setOwner(m.attacker.getOwner()); //transferring ownership to attacker
			m.defender.addArmies(m.atkArmy);
			m.attacker.removeArmies(m.atkArmy);
			// TODO add something that keeps track of cards maybe?
		}
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
		possibs.add(new Move(null,null,-1,-1));
		return possibs;
	}
	
	private void copyCountriesToMove(RiskGame g, Move m) {
		//The game has a deep copy of the country, so reassign the move's country pointers to the game's copies
		m.attacker = g.getCountryInt(m.attacker.getColor());
		m.defender = g.getCountryInt(m.defender.getColor());
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
		else return ((heuristic * prob)+totalChildValue);
	}
	
	public double weightedValueWithSim() {
		if (simsCount == 0) return 0.0;
		else return ((heuristic * prob)+totalChildValue) / Double.valueOf(simsCount);
	}
	
	public double ucb() {
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) return -1;
		if (simsCount == 0 || parent == null) return Double.MAX_VALUE; 
		else {
			int c = 3; //set to 3 based off slides
			// TODO change this math to probability
			return weightedValueWithSim() + (c * Math.sqrt(2 * Math.log(parent.simsCount) / simsCount));
		} 
	}
	
	public PNode addChildFromMove(Move newMove) {
		PNode newNode = new PNode(game, newMove, this);
		children.add(newNode);
		return newNode;
	}
	
	public PNode bestUCBChild() {
		PNode winner = (PNode) Collections.max(children);
		return winner;
	}
	
	public PNode bestWinChild() {
		class WinComp implements Comparator<Node> {
			public int compare(Node n1, Node n2) {
				return Double.compare(n1.weightedValueWithSim(), n2.weightedValueWithSim());
			}
		}
		return (PNode) Collections.max(children, new WinComp());
	}
	
	
}

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

	public RiskGame game; //the resulting board/game state of move
	public Player player;
	public double[] strengths = { 1.0 };
	
	public GameNode(RiskGame game) { //for when this is root node
		this.game = game;
		this.player = this.game.getCurrentPlayer();
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0) {
			this.setTerminalState(true);
		} else {
			this.setTerminalState(false);
		}
		this.setSimsCount(0);
		this.setHeuristic(new AIHeuristic(this.game, this.player).getRating());
		this.setTerminalState(false);
		this.setIsExpanded(false);
		this.setChildren(new ArrayList<Node>());
		this.setTotalChildValue(0);
	}
	
	public GameNode(RiskGame game, Move move, PNode parent) {
		this.game = cloneGame(game);
		this.player = this.game.getCurrentPlayer();
		this.setMove(move);
		copyCountriesToMove(this.game, this.getMove());
		applyMove(this.game, this.getMove());
		
		this.setParent(parent);
		this.setSimsCount(1);
		this.setHeuristic(new AIHeuristic(this.game, this.player).getRating());
		if (game.checkPlayerWon() || getPossibleMoves().size() == 0 || move.atkArmy == -1) {
			this.setTerminalState(true);
		} else {
			this.setTerminalState(false);
		}
		
		this.setIsExpanded(false);
		this.setChildren(new ArrayList<Node>());
		this.setTotalChildValue(0);

	}
	
	private void applyMove(RiskGame g, Move m) {
		if (m.atkArmy == -1) {
			this.setTerminalState(true);
			return;
		} else if (m.atkArmy == 0) { //simulating a loss
			m.attacker.removeArmies(m.originalAtkArmy); 
		} else { //simulating victory
			m.defender.removeArmies(m.defender.getArmies());
			m.defender.setOwner(m.attacker.getOwner()); //transferring ownership to attacker
			m.defender.addArmies(m.atkArmy);
			m.attacker.removeArmies(m.atkArmy);
		}
	}
	
	public void generateChildren() {
		Move move = this.getMove();
		List<Node> children = this.getChildren();
		if (move != null && move.atkArmy == -1) System.out.println("\n\n\n\nTHIS SHOULDNT HAPPEN!\n\n\n\n");
		for (Move m : getPossibleMoves()) {
			children.add(new PNode(game, m, this));
			this.setChildren(children);
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
		if (m.atkArmy != -1) {
			m.attacker = g.getCountryInt(m.attacker.getColor());
			m.defender = g.getCountryInt(m.defender.getColor());
		}
	}
	
	private RiskGame cloneGame(RiskGame gameToClone) { 
		RiskGame copy = null;
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
		int simsCount = this.getSimsCount();
		double heuristic = this.getHeuristic();
		double prob = this.getProb();
		double totalChildValue = this.getTotalChildValue();
		if (simsCount == 0) return 0.0;
		else return ((heuristic * prob)+totalChildValue);
	}
	
	public double weightedValueWithSim() {
		int simsCount = this.getSimsCount();
		double heuristic = this.getHeuristic();
		double prob = this.getProb();
		double totalChildValue = this.getTotalChildValue();
		if (simsCount == 0) return 0.0;
		else return ((heuristic * prob)+totalChildValue) / Double.valueOf(simsCount);
	}
	
	public double ucb() {
		int simsCount = this.getSimsCount();
		Node parent = this.getParent();
		
		if (simsCount == 0 || parent == null) return Double.MAX_VALUE; 
		else {
			int c = 3; //set to 3 based off slides
			return weightedValueWithSim() + (c * Math.sqrt(2 * Math.log(parent.getSimsCount()) / simsCount));
		} 
	}
	
	public PNode addChildFromMove(Move newMove) {
		PNode newNode = new PNode(game, newMove, this);
		List<Node> children = this.getChildren();
		children.add(newNode);
		this.setChildren(children);
		return newNode;
	}
	
	public PNode bestUCBChild() {
		List<Node> children = this.getChildren();
		PNode winner = (PNode) Collections.max(children);
		return winner;
	}
	
	public PNode bestWinChild() {
		class WinComp implements Comparator<Node> {
			public int compare(Node n1, Node n2) {
				return Double.compare(n1.weightedValueWithSim(), n2.weightedValueWithSim());
			}
		}
		List<Node> children = this.getChildren();
		return (PNode) Collections.max(children, new WinComp());
	}
	
	
}

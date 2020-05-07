package net.yura.domination.engine.ai.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class AIMonteCarlo {
	
	Player player;
	RiskGame game;
	
	public AIMonteCarlo(RiskGame game, Player player) {
		this.game = cloneGame(game);
		this.player = this.game.getCurrentPlayer();
	}
	
	public String getMove() {
		GameNode root = new GameNode(game);
		if (root.getTerminalState()) {
			return "endattack";
		}
		for (int i=0; i<250; i++) {
			MCTSLoop(root);			
		}

		//get rid of endattack node cuz it always has highest score
		root.eliminateEndAttackNode();
		
//		System.out.println("Chosen one: " + root.bestWinChild().getTotalChildValue() + "/" + root.bestWinChild().getSimsCount());
//		System.out.println("Prob of win: " + root.bestWinChild().probOfWin());	
		//if the best move is a bad move, end attacking phase
		
		if (root.bestWinChild().probOfWin() < .30) {
			return "endattack";
		} else {
			Move bestWinChildMove = root.bestWinChild().getMove();
			return "attack " + bestWinChildMove.attacker.getColor() + " " + bestWinChildMove.defender.getColor(); 			
		}
	
	}
	
	private void MCTSLoop(GameNode root) {
		Node selectedNode = select(root);
		expand(selectedNode);
		simulate(selectedNode); //expand function also handles simulating and backpropogating
	}
	
	private Node select(Node root) {
		if (!root.getIsExpanded()) return root; 
		else return select(root.bestUCBChild()); //otherwise pick the most promising child
		
	}
	
	private void expand(Node n) {		
		if (n.getTerminalState()/*|| n.move.atkArmy == -1*/) { //simulate ending turn
			backpropogate(n, n.weightedValue(), n.getSimsCount());
			return;
		}
		if (n.getChildren().size() == 0) {
			//System.out.println("EXPANDING: " + n.getClass());
			n.generateChildren();
		}
	}
	
	private void simulate(Node n) {
		for (Node ni : n.getChildren()) {
			if (ni.getSimsCount() < 1) {
				//System.out.println("SIMULATING: " + ni.getClass());
				ni.generateChildren();
				backpropogate(ni,ni.weightedValue(),ni.getSimsCount());
				return;
			}
		}
		if (n.getChildren().size() != 0) {
			n.setIsExpanded(true);
		}
	}

	private void backpropogate(Node n, double weightedValueTotal, int noOfNewSims) {
		int simsCount = n.getSimsCount();
		double totalChildValue = n.getTotalChildValue();
		Node parent = n.getParent();
		
		n.setSimsCount(simsCount + noOfNewSims);
		n.setTotalChildValue(totalChildValue + weightedValueTotal); 
	
		if (parent != null) backpropogate(parent, weightedValueTotal, noOfNewSims);
	}

	
	RiskGame cloneGame(RiskGame gameToClone) { 
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
	
	Player clonePlayer(Player p) { 
		Player copy = null;
		//perhaps we shouldn't catch this exception, so we really know when a copy hasn't been made. 
		try {
			return p.deepCopy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ABORT - COPY NOT SUCCESSFULLY MADE");
			e.printStackTrace();
		}
		return copy;
	}
}

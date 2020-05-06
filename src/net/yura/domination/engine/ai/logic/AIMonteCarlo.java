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
	
	public void testing() {
		GameNode root = new GameNode(game);
		int x = (int) ((int) 2*0.2);
		System.out.println("Testing ints: " + x);
		System.out.println("ai.testing beginning");
		root.generateChildren();
	}
	
	public String getMove() {
		GameNode root = new GameNode(game);
		if (root.getTerminalState()) {
			return "endattack";
		}
		for (int i=0; i<150; i++) {
			MCTSLoop(root);

			for (Node ni : root.getChildren()) {
				Move move = ni.getMove();
				if (move.atkArmy == -1) System.out.println("End turn node " + ni.weightedValueWithSim());
			}
		}
		
		Move bestWinChildMove = root.bestWinChild().getMove();
		
		if (bestWinChildMove.atkArmy == -1) {
			return "endattack";
		}
		
		return "attack " + bestWinChildMove.attacker.getColor() + " " + bestWinChildMove.defender.getColor(); 

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
			System.out.println();
			System.out.println();
			System.out.println("terminal state bakc propogated");
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

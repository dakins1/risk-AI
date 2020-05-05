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
		if (root.terminalState) {
			return "endattack";
		}
		for (int i=0; i<150; i++) {
			MCTSLoop(root);
//			for (int j=0; j<root.children.size();j++) {
//				System.out.println("THIS IS A CHILD VALUE: " + root.children.get(j).weightedValueWithSim());
//			}
		}
		for (Node ni : root.children) {
			
			//System.out.println("Node Move: " + ni.move);
			//System.out.println("Node Value: " + ni.weightedValueWithSim());
		}
		//System.out.println("Best Node Move: " + root.bestWinChild().move);
		//System.out.println("Best Node Value: " + root.bestWinChild().weightedValueWithSim());
		
//		if (root.weightedValueWithSim() >= root.totalChildValue/root.children.size()) {
//			
//			System.out.println("THE SIMS SUCK DONT DO ANYTHING!!!");
//
//			return "endattack";
//		}
		
		if (root.bestWinChild().move.atkArmy == -1) {
			return "endattack";
		}
		
		return "attack " + root.bestWinChild().move.attacker.getColor() + " " + root.bestWinChild().move.defender.getColor(); 

	}
	
	private void MCTSLoop(GameNode root) {
		Node selectedNode = select(root);
		expand(selectedNode);
		simulate(selectedNode); //expand function also handles simulating and backpropogating
	}
	
	private Node select(Node root) {
		if (!root.isExpanded) return root; 
		else return select(root.bestUCBChild()); //otherwise pick the most promising child
		
	}
	
	private void simulate(Node n) {
		
		for (Node ni : n.children) {
			
			if (ni.simsCount < 1) {
				//System.out.println("SIMULATING: " + ni.getClass());
				ni.generateChildren();
				backpropogate(ni,ni.weightedValue(),ni.simsCount);
				return;
			}
			
		}
		
		if (n.children.size() != 0) {
			n.isExpanded = true;
		}
		
	}

	private void expand(Node n) {
		
		if (n.terminalState) {
			backpropogate(n, n.weightedValue(), n.simsCount);
			return;
		}
		
		if (n.children.size() == 0) {
			//System.out.println("EXPANDING: " + n.getClass());
			n.generateChildren();
		}
	
	}
	
	private void backpropogate(Node n, double weightedValueTotal, int noOfNewSims) {
		// TODO make this update probabilities 
		System.out.println("SIMS COUNT BEFORE: " + n.simsCount);
		n.simsCount += noOfNewSims;
		System.out.println("SIMS COUNT AFTER: " + n.simsCount);
		System.out.println("TOTAL CHILD VALUE BEFORE: " + n.totalChildValue);
		n.totalChildValue += weightedValueTotal;
		System.out.println("TOTAL CHILD VALUE : " + n.totalChildValue);
		if (n.parent != null) backpropogate(n.parent, weightedValueTotal, noOfNewSims);
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

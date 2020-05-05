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
	
	public Move getMove() {
		GameNode root = new GameNode(game);
		for (int i=0; i<100; i++) MCTSLoop(root);
		return root.bestWinChild().move; 

	}
	
	private void MCTSLoop(GameNode root) {
		Node selectedNode = select(root);
		simulate(selectedNode); //expand function also handles simulating and backpropogating
	}
	
	private Node select(Node root) {
		// TODO i think probabilit nodes are already expanded by default
		if (!root.isExpanded) {
			if (!root.isVisited) {
				System.out.println("Generating children for " + root.hashCode() + " inside select !expanded && !visited");
				root.generateChildren();
			}
			if (root.terminalState)
				return root;
			
			root.isVisited = true;
			for (Node n : root.children) {

				if (!n.isVisited) {
					if (!n.terminalState) {
//						System.out.println("Generating children for " + n.hashCode() + " inside select !visited && !terminal");
//						n.generateChildren();
					}
					return n;
				}
			}
			//Null point
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			if (root instanceof GameNode) {
				System.out.println("Gamenode, num. children: " + root.children.size() + ", value: " + root.totalChildValue);
			} else {
				System.out.println(root.move.toString());
				System.out.println("PNode, num. children: " + root.children.size() + ", value: " + root.totalChildValue);
				System.out.println("Num grandchildren: " + root.children.get(0).children.size());
			}
			
			for (Node c : root.children) {
				if (c.parent != null) System.out.println("Parent hash: " + c.parent.hashCode());
				System.out.println(c.hashCode() + " " + root.move.toString());
				System.out.println("num. children: " + root.children.size() + ", value: " + root.totalChildValue);
//				System.out.println("Num grandchildren: " + root.children.get(0).children.size());
			}
			
			
			System.out.println("\n\n\n NULL ERROR IN LINE 52 \n\n\n");
			return null;

//			if (root instanceof GameNode) {
//				System.out.println("Gamenode, num. children: " + root.children.size() + ", value: " + root.totalChildValue);
//			} else {
//				System.out.println(root.move.toString());
//				System.out.println("PNode, num. children: " + root.children.size() + ", value: " + root.totalChildValue);
//				System.out.println("Num grandchildren: " + root.children.get(0).children.size());
//			}
//			
//			return root; //if we have come across a non-fully expanded node, pick that one
		}
		else return select(root.bestUCBChild()); //otherwise pick the most promising child
	}
	
	private void simulate(Node n) {
//		if (n.terminalState) { //if node is a terminal state, just re-backpropogate the sim results
//			//old code:
////			backpropogate(n, simulate(n));
//			// TODO new code
//			return;
//		}
//		if (n instanceof GameNode) {
//			System.out.println("Gamenode, num. children: " + n.children.size() + ", value: " + n.totalChildValue);
//		} else {
//			System.out.println(n.move.toString());
//			System.out.println("PNode, num. children: " + n.children.size() + ", value: " + n.totalChildValue);
//			System.out.println("Num grandchildren: " + n.children.get(0).children.size());
//		}
		
		n.isVisited = true;
		
		//only gamenodes get expanded
		//only pnodes get simulated

		System.out.println("Generating children for " + n.hashCode() + " inside simulate");
		n.generateChildren();
		if (n instanceof PNode) {
			if (n.children.size() == 0) { //hasn't been simulated
				//essentially simulate the pnode
//				n.generateChildren();
				backpropogate(n.parent, n.weightedValue(), n.simsCount);
			}
		}
		if (n.parent != null && !n.parent.isExpanded) {
			checkExpanded(n.parent);			
		}
		
//		if (n instanceof GameNode) {
//			if (n.children.size()==0) { //gameNode hasn't been expanded
//				n.generateChildren(); //expand the node
//				//nothing to backpropogate
//			}
	}
		
//		if (n.children.size() == 0) { //if children have not yet been added, add them
//			n.generateChildren();
//			if (n instanceof PNode) { //if we just created a bunch of PNodes, there's nothing to backpropogate yet
//				backpropogate(n.parent, n.weightedValue(), n.simsCount);
//			}
//		}
//		//Run a simulation on one of the children
//		for (Node ni : n.children) {
//			//we simulate a pnode by adding all possible gamenode children 
//			if (ni.simsCount < 1) {
//				//old code:
//				//backpropogate(ni, simulate(ni));
//				// TODO new code
//				// possible we might remove the simulating step...cuz we just move to 
//				// the next state with a probability inbetween
//				return; //only run one simulation on one child
//			}
//		}
//		
//		//If every child node has had a simulation on it, this node is considered fully expanded
//		n.isExpanded = true;		
//	}
	
	private void checkExpanded(Node n) {
		if (n.children.size() == 0) return;
		else {
			for (Node ni : n.children) {
				if (!ni.isVisited) return;
			}
			n.isExpanded = true;
		}
	}
	
	private void backpropogate(Node n, double weightedValueTotal, int noOfNewSims) {
		// TODO make this update probabilities 
		n.simsCount += noOfNewSims;
		n.totalChildValue += weightedValueTotal;
		if (n.parent != null) backpropogate(n.parent, weightedValueTotal, noOfNewSims);
	}
	
//	@SuppressWarnings("unchecked")
//	public void simulate() {
//		
//		
//		Vector<Player> ps = game.getPlayers();
//		for (Country ct : (Vector<Country>) player.getTerritoriesOwned()) {
//			ct.addArmies(30);
//		}
//		for (Country ct : game.getCountries()) {
//			
//			if (ct.getOwner() == player) System.out.println("ownership is the same");
//			ct.addArmies(30);
//			
//			
//			System.out.println(ct.getName() + " color: " + ct.getOwner().getName());
//			System.out.println("Player color: " + player.getName());
//			
//			if (ct.getOwner() == player) System.out.println("Player does == game.country owner, should have 30+ armies: " + ct.getArmies());
//			else System.out.println("clonedGame.Country is not owned by clonedPlayer.copy");
//		}
//	}
	
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

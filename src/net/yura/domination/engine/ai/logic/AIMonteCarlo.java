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
//		for (Move m : root.getPossibleMoves()) {
//			root.addChildFromMove(m);
//		}
	}
	
//	private Move getMove(RiskGame game) {
//		GameNode root = new GameNode(game);
//		for (int i=0; i<1000; i++) MCTSLoop(root);
//		return root.bestWinChild().move; 
//
//	}
//	
//	private void MCTSLoop(GameNode root) {
//		GameNode selectedNode = select(root);
//		expand(selectedNode); //expand function also handles simulating and backpropogating
//	}
//	
//	private GameNode select(GameNode root) {
//		if (!root.isExpanded) return root; //if we have come across a non-fully expanded node, pick that one
//		// TODO def need to add probability stuff here
//		else return select(root.bestValueChild()); //otherwise pick the most promising child
//	}
//	
//	private void expand(GameNode n) {
//		if (n.terminalState) { //if node is a terminal state, just re-backpropogate the sim results
//			//old code:
//			//backpropogate(n, simulate(n));
//			// TODO new code
//			return;
//		}
//		
//		if (n.children.size() == 0) { //if children have not yet been added, add them
//			List<Move> validMoves = n.getPossibleMoves();
//			for (Move m : validMoves) n.addChildFromMove(m);
//		}
//		//Run a simulation on one of the children
//		for (PNode ni : n.children) {
////			if (ni.simsCount < 1) {
//				//old code:
//				//backpropogate(ni, simulate(ni));
//				// TODO new code
//				// possible we might remove the simulating step...cuz we just move to 
//				// the next state with a probability inbetween
////				return; //only run one simulation on one child
//			}
//		}
//		
//		//If every child node has had a simulation on it, this node is considered fully expanded
//		n.isExpanded = true;		
//	}
//	
//	private void backpropogate(GameNode n, int simResults) {
//		// TODO make this update probabilities 
//		n.simsCount += 1;
//		if (n.parent != null) backpropogate(n.parent, simResults);
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void simulate() {
//		
//		
////		Vector<Player> ps = game.getPlayers();
////		for (Country ct : (Vector<Country>) player.getTerritoriesOwned()) {
////			ct.addArmies(30);
////		}
////		for (Country ct : game.getCountries()) {
////			
////			if (ct.getOwner() == player) System.out.println("ownership is the same");
////			ct.addArmies(30);
//			
//			
////			System.out.println(ct.getName() + " color: " + ct.getOwner().getName());
////			System.out.println("Player color: " + player.getName());
//			
////			if (ct.getOwner() == player) System.out.println("Player does == game.country owner, should have 30+ armies: " + ct.getArmies());
////			else System.out.println("clonedGame.Country is not owned by clonedPlayer.copy");
////		}
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

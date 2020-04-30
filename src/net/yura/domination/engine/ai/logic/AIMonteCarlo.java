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
	AIHeuristic heur;
	
	public AIMonteCarlo(RiskGame game, Player player) {
//		this.player = clonePlayer(player);
		this.game = cloneGame(game);
		this.player = this.game.getCurrentPlayer();
//		if (this.player == player) System.out.println("same player");
//		else System.out.println("not same player");
		heur = new AIHeuristic(this.game, this.player);
	}
	
	@SuppressWarnings("unchecked")
	private List<Move> getPossibleMoves() {
		System.out.println("Possible moves for: " + player.getName());
		ArrayList<Move> possibs = new ArrayList<Move>();
		for (Country us : (Vector<Country>) player.getTerritoriesOwned()) {
			for (Country them : (Vector<Country>) us.getNeighbours()) {
				if (them.getOwner() != player) {
					for (MoveStrength s : MoveStrength.values()) {
						possibs.add(new Move(us, them, s));
					}
				}
			}
		}
		return possibs;
	}
	
	
	@SuppressWarnings("unchecked")
	public void simulate() {
		List p = getPossibleMoves();
		
//		Vector<Player> ps = game.getPlayers();
//		for (Country ct : (Vector<Country>) player.getTerritoriesOwned()) {
//			ct.addArmies(30);
//		}
//		for (Country ct : game.getCountries()) {
//			
//			if (ct.getOwner() == player) System.out.println("ownership is the same");
//			ct.addArmies(30);
			
			
//			System.out.println(ct.getName() + " color: " + ct.getOwner().getName());
//			System.out.println("Player color: " + player.getName());
			
//			if (ct.getOwner() == player) System.out.println("Player does == game.country owner, should have 30+ armies: " + ct.getArmies());
//			else System.out.println("clonedGame.Country is not owned by clonedPlayer.copy");
//		}
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

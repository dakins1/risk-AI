package net.yura.domination.engine.ai.logic;

import java.util.List;
import java.util.Vector;

import net.yura.domination.engine.ai.AISubmissive;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.core.StatType;
import net.yura.domination.engine.core.Statistic;


public class AIHeuristic {
	
	RiskGame game;
	
	Player player;
	
	public AIHeuristic(RiskGame game, Player player) {
		
		this.game = game;
		this.player = player;
		
	}
	
	int getRating() {
		getBorderCountriesInContinent();
		return this.player.getNoTerritoriesOwned();
	}
	
	int getBorderCountriesInContinent() {
		
		for (Continent c : game.getContinents()) {
			
			Vector<Country> borderCountries;
			Player owner;
			
			borderCountries = c.getBorderCountries();
			owner = c.getOwner();
			RiskGame copy = null;
			try {
				copy = game.deepCopy();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(copy);
			copy.getCountries()[0].addArmies(32);
			System.out.println("GAME: " + game.getCountries()[0].getArmies());
			System.out.println("COPY: " + copy.getCountries()[0].getArmies());
			
			
			if (c.isOwned(this.player)) {
				
				int value = c.getArmyValue()*c.getArmyValue();
				
			}
			
		}
		
		
		return 0;
	}
	

}

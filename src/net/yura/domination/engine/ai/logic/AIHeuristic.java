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
	
	// TODO possible give regular troops within owned continents more points than regular troops on un-owned continents? 
	//		maybe not, since that kind of punishes expansion 
	// TODO make sure to test - owning and not owning a continent (use load game)
	// TODO we need some way of balancing owning territories vs. troop density, e.g. better to own a few countries with
	//		dense troops rather than multiple territories with thin troops. Maybe divide entire score by number of 
	//		territories? But as it is now, owning continents with strong borders hold the biggest rewards, so maybe 
	//		this won't be necessary 
	// TODO card rating balancing?
	
	// TODO consider giving extra points to owning border territories - but number of troops in border territories might
	//		already naturally account for this
	
	int ownedBorderScale = 3;
	int borderScale = 2;
	int ownedContinentExp = 2;
	int ownedCountriesScale = 2;
	int cardScale = 4;
	RiskGame game;
	Player player;
	
	public AIHeuristic(RiskGame game, Player player) {
		this.game = game;
		this.player = player;
		
	}
	
	int getRating() {
		int total = 0;
		total += player.getNoTerritoriesOwned() * ownedCountriesScale;
		total += continentOwnershipRating();
		total += ownedBorderStrengthRating();
		total += borderStrengthRating();
		total += genericTroopRating();
		total += cardRating();
		return total;
	}
	
	int continentOwnershipRating() {
		int rating = 0;
		for (Continent c : game.getContinents()) {
			if (c.isOwned(player)) {
				//Exponentiate the value to make different continent values stand out more in heuristic
				int val = c.getArmyValue();
				for (int i=1; i<=ownedContinentExp; i++) {
					val *= c.getArmyValue();
				}
				rating += val;
			}
		}
		return rating;
	}
	
	//Borders of owned continents should be heavily protected
	@SuppressWarnings("unchecked") 
	int ownedBorderStrengthRating() {
		int rating = 0;
		for (Continent c : game.getContinents()) {
			if (c.isOwned(player)) {
				//Returns a vector of the edge countries w/in that continent
				//e.g., North America has border countries Alaska, Greenland, and Central America
				for (Country ct : (Vector<Country>) c.getBorderCountries()) {
					rating += ct.getArmies()*ownedBorderScale;
				}
			}
		}
		return rating;
	}
	
	//Strong borders are still important, even if we don't yet own the continent
	@SuppressWarnings("unchecked")
	int borderStrengthRating() {
		int rating = 0;
		for (Continent c : game.getContinents()) {
			for (Country ct : (Vector<Country>) c.getBorderCountries()) {
				if (ct.getOwner() == player) rating += ct.getArmies()*borderScale;
			}
		}
		return rating;
	}
	
	//Since this goes through all owned countries, it will re-count the troops in border countries.
	//I'm guessing this shouldn't matter since the total score only matters in relation to other scores. 
	int genericTroopRating() {
		int rating = 0;
		for (Country ct : game.getCountries()) {
			if (ct.getOwner() == player) rating += ct.getArmies();
		}
		return rating;
	}

	int cardRating() {
		return player.getNumberCardsOwned() * cardScale;
	}
}

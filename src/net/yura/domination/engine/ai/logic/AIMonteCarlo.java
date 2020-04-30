package net.yura.domination.engine.ai.logic;

import net.yura.domination.engine.core.RiskGame;

public class AIMonteCarlo {
	
	RiskGame cloneGame(RiskGame game) {
	//place holder for our clone logic. 
		RiskGame copy = null;
		//perhaps we shouldn't catch this exception, so we really know when a copy hasn't been made. 
		//or we make everything implements Cloneable...does not sound fun
		try {
			return game.deepCopy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ABORT - COPY NOT SUCCESSFULLY MADE");
			e.printStackTrace();
		}
		return copy;
	}
	
//	
//	System.out.println(copy);
//	copy.getCountries()[0].addArmies(32);
//	System.out.println("GAME: " + game.getCountries()[0].getArmies());
//	System.out.println("COPY: " + copy.getCountries()[0].getArmies());
}

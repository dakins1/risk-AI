package net.yura.domination.engine.ai.logic;

import net.yura.domination.engine.core.Country;

public class Move {

	Country attacker;
	Country defender;
	MoveStrength strength;
	int atkArmy;
	int defArmy;
	
	public Move(Country attacker, Country defender, MoveStrength strength) {
		this.attacker = attacker;
		this.defender = defender;
		this.strength = strength;
		atkArmy = attacker.getArmies();
		defArmy = defender.getArmies();
	}
	
	@Override
	public String toString() {
		return String.format(attacker.getName() + " " + attacker.getOwner().getName() + " attacking " + defender.getName() +
				" " + defender.getOwner().getName() + ", strength " + strength.toString());
	}
	
	
	
}

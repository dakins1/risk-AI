package net.yura.domination.engine.ai.logic;

import net.yura.domination.engine.core.Country;

public class Move {

	Country attacker;
	Country defender;
	int atkArmy;
	int defArmy;
	
	public Move(Country attacker, Country defender, int atkSize, int defSize) {
		this.attacker = attacker;
		this.defender = defender;
		atkArmy = atkSize;
		defArmy = defSize;
	}
	
	@Override
	public String toString() {
		return String.format(attacker.getName() + " " + atkArmy + " attacking " + defender.getName() +
				" " + defArmy);
	}
	
	
	
}

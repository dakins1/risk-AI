package net.yura.domination.engine.ai.logic;

import net.yura.domination.engine.core.Country;

public class Move {

	public Country attacker;
	public Country defender;
	public int atkArmy;
	public int defArmy;
	public int originalAtkArmy; //very special case for when we want to track lost armies for a lost battle
	
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

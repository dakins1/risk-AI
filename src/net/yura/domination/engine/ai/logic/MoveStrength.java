package net.yura.domination.engine.ai.logic;

public enum MoveStrength {
	ONE(.2),
	TWO(.4),
	THREE(.6),
	FOUR(.8),
	FIVE(1.0);
	
	private double value;
	
	public double getValue() {
		return this.value;
	}
	
	private MoveStrength(double value) {
		this.value = value;
	}
}

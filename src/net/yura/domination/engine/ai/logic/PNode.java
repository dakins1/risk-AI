package net.yura.domination.engine.ai.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class PNode implements Comparable<PNode> {

	public Move move;
	public RiskGame game;
	public Player player;
	
	public int simsCount;
	public boolean isExpanded; //??
	public List<GameNode> children;
	public double average; //weighted avg of rewards and probabilities
	List<Double> childrenOutcomes;
	public double[] percentages = { 0, .20, .40, .60, .80, 1.0 };
	List<Integer> atkDice;
	List<Integer> defDice;
	
	public PNode(RiskGame game, Move move, GameNode parent) {
		//Might need to be a deep copy??? Might not since we're not mutating anything
		
		this.game = game;
		this.player = game.getCurrentPlayer();
		this.move = move;
		atkDice = new ArrayList<Integer>();
		defDice = new ArrayList<Integer>();		

		simsCount = 0;
		children = new ArrayList<GameNode>();
		generateChildren();
		childrenOutcomes = simulateOutcomes(this.move.atkArmy, this.move.defArmy);
		System.out.println("Original move: " + move.toString());
		for (int i=0; i<childrenOutcomes.size(); i++) {
			children.get(i).prob = childrenOutcomes.get(i);
		}
	}


	public void generateChildren() {
		System.out.println("Generating resulting gameNodes for pnode");
//		System.out.println("Original attack: " + move.atkArmy + " vs. " + move.defArmy + " strength " + move.strength);
		for (double p : percentages) {
			if (p == 0) {
				Move m = new Move(move.attacker, move.defender, 0, move.defender.getArmies());
				children.add(new GameNode(game, m, this));
			} else {	
				int size = (int) ((int) move.atkArmy*p);
				Move m = new Move(move.attacker, move.defender, size, 0);
				if (m.atkArmy > 0) children.add(new GameNode(game, m, this));				
			}
		}
	}
	
	@Override 
	public int compareTo(PNode n2) {
		//might need to add UCB here
		return Double.compare(average, n2.average);		
	}
	
	public List<Double> simulateOutcomes(int atk, int def) {
		
		int[] outcomesArr = {0,0,0,0,0, 0};
		
		int numOfSimulations = 100000;
		
		for (int i = 0; i < numOfSimulations; i++) {		
			
			int currentAtk = atk;
			int currentDef = def;
			
			while (currentAtk > 0 && currentDef > 0) {
				
				if (currentAtk > 3) {
					
					fillAtkDiceAndSortWith(3);
					
					if (currentDef > 2) {
						
						fillDefDiceAndSortWith(2);
						
						for (int k = 0; k < 2; k++) {
							if (this.atkDice.get(k) > this.defDice.get(k)) {
								currentDef -= 1;
							} else 
							if (this.atkDice.get(k) == this.defDice.get(k)) {
								currentAtk -= 1; 
							} else 
							if (this.atkDice.get(k) < this.defDice.get(k)) {
								currentAtk -= 1;
							}
						}
						
					} else {
						
						fillDefDiceAndSortWith(1);
						
						for (int k = 0; k < 1; k++) {
							if (this.atkDice.get(k) > this.defDice.get(k)) {
								currentDef -= 1;
							} else 
							if (this.atkDice.get(k) == this.defDice.get(k)) {
								currentAtk -= 1; 
							} else 
							if (this.atkDice.get(k) < this.defDice.get(k)) {
								currentAtk -= 1;
							}
						}
					}
				} else 
				if (currentAtk > 2) {
					
					fillAtkDiceAndSortWith(2);
					
					if (currentDef > 2) {
						
						fillDefDiceAndSortWith(2);
						
						for (int k = 0; k < 2; k++) {
							if (this.atkDice.get(k) > this.defDice.get(k)) {
								currentDef -= 1;
							} else 
							if (this.atkDice.get(k) == this.defDice.get(k)) {
								currentAtk -= 1; 
							} else 
							if (this.atkDice.get(k) < this.defDice.get(k)) {
								currentAtk -= 1;
							}
						}
					} else {
						
						fillDefDiceAndSortWith(1);
						
						for (int k = 0; k < 1; k++) {
							if (this.atkDice.get(k) > this.defDice.get(k)) {
								currentDef -= 1;
							} else 
							if (this.atkDice.get(k) == this.defDice.get(k)) {
								currentAtk -= 1; 
							} else 
							if (this.atkDice.get(k) < this.defDice.get(k)) {
								currentAtk -= 1;
							}
						}
					}
				} else {
					
					fillAtkDiceAndSortWith(1);
					fillDefDiceAndSortWith(1);
					
					for (int k = 0; k < 1; k++) {
						if (this.atkDice.get(k) > this.defDice.get(k)) {
							currentDef -= 1;
						} else 
						if (this.atkDice.get(k) == this.defDice.get(k)) {
							currentAtk -= 1; 
						} else 
						if (this.atkDice.get(k) < this.defDice.get(k)) {
							currentAtk -= 1;
						}
					}
				}
				
				//testWithPrint(atk,def,currentAtk,currentDef,atkDice,defDice);
				
				emptyAtkDice();
				emptyDefDice();
		
				if (currentAtk == 0 || currentDef == 0) {
					double percentRemaining = (double) currentAtk/ (double) atk;
					if (percentRemaining == 1.0) {
						outcomesArr[5] += 1;
					} else
					if (percentRemaining >= 0.8) {
						outcomesArr[4] += 1;
				
					} else 
					if (percentRemaining < 0.8 && percentRemaining >= 0.6 ) {
						outcomesArr[3] += 1;
				
					} else 
					if (percentRemaining < 0.6 && percentRemaining >= 0.4) {
						outcomesArr[2] += 1;
					
					} else
					if (percentRemaining < 0.4 && percentRemaining >= 0.2) {
						outcomesArr[1] += 1;
			
					} else {
						outcomesArr[0] += 1;
		
					}
				}

			}
			
			
			
		}

		List<Double> outcomesLst = new ArrayList<Double>();
		
		for (int z = 0; z < outcomesArr.length; z++) {
			double prob = (double) outcomesArr[z]/numOfSimulations;
			if (prob != 0.0) {
				outcomesLst.add(prob);
			}
//			outcomesLst.add((double) outcomesArr[z]/numOfSimulations);
		}
		
		return outcomesLst;
	}
	
	void fillAtkDiceAndSortWith(int count) {
		for (int k = 0; k < count; k++) {
			this.atkDice.add((int) (Math.random() * 6 + 1));
		}
		Collections.sort(this.atkDice, Collections.reverseOrder());
	}
	
	void emptyAtkDice() {
		this.atkDice.removeAll(this.atkDice);
	}
	
	void fillDefDiceAndSortWith(int count) {
		for (int k = 0; k < count; k++) {
			this.defDice.add((int) (Math.random() * 6 + 1));
		}
		Collections.sort(this.defDice, Collections.reverseOrder());
	}
	
	void emptyDefDice() {
		this.defDice.removeAll(this.defDice);
	}
	
	void testWithPrint(int startAtk, int startDef, int finalAtk, int finalDef, List<Integer> atkDice, List<Integer> defDice) {
		
		System.out.println("\n ********Roll********");
		System.out.print("Start: " + startAtk + "v" + startDef);
		System.out.print("  Attacker Dice: ");
//		atkDice.forEach(die -> System.out.print(die + ","));
		System.out.print("  Defender Dice: ");
//		defDice.forEach(die -> System.out.print(die + ","));
		System.out.print("  Attacker Percent Remaining: " + (double) finalAtk/ (double) startAtk);
		System.out.print("  Defender Percent Remaining: " + (double) finalDef/ (double) startDef);
		if (finalAtk == 0 || finalDef == 0) {
			System.out.println("\n WINNNER!");
			if (finalAtk > finalDef) {
				System.out.print("  Attaker Wins: " + finalAtk + " to " + finalDef);
			} else 
			if (finalAtk < finalDef) {
				System.out.print("  Defender Wins: " + finalDef + " to " + finalAtk);
			} else {
				System.out.print("  Draw: " + finalDef + " to " + finalAtk);
			}
		}
	}
	
}

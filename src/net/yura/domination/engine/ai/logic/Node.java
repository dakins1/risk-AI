package net.yura.domination.engine.ai.logic;

import java.util.List;

public abstract class Node implements Comparable<Node> {

	abstract public Node bestUCBChild();
	abstract public void generateChildren();
	public boolean isExpanded;
	public int simsCount;
	public Node parent;
	public boolean terminalState;
	public List<Node> children;
	public double prob;
	public abstract double ucb();
	public abstract double weightedValueWithSim();
	public abstract double weightedValue();
	public double totalChildValue;
	public boolean isVisited;
	public Move move;
	
	@Override 
	public int compareTo(Node n2) {
		return Double.compare(ucb(), n2.ucb());		
	}
//	public double weightedValueWithSim;
//	public int compareTo(GameNode n2) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
}

package net.yura.domination.engine.ai.logic;

import java.util.List;

public abstract class Node implements Comparable<Node> {

	abstract public Node bestUCBChild();
	abstract public void generateChildren();
	private boolean isExpanded;
	private int simsCount;
	private Node parent;
	private boolean terminalState;
	private List<Node> children;
	private double prob;
	public abstract double ucb();
	public abstract double weightedValueWithSim();
	public abstract double weightedValue();
	private double totalChildValue;
	private boolean isVisited;
	private Move move;
	private double heuristic;
	
	public void eliminateEndAttackNode() {
		int dirtyBoy = 0;
		for (Node n : children){
			if (n.move.atkArmy == -1) break;
			dirtyBoy++;
		}
		children.remove(dirtyBoy);
	}
	
	@Override 
	public int compareTo(Node n2) {
		return Double.compare(ucb(), n2.ucb());		
	}
	
	public boolean getIsExpanded() {
		return this.isExpanded;
	}
	
	public void setIsExpanded(boolean b) {
		this.isExpanded = b;
	}
	
	public int getSimsCount() {
		return this.simsCount;
	}
	
	public void setSimsCount(int c) {
		this.simsCount = c;
	}
	
	public Node getParent() {
		return this.parent;
	}
	
	public void setParent(Node p) {
		this.parent = p;
	}
	
	public boolean getTerminalState() {
		return this.terminalState;
	}
	
	public void setTerminalState(boolean t) {
		this.terminalState = t;
	}
	
	public void setChildren(List<Node> c) {
		this.children = c;
	}
	
	public List<Node> getChildren() {
		return this.children;
	}
	
	public double getProb() {
		return this.prob;
	}
	
	public void setProb(double p) {
		this.prob = p;
	}
	
	public boolean getIsVisited() {
		return this.isVisited;
	}
	
	public void setIsVisisted(boolean v) {
		this.isVisited = v;
	}
	
	public Move getMove() {
		return this.move;
	}
	
	public void setMove(Move m) {
		this.move = m;
	}
	
	public double getHeuristic() {
		return this.heuristic;
	}
	
	public void setHeuristic(double h) {
		this.heuristic = h;
	}
	
	public double getTotalChildValue() {
		return this.totalChildValue;
	}
	
	public void setTotalChildValue(double v) {
		this.totalChildValue = v;
	}
	
//	public double weightedValueWithSim;
//	public int compareTo(GameNode n2) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
}

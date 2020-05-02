package net.yura.domination.engine.ai.logic;

import java.util.ArrayList;
import java.util.List;

import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class PNode {

	public Move move;
	public RiskGame game;
	public Player player;
	public List<GameNode> children;
	public double average; //weighted avg of rewards and probabilities
	
	public PNode(Move move, RiskGame game) {
		//Might need to be a deep copy??? Might not since we're not mutating anything
		this.game = game;
		this.player = game.getCurrentPlayer();
		this.move = move;
		children = new ArrayList<GameNode>();
	}
	
}

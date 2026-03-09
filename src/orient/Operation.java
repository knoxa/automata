package orient;

import java.util.HashSet;
import java.util.Set;

import act.Action;

public class Operation {

	private Set<Action> actions;
	private Set<Integer> agents;
	private String label;
	private double preference;

	public Operation() {
		
		this.setLabel("*");
		actions = new HashSet<>();
		agents  = new HashSet<>();
		preference = 0.5;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public void addAction(Action action) {
		actions.add(action);
	}

	public Set<Integer> getAgents() {
		return agents;
	}

	public void addAgent(Integer agent) {
		agents.add(agent);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getPreference() {
		return preference;
	}

	public void setPreference(double preference) {
		this.preference = preference;
	}

	@Override
	public String toString() {
		return label + ". Affects " + agents + " - Priority = " + getPreference();
	}
	
}

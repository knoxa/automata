package tiles;

import java.util.List;

import act.Action;

public class PentominoMove {

	// Hold a list of actions that would exchange squares between neighbouring pentominoes,
	// together with the types of pentominoes that would result.
	
	private List<Action> actions;
	private List<PentominoType> result;

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public void addAction(Action action) {
		actions.add(action);
	}

	public List<PentominoType> getResult() {
		return result;
	}

	public void setResult(List<PentominoType> result) {
		this.result = result;
	}

}

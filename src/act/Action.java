package act;

import java.util.Collection;

import cells.Sense;
import cells.Square;
import tiles.Tile;

public class Action {

	Square actor;
	Sense sense;
	Act act;
	double probability = 1.0;
	
	
	public Square getActor() {
		return actor;
	}

	public void setActor(Square actor) {
		this.actor = actor;
	}

	public Sense getSense() {
		return sense;
	}

	public void setSense(Sense sense) {
		this.sense = sense;
	}

	public Act getAct() {
		return act;
	}

	public void setAct(Act act) {
		this.act = act;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		return new String(actor + " (" + act + ") " + sense + " [" + probability + "]");
	}

	public static void makeMoves(Collection<Action> options) {
		
		for ( Action action: options ) {
			
			System.out.println(".. " + action.actor + " *" + action.act +"* --> " + action.sense.getDirection() + " " + action.sense.getSquare());
					
			switch (action.act) {
			
			case ATTACH:
				Tile.attach(action.actor, action.sense.getDirection(), action.sense.getSquare());
				break;
				
			case DETACH:
				
				if (action.sense.getSquare() == null)  Tile.detach(action.actor);
				else Tile.detach(action.actor, action.sense.getDirection(), action.sense.getSquare());
				break;
				
			case CAPTURE:
				Tile.capture(action.actor, action.sense.getDirection(), action.sense.getSquare());
				break;
				
			case DEFECT:
				Tile.defect(action.actor, action.sense.getDirection(), action.sense.getSquare());
				break;
			}
		}	
	}

}

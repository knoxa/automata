package cells;

import java.util.Set;

public class Action {

	Square actor;
	Sense sense;
	Act act;
	double probability = 0.5;
	
	
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
		return new String(actor + " (" + act + ") " + sense);
	}

	public static void attach(Square from, Direction step, Square to) {
		
		from.setNeighbour(step, to);
		Direction returnDirection = from.getReturnDirection(step);
		to.setNeighbour(returnDirection, from);
		
		// also link "from" to adjacent squares that are diagonal to "to"

		//System.out.println( from + " from.. " + from.diagonal());
		
		for ( Sense diagonal: from.diagonal(step) ) {
			
			//System.out.println(" ... " + from + " - " + diagonal.direction + ", " + diagonal.square);
			//System.out.println("   diagonal - " + from + "; " + diagonal.square );

			if ( from.getNeighbour(diagonal.direction) == null ) {
				
				from.setNeighbour(diagonal.direction, diagonal.square);
				returnDirection = from.getReturnDirection(diagonal.direction);
				diagonal.square.setNeighbour(returnDirection, from);
			}
		}

		for ( Sense diagonal: to.diagonal(returnDirection) ) {
			
			System.out.println(" ... " + to + " - " + diagonal.direction + ", " + diagonal.square);
			System.out.println("   diagonal - " + to + "; " + diagonal.square );

			if ( to.getNeighbour(diagonal.direction) == null ) {
				
				to.setNeighbour(diagonal.direction, diagonal.square);
				returnDirection = to.getReturnDirection(diagonal.direction);
				diagonal.square.setNeighbour(returnDirection, to);
			}
		}
	}
	
	public static void detach(Square from, Direction step, Square to) {
		
		from.clearNeighbour(step);
	}
	
	public static void detach(Square square) {
		
		square.clearNeighbours();
	}
	
	public static void capture(Square from, Direction step, Square to) {
		
		to.clearNeighbours();
		attach(from, step, to);
	}
	
	public static void defect(Square from, Direction step, Square to) {
		
		from.clearNeighbours();
		attach(from, step, to);
	}

	public static void makeMoves(Set<Action> options) {
		
		for ( Action action: options ) {
			
			System.out.println(".. " + action.actor + " *" + action.act +"* --> " + action.sense.direction + " " + action.sense.square);
					
			switch (action.act) {
			
			case ATTACH:
				Action.attach(action.actor, action.sense.direction, action.sense.square);
				break;
				
			case DETACH:
				Action.detach(action.actor);
				break;
				
			case CAPTURE:
				Action.capture(action.actor, action.sense.direction, action.sense.square);
				break;
				
			case DEFECT:
				Action.defect(action.actor, action.sense.direction, action.sense.square);
				break;
			}
		}	
	}

}

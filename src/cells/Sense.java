package cells;

public class Sense {
	
	Direction direction = null;
	Square square = null;

	
	public Sense(Direction direction, Square square) {
		this.direction = direction;
		this.square = square;
	}

	public Sense() {
	}

	public Direction getDirection() {
		return direction;
	}


	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Square getSquare() {
		return square;
	}

	public void setSquare(Square square) {
		this.square = square;
	}

	@Override
	public String toString() {
		return new String(direction + " --> " + square);
	}
}

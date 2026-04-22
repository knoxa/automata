package worlds;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import cells.Direction;
import cells.Sense;
import cells.Square;
import observe.BoardObserver;
import xslt.BaseFilter;

public class BoardFilter extends BaseFilter {
	
	private Board board;
	private Square[][] grid;
	private Square currentSquare;
	private Map<Square, Set<Sense>> environment;

	@Override
	public void startElement(String uri, String localName, String qname, Attributes attr) throws SAXException {
			
		if ( qname.equals("board") ) {
			
			String width  = attr.getValue("width");
			String height = attr.getValue("height");
			
			this.board = new Board(Integer.parseInt(width), Integer.parseInt(height));
			grid = board.getGrid();
			environment = BoardObserver.lookAbout(board);
		}
		else if ( qname.equals("square") ) {
				
			String row = attr.getValue("row");
			String col = attr.getValue("col");		
			String label = attr.getValue("label");
			
			currentSquare = grid[Integer.parseInt(row)][Integer.parseInt(col)];
			if ( label != null )  currentSquare.setLabel(label);
		}
		else if ( qname.equals("identifier") ) {
			
			// ignore the identifer 
		}
		else {
			
			// a direction
			
			Set<Sense> surroundings = environment.get(currentSquare);
			Map<Direction, Square> thisSquareSees = new EnumMap<>(Direction.class);
			for ( Sense sense: surroundings ) thisSquareSees.put(sense.getDirection(), sense.getSquare());
			
			switch ( qname ) {
			
				case "NORTH":
					currentSquare.setNeighbour(Direction.NORTH, thisSquareSees.get(Direction.NORTH));
					break;
					
				case "SOUTH":				
					currentSquare.setNeighbour(Direction.SOUTH, thisSquareSees.get(Direction.SOUTH));
					break;
					
				case "EAST":				
					currentSquare.setNeighbour(Direction.EAST, thisSquareSees.get(Direction.EAST));
					break;
					
				case "WEST":				
					currentSquare.setNeighbour(Direction.WEST, thisSquareSees.get(Direction.WEST));
					break;
			}
		}
		
		super.startElement(uri, localName, qname, attr);
	}
	

	public Board getBoard() {		
		return board;
	}
}

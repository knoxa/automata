package worlds;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import cakes.category.Maps;
import cells.Direction;
import cells.Square;
import graph.GraphUtils;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;

public class Board {

	private Square[][] grid;
	private int width, height;
	private Set<Square> squares;

	public Board(int width, int height) {

		this.width = width; this.height = height;
		grid = new Square[height][width];
		
		initialize();
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void initialize() {
		
		squares = new HashSet<Square>();
		
		for ( int col = 0; col < width; col++) {		
			
			for ( int row = 0; row < height; row++) {

				grid[row][col] = new Square();
				String label = String.format("(%d,%d)", col, row);
				grid[row][col].setLabel(label);
				squares.add(grid[row][col]);
			}
		}
	}

	public Set<Square> getSquares() {
		return squares;
	}

	public Square[][] getGrid() {
		return grid;
	}
	
	
	public void serialize(ContentHandler ch) throws SAXException {
		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(getSquares());
		Map<Square, Set<Integer>> reverse = Maps.invertMap(partitionMap);
		
		ch.startDocument();
		
		ch.startElement("", "board", "board", new AttributesImpl());
		
		for ( int h = 0; h < height; h++ ) {
			
			for ( int w = 0; w < width; w++ ) {
							
				Square square = grid[h][w];
				int partNo = reverse.get(square).iterator().next();
				Set<Square> tile = partitionMap.get(partNo);

				AttributesImpl attr = new AttributesImpl();
				attr.addAttribute("", "row", " row",  "Integer",  String.valueOf(h));
				attr.addAttribute("", "col",  "col",  "Integer",  String.valueOf(w));
				attr.addAttribute("", "tile", "tile", "Integer",  String.valueOf(partNo));
				attr.addAttribute("", "label", "label", "String",  square.getLabel());
				
				if ( tile.size() == 5 ) {
					
					PentominoType pentomino = Pentomino.identifyPentomino(tile);
					attr.addAttribute("", "pentomino", "pentomino", "String",  pentomino.toString());
				}

				ch.startElement("", "square", "square", attr);
				
				for ( Direction direction: square.getNeighbourMap().keySet() ) {
					
					String name = direction.toString();
					ch.startElement("", name, name, new AttributesImpl());
					ch.endElement("", name, name);					
				}
							
				ch.endElement("", "square", "square");
			}
		}
		
		ch.endElement("", "board", "board");
		ch.endDocument();
	}
}

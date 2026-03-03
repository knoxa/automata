package worlds;

import java.util.Map;
import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import cakes.category.Maps;
import cells.Direction;
import cells.Square;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;

public class BoardManager {

	public static void serialize(Board board, ContentHandler ch) throws SAXException {
		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<Square, Set<Integer>> reverse = Maps.invertMap(partitionMap);
		
		Square[][] grid = board.getGrid();
		
		ch.startDocument();
		
		AttributesImpl attr = new AttributesImpl();
		attr.addAttribute("", "width", " width",  "Integer",  String.valueOf(board.getHeight()));
		attr.addAttribute("", "height",  "height",  "Integer",  String.valueOf(board.getWidth()));
		ch.startElement("", "board", "board", attr);
		
		for ( int h = 0; h < board.getHeight(); h++ ) {
			
			for ( int w = 0; w < board.getWidth(); w++ ) {
							
				Square square = grid[h][w];
				int partNo = reverse.get(square).iterator().next();
				Set<Square> tile = partitionMap.get(partNo);

				attr = new AttributesImpl();
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

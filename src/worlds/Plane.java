package worlds;

import java.util.Map;
import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import cells.Direction;
import cells.Square;

public class Plane {

	
	public static void serialize(Map<Square, Integer[]> coordinates, Map<Integer, Map<Integer, Set<Square>>> positions, ContentHandler ch) throws SAXException {
		
		ch.startDocument();
		
		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE, xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;
		
		for ( Square square: coordinates.keySet() ) {
			
			Integer[] coords = coordinates.get(square);			
			xMin = coords[0] < xMin ? coords[0] : xMin;
			xMax = coords[0] > xMax ? coords[0] : xMax;
			yMin = coords[1] < yMin ? coords[1] : yMin;
			yMax = coords[1] > yMax ? coords[1] : yMax;
		}
		
		AttributesImpl attr = new AttributesImpl();
		ch.startElement("", "plane", "plane", attr);
		
		for ( int h = 0; h <= yMax - yMin; h++ ) {
			
			Map<Integer, Set<Square>> row = positions.get(h + yMin);
			
			for ( int w = 0; w <= xMax - xMin; w++ ) {
							
				Set<Square> squares = row.get(w + xMin);
				
				if ( squares != null ) {
					
					Square square = squares.iterator().next();

					attr = new AttributesImpl();
					attr.addAttribute("", "row", " row",  "Integer",  String.valueOf(h));
					attr.addAttribute("", "col",  "col",  "Integer",  String.valueOf(w));
					attr.addAttribute("", "label", "label", "String",  square.getLabel());

					ch.startElement("", "square", "square", attr);
					
					for ( Direction direction: square.getNeighbourMap().keySet() ) {
						
						String name = direction.toString();
						ch.startElement("", name, name, new AttributesImpl());
						ch.endElement("", name, name);					
					}
								
					ch.endElement("", "square", "square");
				}
			}
		}
		
		ch.endElement("", "plane", "plane");
		ch.endDocument();
	}


}

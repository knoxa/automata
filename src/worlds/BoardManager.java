package worlds;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

import cakes.category.Maps;
import cells.Direction;
import cells.Square;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import xslt.Pipeline;

public class BoardManager {

	public static void serialize(Board board, ContentHandler ch) throws SAXException {
		
		Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
		Map<Square, Set<Integer>> reverse = Maps.invertMap(partitionMap);
		
		Square[][] grid = board.getGrid();
		String identifier = identifySolution(grid);
		
		ch.startDocument();
		
		AttributesImpl attr = new AttributesImpl();
		attr.addAttribute("", "width", " width",  "Integer",  String.valueOf(board.getWidth()));
		attr.addAttribute("", "height",  "height",  "Integer",  String.valueOf(board.getHeight()));
		ch.startElement("", "board", "board", attr);

		ch.startElement("", "identifier", "identifier", new AttributesImpl());
		ch.characters(identifier.toCharArray(), 0, identifier.length());
		ch.endElement("", "identifier", "identifier");

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

	
	public static Board loadFromXml(InputStream input) throws ParserConfigurationException, SAXException, IOException {
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		
		BoardFilter filter = new BoardFilter();
		reader.setContentHandler(filter);
		reader.parse(new InputSource(input));
		
		return filter.getBoard();

	}


	public static void reportPartitions(Map<Integer, Set<Square>> partitionMap, Board board) {
				
		int[][] values = new int[board.getHeight()][board.getWidth()];
		Square[][] grid = board.getGrid();
				
		Map<Square, Set<Integer>> reverse = cakes.category.Maps.invertMap(partitionMap);
		System.out.println(partitionMap.keySet().size() + " partitions");
	
		for ( int r = 0; r < board.getHeight(); r++ ) {
			
			for ( int c = 0; c < board.getWidth(); c++ ) {
				
				Square s = grid[r][c];
				values[r][c] = reverse.get(s).iterator().next();
			}
			
		}
	
	
		for ( int[] row: values ) {
			
			for ( int cell: row ) {
				
				System.out.print(cell + "\t" );
			}
			System.out.println();
		}
	}

	
	public static void writeBoardToFile(Board board, FileOutputStream file) {
			
		Pipeline p = new Pipeline();
		
		try {
			p.setOutput(file);
			BoardManager.serialize(board, p.getContentHandler());

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String identifySolution(Square[][] grid) {
		
		int rows = grid.length;
		int cols = grid[0].length;
		
		StringBuffer bufferA = new StringBuffer();
		StringBuffer bufferB = new StringBuffer();
		StringBuffer bufferC = new StringBuffer();
		StringBuffer bufferD = new StringBuffer();
		
		for ( int col = 0; col < cols; col++ ) {
			
			for ( int row = 0; row < rows; row++ ) {
				
				bufferA.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[row][col])));
				bufferB.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[row][cols - col - 1])));
				bufferC.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[rows - row - 1][col])));
				bufferD.append(Pentomino.identifyPentomino(Partitioner.getTileContaining(grid[rows - row - 1][cols - col - 1])));
			}
			
			bufferA.append(' '); bufferB.append(' '); bufferC.append(' '); bufferD.append(' ');
		}
		
		List<String> list = new ArrayList<>();
		list.add(bufferA.toString()); list.add(bufferB.toString()); list.add(bufferC.toString()); list.add(bufferD.toString());
		Collections.sort(list);
		return list.get(0).trim();
	}
	
}

package experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import cakes.files.FileQueueManager;
import cakes.files.ReadFiles;
import cells.Square;
import orient.Partitioner;
import tiles.Pentomino;
import tiles.PentominoType;
import worlds.Board;
import worlds.BoardManager;
import xslt.Pipeline;

public class Classify {

	public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerConfigurationException {

		File file = new File("solutions.txt"); // downloaded from https://isomerdesign.com/Pentomino/6x10/solutions.txt
		List<String> solutions = FileUtils.readLines(file, "windows-1252");
		
		Map<String, String> solutionMap = new HashMap<String, String>();
		
		for ( String solution: solutions ) {
			
			String[] text = solution.split(",");
			solutionMap.put(text[1].trim(), text[0].trim());
		}
		
		System.out.println(solutionMap);
		
		File dir = new File("experiments");
		
		ReadFiles.start(dir);
		
		while ( (file = FileQueueManager.getFileQueue().poll(FileQueueManager.FILE_QUEUE_TIMEOUT, TimeUnit.SECONDS)) != null ) {
			
			if ( file.getName().endsWith(".xml") ) {
				
				System.out.println(file.getName());
				Board board = BoardManager.loadFromXml(new FileInputStream(file));
				Map<Integer, Set<Square>> partitionMap = Partitioner.partition(board.getSquares());
				
				if ( partitionMap.keySet().size() == 12 ) {
					
					Map<PentominoType, Set<Integer>> pentominoes = Pentomino.getPentominoes(partitionMap);
					
					if ( pentominoes.keySet().size() == 12 ) {
						
						String identifier = BoardManager.identifySolution(board.getGrid());
						
						System.out.println(file.getName() + " .. " + identifier + " = " + solutionMap.get(identifier));
						
						Pipeline p = new Pipeline();
						p.setOutput(new FileOutputStream("output/" + solutionMap.get(identifier) + ".xml"));
						BoardManager.serialize(board, p.getContentHandler());

					}
				}

			}
			
		}
	}

}

package parser;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import struct.Edge;
import struct.SocialNetwork;
import struct.Vertex;

public class SubsetLineParser extends LineParser {
	
	static Logger log = Logger.getLogger(SubsetLineParser.class.getName());

	private static final Integer MAX_ID = 2000;

	@Override
	public void parseLine(SocialNetwork sn, String line) {
		StringTokenizer tokenizer = new StringTokenizer(line);
		
		// Cada linea valida tiene dos enteros FromNodeId y ToNodeId
		if (tokenizer.countTokens() >= 2){
			Integer id1 = Integer.parseInt(tokenizer.nextToken());
			Integer id2 = Integer.parseInt(tokenizer.nextToken());
			
			if (id1 > MAX_ID || id2 > MAX_ID)
				return;
			
			// Vertices
			Vertex v1 = sn.addVertex(new Vertex(id1));
			Vertex v2 = sn.addVertex(new Vertex(id2));			
			
			// Peso (opcional, puede generarse aleatoriamente)
			if (tokenizer.hasMoreTokens()){
				Double weight = Double.parseDouble(tokenizer.nextToken());
				
				// Arista con peso
				sn.addEdge(new Edge(v1, v2, weight));
			} else {
				// Arista sin peso
				sn.addEdge(new Edge(v1, v2));
			}
		} else {
			log.error("Linea invalida");
		}
	}

}

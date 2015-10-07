package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import propagation.ltm.LTMLineParser;
import struct.Edge;
import struct.SocialNetwork;
import struct.Vertex;

public class SimpleFileParser extends FileParser {

	// TODO Line parser
	LineParser lp = new LTMLineParser();
	//LineParser lp = new LTMLineParser();
	
	@Override
	public void parseFile(File file, SocialNetwork sn) {
		System.out.println("[SimpleFileParser]");
		System.out.println(" Archivo: " + file.getName());
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			
			while((line = br.readLine()) != null ){	
				// Las lineas con # al comienzo son comentarios
				if (! line.startsWith("#")){
					lp.parseLine(sn, line);
				}
			}

			br.close();
			
			System.out.println(" Archivo parseado correctamente");
			System.out.println(" - Vertices: " + sn.getVerticesCount());
			System.out.println(" - Aristas: " + sn.getEdgeCount());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

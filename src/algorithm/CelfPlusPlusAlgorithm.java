package algorithm;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import algorithm.spread.SpreadCalculator;
import propagation.PropagationModel;
import struct.SocialNetwork;
import struct.Vertex;

public class CelfPlusPlusAlgorithm extends MaximizationAlgorithm {

	static Logger log = Logger.getLogger(CelfPlusPlusAlgorithm.class.getName());
	
	@Override
	public Set<Vertex> maximize(SocialNetwork sn, SpreadCalculator spread, PropagationModel model, Integer n) {
		log.info("[CELF++ Algorithm]");
		updateProgress(0);
		
		Set<Vertex> vertices = sn.getVertices();
		SortedSet<CelfPlusPlusVertex> Q = new TreeSet<CelfPlusPlusVertex>(); // Q = new Pila<CelfPlusPlusVertex>
		Set<Vertex> S = new HashSet<Vertex>(); // S = new HashSet<Vertex>
		CelfPlusPlusVertex last_seed = null;
		CelfPlusPlusVertex cur_best = null;

		int j = 1;
		for (Vertex v : vertices) {
			CelfPlusPlusVertex u = new CelfPlusPlusVertex(v);
			Set<Vertex> seed = new HashSet<Vertex>();
			seed.add(v);
			u.mg1 = spread.calculateSpread(sn, seed, model);
			u.prev_best = cur_best;
			if (cur_best != null) {
				seed.add(cur_best.vertex);
				u.mg2 = spread.calculateSpread(sn, seed, model) - u.mg1;
			} else {
				u.mg2 = u.mg1;
			}
			u.flag = 0;

			Q.add(u);
			
			if (cur_best == null || cur_best.mg1 < u.mg1) {
				cur_best = u;
			}
			
			int a = j * 100;
			int b = vertices.size() * n;
			updateProgress(a / b);
			j++;
		}

		while (S.size() < n) {
			CelfPlusPlusVertex u = Q.last();
			
			// Eliminar elemento. Si no se selecciona, luego se reinsertara en la posicion correspondiente
			if (!Q.remove(Q.last())) {
				log.error("Error al eliminar");				
			}
			
			log.trace("Revisar: " + u.vertex);
			if (u.flag == S.size()) {
				Vertex v = u.vertex;
				log.warn(" - Seleccionar: " + v + "(marginal " + u.mg1 + ")");
				S.add(v);
				last_seed = u;
				cur_best = null;
			} else { 
				if (u.prev_best == last_seed) {
					log.trace(" - Marginal ya calculada");
					u.mg1 = u.mg2;
				} else {
					log.trace(" - Calcular marginal");
					u.mg1 = getMarginal(sn, spread, model, S, u.vertex); //delta u (S);
					u.prev_best = cur_best;
					
					if (cur_best != null){
						S.add(cur_best.vertex);
						u.mg2 = getMarginal(sn, spread, model, S, u.vertex); // delta u ( S U {cur_best});
						S.remove(cur_best.vertex);
					} else {
						u.mg2 = u.mg1;
					}
				}
				u.flag = S.size();
				
				// Update cur_best
				if (cur_best == null || cur_best.mg1 < u.mg1) {
					cur_best = u;
				}

				// Reinsertar u en Q
				if(!Q.add(u)) {
					log.error("Error al reinsertar");
				}
			}

			updateProgress(S.size() * 100 / n);
		}
		
		return S;
	}

	@Override
	public MaximizationAlgorithm instance() {
		return new CelfPlusPlusAlgorithm();
	}

}

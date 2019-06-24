package it.polito.tdp.newufosightings.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	private NewUfoSightingsDAO dao;
	private SimpleWeightedGraph<State, DefaultWeightedEdge> grafo;
	private List<State> states;
	List<Confine> confini;
	private Map<String, State> stateIdMap;
	
	Simulatore sim = new Simulatore();
	
	

	public Model() {
		this.dao = new NewUfoSightingsDAO();
		this.states = new ArrayList<State>();
		confini = new ArrayList<Confine>();
		this.stateIdMap= new HashMap<String, State>();
	}


	// nel grafo mmetto tutti gli stati come vertici
	// i pesi degli archi dipendono dall'anno e dalla forma selezionati

	public List<String> getShapesByYear(Integer anno) {
		return dao.getShapesByYear(anno);
	}


	public void creaGrafo(Integer anno, String forma) {
		
		this.grafo = new SimpleWeightedGraph<State,
				DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		this.states = dao.loadAllStates(stateIdMap);
		Graphs.addAllVertices(this.grafo, this.states);
		
		this.confini = dao.getConfini();
		for(Confine c : confini) {
			State source = this.stateIdMap.get(c.getState1());
			State dest = this.stateIdMap.get(c.getState2());
			Graphs.addEdgeWithVertices(this.grafo, source, dest,
					dao.getPesoConfine(source.getId(), dest.getId(),
							anno, forma));
		}
		
		System.out.println("Grafo creato ! \n" +grafo.vertexSet().size() + 
				" vertici \n"+grafo.edgeSet().size()+" archi\n");
	}


	public Integer getNumVertexGrafo() {
		return this.grafo.vertexSet().size();
	}
	
	public Integer getNumEdgeGrafo() {
		return this.grafo.edgeSet().size();
	}

	public Map<State, Integer> getPesoArchiStati() {
		Map<State, Integer> pesoStati = new HashMap<State, Integer>();
		for(State s1: this.grafo.vertexSet()) {
			Integer sommaPesi =0;
			for(State s2 :Graphs.neighborListOf(this.grafo, s1)) {
				sommaPesi =  (int) (sommaPesi + this.grafo.getEdgeWeight(this.grafo.getEdge(s1, s2)));
			}
			pesoStati.put(s1, sommaPesi);
		}
		return pesoStati;
	}
	
	public List<Sighting> getAvvistamenti(Integer anno, String forma){
		return dao.getAvvistammentiByYearShape(anno, forma);
	}
	
	public Map<State, Double> simula(Integer anno, String forma, Integer T1, Integer alfa) {
		List<Sighting> avvistamenti = new ArrayList<Sighting>();

		avvistamenti = dao.getAvvistammentiByYearShape(anno, forma);
//		for(Sighting s : avvistamenti) {
//			System.out.println(s.toString());
//		}
		sim.init(T1, alfa, avvistamenti, grafo);
		return sim.run();
	}
}

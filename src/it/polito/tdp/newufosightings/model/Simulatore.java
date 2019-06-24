package it.polito.tdp.newufosightings.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.model.Evento.TipoEvento;

public class Simulatore {

	
	// DEFCON: LIVELLO ALLERTA
	// INCREMENTO -> ALLERTA DIMINUISCE
	// DECREMENTO -> ALLERTA AUMENTA
	
	// CODA DEGLI EVENTI
	PriorityQueue<Evento> queue =  new PriorityQueue();
	// TIPI DI EVENTO
	// VERIFCA SEMPRE->non è possibile scendere sooto allerta max e salire sopra allerta min
	//1.AVVISTAMENTO (1A AVVISTAMENTO_STATO, 1B AVVISTAMENTO_ADIACENTE)
		//1.1 DECREMENTO IL DEFCON 
		//		DI UN'UNITA' NELLO STATO IN CUI SI VERIFICA
		//1.2 CON PROBBAILITA' ALFA PUO DECREMENTARE NEGLI STATI ADIACENTI
		//1.3 AGGIUNGO ALLA CODA EVENTO CESSATA ALLERTA DOPO T1 PER STATO
		// E PER STATI ADIACENTI
	//2.CESSATA_ALLERTA (2A CESSATA_ALLERTA, 2B CESSATA_ALLERTA_ADIACENTE)
		//2.1 INCREMENTO IL DEFCON
		//		DI UN'UNITA' NELLO STATO IN CUI SI VERIFICA
		//1.2 INCREMENTO IL DEFCON DI 0.5 NEGLI STATI ADIACENTI TOCCATI
	
	// Struttere dati
	
	//STATISTICHE DA CALCOLARE
	// DEFCON FINAL EPER CIASCUNO STATO, GIA' NELLA MAP ALLERTASTATI
	
	// PARAMETRI DI SIMULAZIONE	
	// VERIFCA SEMPRE->
	//non è possibile scendere sooto allerta max e salire sopra allerta min
	private static Double DEFCON_MIN = 5.0;
	private static Double DEFCON_MAX = 1.0;
	private static Double DELTA_DEFCON_STATO = 1.0;
	private static Double DELTA_DEFCON_ADIACENTE = 0.5;
	
	private Integer T1 ; // INTERVALLO CESSATO ALLARME DA INIZIO IN GIORNI
	private Integer probabilita;
	
	//VARIABILI INTERNE
	private Random r = new Random();
	private List<Sighting> sightings;
	
	
	// MODELLO DEL  MONDO
	private Map<String, State> stateIdMap;
	private Map<State, Double> allertaStati; //idstato-livelloallerta
	private SimpleWeightedGraph<State, DefaultWeightedEdge> grafo;
	
	public Simulatore(){
		this.allertaStati= new HashMap<State, Double>();
		this.stateIdMap = new HashMap<String, State>(); // id map stati, identif da nome stato
		this.sightings = new ArrayList<Sighting>();
		
	}
	
	public void init(Integer T1, Integer alfa, List<Sighting> sightings,
			SimpleWeightedGraph<State, DefaultWeightedEdge> grafo) {
//		this.queue.clear();
		
		this.T1=T1;
		this.probabilita=alfa;
		this.grafo=grafo;
		this.sightings=sightings;
		
		//INIZIALIZZO TUTTI GLI STATI A DEFCON MAX
		for(State s:this.grafo.vertexSet()) {
			if(s!=null) {
				this.stateIdMap.put(s.getId(), s);
				allertaStati.put(s, this.DEFCON_MIN);
//				System.out.println(s.getId()+" "+allertaStati.get(s));				
			}
		}
		
		//CREO LA CODA
		this.queue = new PriorityQueue();
		
		// creo gli eventi iniziali
		for(Sighting s:this.sightings) {
//			System.out.println(s.toString());		
			this.queue.add(new Evento(TipoEvento.AVVISTAMENTO_STATO,
					s, s.getDatetime(), this.stateIdMap.get(s.getState().toUpperCase())));
		}
		
	}
	
	public Map<State, Double> run() {
		
		Evento e;
		String idStato;
		
		
		while((e = queue.poll())!=null){

			idStato = e.getStato().getId();
			Double DEEFCONAttuale = this.allertaStati.get(e.getStato());
//			System.out.println(e.getStato()+" "+DEEFCONAttuale);
			switch(e.getTipo()) {
			
				case AVVISTAMENTO_STATO:
					System.out.println(e.getStato()+" "+allertaStati.get(e.getStato()));
					// verifico defcon entro limiti
					if(DEEFCONAttuale>=DEFCON_MAX+DELTA_DEFCON_STATO) {
						// diminuisco defcon di 1
						this.allertaStati.put(e.getStato(),
								this.allertaStati.get(e.getStato())-DELTA_DEFCON_STATO);
					} 
					// calcolo avvistamenti adiacenti ed eventualmente aggiungo
					// evento AVVISTAMENTO_ADIACENTE
					for(State adiacente:Graphs.neighborListOf(this.grafo,
							this.stateIdMap.get(idStato))) {
						boolean avvistamentoAdiacente = false;
						avvistamentoAdiacente = verificaAdiacenzaAvvistamento(adiacente);
						if(avvistamentoAdiacente==true) {
							// contemporaneo
							this.queue.add(new Evento(TipoEvento.AVVISTAMENTO_ADIACENTE,
									e.getAvvistamento(), e.getData(), adiacente));
						}
					}
					
					// GENERO EVENTO CESSATA_ALLERTA_STATO
					// dopo tempo T1
					this.queue.add(new Evento(TipoEvento.CESSATA_ALLERTA_STATO, e.getAvvistamento(),
							e.getData().plusDays(T1), e.getStato()));
					break;
					
				case AVVISTAMENTO_ADIACENTE:
					System.out.println(e.getStato()+" "+allertaStati.get(e.getStato()));
					// verifico defcon entro limiti
					if(DEEFCONAttuale>=DEFCON_MAX+DELTA_DEFCON_ADIACENTE) {
						// diminuisco defcon di 0.5
						this.allertaStati.put(e.getStato(),
								this.allertaStati.get(e.getStato())-DELTA_DEFCON_ADIACENTE);
					}
					
					// GENERO EVENTO CESSATA_ALLERTA_ADIACENTE
					// dopo tempo T1
					this.queue.add(new Evento(TipoEvento.CESSATA_ALLERTA_ADIACENTE, e.getAvvistamento(),
							e.getData().plusDays(T1), e.getStato()));
					break;
					
				case CESSATA_ALLERTA_STATO:
					System.out.println(e.getStato()+" "+DEEFCONAttuale+" CESSATA_ALLERTA_STATO");
					// verifico defcon entro limiti
					if(DEEFCONAttuale<=DEFCON_MIN-DELTA_DEFCON_STATO) {
						// aumento defcon di 1
						this.allertaStati.put(e.getStato(),
								this.allertaStati.get(e.getStato())+DELTA_DEFCON_STATO);
					} 
					break;
					
				case CESSATA_ALLERTA_ADIACENTE:
					System.out.println(e.getStato()+" "+allertaStati.get(e.getStato()));
					// verifico defcon entro limiti
					if(DEEFCONAttuale<=DEFCON_MIN-DELTA_DEFCON_ADIACENTE) {
						// aumento defcon di 0.5
						this.allertaStati.put(e.getStato(),
								this.allertaStati.get(e.getStato())+DELTA_DEFCON_ADIACENTE);
						System.out.println("NUOVA ALLERTA"+allertaStati.get(e.getStato()));
					}  
					break;
			}
		}
		
		System.out.println("SIMULAZIONE TERMINATA!\n");
		return this.allertaStati;		
	}

	private boolean verificaAdiacenzaAvvistamento(State adiacente) {
		if(r.nextDouble()>(this.probabilita.doubleValue())) {
			return false;
		} else
			return true;
	}
}

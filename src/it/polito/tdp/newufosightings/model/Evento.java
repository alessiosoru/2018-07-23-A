package it.polito.tdp.newufosightings.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Evento implements Comparable<Evento> {
	
	public enum TipoEvento{
		AVVISTAMENTO_STATO,
		AVVISTAMENTO_ADIACENTE,
		CESSATA_ALLERTA_STATO,
		CESSATA_ALLERTA_ADIACENTE
	}
	
	private TipoEvento tipo;
	private Sighting avvistamento;
	private LocalDateTime data;
	private State stato;
	public Evento(TipoEvento tipo, Sighting avvistamento, LocalDateTime data, State stato) {
		super();
		this.tipo = tipo;
		this.avvistamento = avvistamento;
		this.data = data;
		this.stato = stato;
	}
	public TipoEvento getTipo() {
		return tipo;
	}
	public void setTipo(TipoEvento tipo) {
		this.tipo = tipo;
	}
	public Sighting getAvvistamento() {
		return avvistamento;
	}
	public void setAvvistamento(Sighting avvistamento) {
		this.avvistamento = avvistamento;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	public State getStato() {
		return stato;
	}
	public void setStato(State stato) {
		this.stato = stato;
	}
	@Override
	public String toString() {
		return "Evento [tipo=" + tipo + ", avvistamento=" + avvistamento + ", data=" + data + ", stato=" + stato + "]";
	}
	@Override
	public int compareTo(Evento other) {
		return this.data.compareTo(other.data);
	}
	
	
}

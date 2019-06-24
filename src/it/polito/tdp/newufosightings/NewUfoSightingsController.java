/**
 * Sample Skeleton for 'NewUfoSightings.fxml' Controller Class
 */

package it.polito.tdp.newufosightings;

import java.net.URL;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.newufosightings.model.Model;
import it.polito.tdp.newufosightings.model.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NewUfoSightingsController {

	private Model model;
	
	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	@FXML // fx:id="txtAnno"
	private TextField txtAnno; // Value injected by FXMLLoader

	@FXML // fx:id="btnSelezionaAnno"
	private Button btnSelezionaAnno; // Value injected by FXMLLoader

	@FXML // fx:id="cmbBoxForma"
	private ComboBox<String> cmbBoxForma; // Value injected by FXMLLoader

	@FXML // fx:id="btnCreaGrafo"
	private Button btnCreaGrafo; // Value injected by FXMLLoader

	@FXML // fx:id="txtT1"
	private TextField txtT1; // Value injected by FXMLLoader

	@FXML // fx:id="txtAlfa"
	private TextField txtAlfa; // Value injected by FXMLLoader

	@FXML // fx:id="btnSimula"
	private Button btnSimula; // Value injected by FXMLLoader

	@FXML
	void doCreaGrafo(ActionEvent event) {
		this.txtResult.clear();
		if(this.txtAnno.getText().isEmpty()) {
			this.txtResult.appendText("Devi inserire un anno tra 1910-2014, estremi esclusi\n");
			return;
		}
		Integer anno = Integer.parseInt(this.txtAnno.getText());
		if(anno>=2014 || anno <= 1910) {
			this.txtResult.appendText("Devi inserire un anno tra 1910-2014, estremi esclusi\n");
			return;
		}
		
		String forma = this.cmbBoxForma.getValue();
		if(forma==null) {
			this.txtResult.appendText("Devi selezionare un anno tra 1910-2014, estremi esclusi,\n"
					+ "per scegliere la forma dell'avvistamento e creare il grafo\n");
			return;
		}
		
		model.creaGrafo(anno, forma);
		
		this.txtResult.appendText("GRAFOO CREATO\n"+model.getNumVertexGrafo()+" VERTICI\n"+
				+ model.getNumEdgeGrafo()+" ARCHI\n");
		
		
		this.txtResult.appendText("*** PESI TOTALI PER STATO ***\n");
		
		Map<State, Integer> pesoStati = model.getPesoArchiStati();

		for(State s : pesoStati.keySet()) {
			this.txtResult.appendText(s.getName()+" "+pesoStati.get(s)+"\n");
		}
	}


	@FXML
	void doSelezionaAnno(ActionEvent event) {
		this.txtResult.clear();
		if(this.txtAnno.getText().isEmpty()) {
			this.txtResult.appendText("Devi inserire un anno tra 1910-2014, estremi esclusi\n");
			return;
		}
		Integer anno = Integer.parseInt(this.txtAnno.getText());
		if(anno>=2014 || anno <= 1910 ) {
			this.txtResult.appendText("Devi inserire un anno tra 1910-2014, estremi esclusi\n");
			return;
		}
		Year year = Year.of(anno);
		List<String> shapes = model.getShapesByYear(anno);
		this.cmbBoxForma.getItems().clear();
		this.cmbBoxForma.getItems().addAll(shapes);
		this.txtResult.appendText("Forme inserite nella tendina, puoi creare il grafo!\n");
	}

	@FXML
	void doSimula(ActionEvent event) {
		this.txtResult.clear();
		if(this.txtAnno.getText().isEmpty()) {
			this.txtResult.appendText("Devi inserire un anno tra 1910-2014, estremi esclusi\n");
			return;
		}
		Integer anno = Integer.parseInt(this.txtAnno.getText());
		if(anno>=2014 || anno <= 1910) {
			this.txtResult.appendText("Devi inserire un anno tra 1910-2014, estremi esclusi\n");
			return;
		}
		
		String forma = this.cmbBoxForma.getValue();
		if(forma==null) {
			this.txtResult.appendText("Devi selezionare un anno tra 1910-2014, estremi esclusi,\n"
					+ "per scegliere la forma dell'avvistamento e creare il grafo\n");
			return;
		}
		
		Integer alfa = Integer.parseInt(this.txtAlfa.getText());
		if(alfa>=100 || alfa <= 0) {
			this.txtResult.appendText("Devi inserire un parametro di probabilità alfa\n"
					+ "compreso tra 0 e 100, estremi inclusi\n");
			return;
		}
		
		Integer T1 = Integer.parseInt(this.txtT1.getText());
		if(T1>=365 || T1 <= 0) {
			this.txtResult.appendText("Devi inserire un valore temporale in giorni\n"
					+ "compreso tra 0 e 365, estremi inclusi\n");
			return;
		}

		Map<State, Double> allertaStati = new HashMap<State, Double>();
		allertaStati = model.simula(anno, forma, T1, alfa);
		
		this.txtResult.appendText("*** ALLERTA PER STATO NELL'ANNO "+anno+
				"***\n");
		
		for(State s : allertaStati.keySet()) {
			this.txtResult.appendText(s.toString()+"\nPERICOLO:"+allertaStati.get(s)+"\n");
		}
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
		assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
		assert btnSelezionaAnno != null : "fx:id=\"btnSelezionaAnno\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
		assert cmbBoxForma != null : "fx:id=\"cmbBoxForma\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
		assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
		assert txtT1 != null : "fx:id=\"txtT1\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
		assert txtAlfa != null : "fx:id=\"txtAlfa\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";
		assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'NewUfoSightings.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;

	}
}

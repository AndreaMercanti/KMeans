package analysis;

import org.jfree.data.general.Dataset;

/**
 * La classe si occupa di modellare un oggeto responsabile di rappresentare un insieme di
 * dati che verranno utilizzati dal client, per la creazione di un grafico.<br>
 * I membri della classe infatti vanno a costituire gli elementi base per la rappresentazione
 * di un grafico tra cui l'insieme dei dati appartenenti a un grafico (dataset, che in base
 * alla classe di cui &egrave istanza rappresenta anche il tipo di grafico da rappresentare),
 * l'etichetta per l'asse x e l'etichetta per l'asse y. Essi quindi vengono, tramite gli stream
 * di I/O, inviati al client.
 *
 * @author Luca Suriano
 * @see org.jfree.data.general.Dataset
 * 
 */
public class ChartData {
	
	/**
	 * Il dataset appartenente al grafico 
	 */
	Dataset dataset;
	
	/**
	 * Etichetta per l'asse x 
	 */
	String labelX;
	
	/**
	 * Etichetta per l'asse y
	 */
	String labelY;
	
	/**
	 * Il costruttore si occupa di istanziare i membri della classe
	 *
	 * @param data Il dataset per il grafico
	 * @param labelx L'etichetta per l'asse x
	 * @param labely L'etichetta per l'asse y
	 */
	ChartData(Dataset data, String labelx, String labely){
		this.dataset = data;
		this.labelX = labelx;
		this.labelY = labely;
	}
	
	/**
	 * Il costruttore si occupa di istanziare esclusivamente il dataset per quei grafici che
	 * non utilizzano etichette.
	 *
	 * @param data Il dataset utile per l'inizializzazione
	 */
	ChartData(Dataset data){
		this.dataset = data;
	}
	
	/**
	 * @return Ritorna il dataset per il grafico.
	 */
	public Dataset getDataset(){
		return this.dataset;
	}
	
	/**
	 * @return Ritorna l'etichetta rappresentante l'asse x
	 */
	public String getLabelX(){
		return this.labelX;
	}
	
	/**
	 * @return Ritorna l'etichetta rappresentante l'asse y
	 */
	public String getLabelY(){
		return this.labelY;
	}
}

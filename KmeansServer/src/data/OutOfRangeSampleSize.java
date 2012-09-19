package data;

import java.io.Serializable;

/**
 * La classe OutOfRangeSampleSize modella una eccezione controllata 
 * sollevata nel momento in cui il numero k di cluster da scoprire passato al server &egrave
 * maggiore rispetto al numero di centroidi generabili dall'insieme di transazioni.
 *
 * @author Luca Suriano
 *
 */

public class OutOfRangeSampleSize extends Exception implements Serializable {
	
	/**
	 * Richiama il costruttore della classe madre per creare l'oggetto eccezione
	 * corrispondente con il messaggio specificato come parametro.
	 * 
	 * @param msg Messaggio da visualizzare nel caso in cui l'eccezione &egrave sollevata
	 */
	public OutOfRangeSampleSize(String msg){
		super(msg);
	}

}
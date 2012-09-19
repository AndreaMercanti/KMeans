package data;
import java.io.Serializable;
import java.util.Set;

/**
 * La Tuple che rappresenta una tupla come sequenza di coppie attributo-valore.
 * In particolare una tupla sar&agrave una sequenza di {@link Item} ({@link DiscreteItem} o 
 * {@link ContinuousItem}).
 * 
 * @author Luca Suriano
 *
 */

public class Tuple implements Serializable {
	/**
	 * Array di item che rappresenta la sequenza di coppie attributo-valore
	 * caratterizzanti la tupla.
	 */
	Item tuple[];
	
	/**
	 * Inizializza la tupla specificando una dimensione per l'array {@link #tuple}
	 * 
	 * @param size Dimensione da assegnare all'array (attributo) {@link #tuple}
	 */
	Tuple(int size)
	{
		tuple = new Item[size];
	}
	
	/**
	 * Ritorna la dimensione dell'array {@link #tuple}
	 * 
	 * @return Valore intero rappresentante la dimensione dell'array
	 */
	
	public int getLength()
	{
		return this.tuple.length;
	}
	
	/**
	 * Ritorna un {@link Item} appartenente alla tupla
	 * 
	 * @param i Parametro intero rappresentante l'indice dell'array {@link #tuple}
	 * 			rappresentante la tupla.
	 * 
	 * @return Ritorna l'{@link Item} all'indice specificato.
	 */
	
	public Item get(int i)
	{
		return this.tuple[i];
	}
	
	/**
	 * Crea la sequenza di {@link Item} rappresentante la tupla.<br>
	 * La creazione avviene aggiungendo l'{@link Item} specificato come parametro 
	 * all'array {@link #tuple} all'indice i. 
	 * @param c  {@link Item} da aggiungere alla tupla.
	 * 
	 * @param i	 Parametro di tipo intero rappresentante l'indice dell'array {@link #tuple}
	 * 			 in cui aggiungere l'Item specificato.
	 */
	
	void add(Item c, int i)
	{
		this.tuple[i] = c;
	}
	
	/**
	 * Determina la distanza tra la tupla riferita da obj e la tupla corrente (riferita da this).<br>
	 * La distanza è ottenuta come la somma delle distanze tra gli item in posizioni eguali nelle due tuple.
	 * 
	 * @param obj Parametro rappresentante la tupla per cui &egrave necessario
	 * 			  effettuare il calcolo rispetto alla tupla corrente
	 * 
	 * @return Un valore di tipo double rapprensentante la distanza tra le due tuple
	 */
	
	public double getDistance(Tuple obj)
	{
		int i;
		double dist = 0;
		for(i=0; i<obj.getLength(); i++)
			dist = dist + this.tuple[i].distance(obj.get(i)); 
		
		return dist;
	}
	
	/**
	 * Restituisce la media delle distanze tra la tupla corrente e quelle ottenibili dalle righe 
	 * della tabella riferita da data aventi indice in clusteredData.<br>
	 * Per l'ottenimento delle tuple nelle corrispondenti righe indicizzate da clusteredData,
	 * si veda il metodo {@link Data#getItemSet(int)}.
	 * 
	 * @param data				Oggetto istanza della classe Data di cui &grave necessario 
	 * 							considerare alcune tuple.
	 * 
	 * @param clusteredData 	Indici delle tuple da all'interno della 
	 * 
	 * @return Un valore di tipo double contenente il calcolo della media.
	 */
	
	public double avgDistance(Data data, Set<Integer> clusteredData)
	{
		double p = 0.0, sumD=0.0;
		
		for(Integer itg : clusteredData){
			double d = getDistance(data.getItemSet(itg));
			sumD += d;
		}
		
		p = sumD/clusteredData.size();
		
		return p;
	}
}
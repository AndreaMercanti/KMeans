package data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * La classe estende la classe Attribute e rappresenta un attributo discreto (categorico).<br>
 * L'attributo &egrave caratterizzato da un insieme di valori che lo rappresentano, oltre che da
 * un indice e da un nome, ereditati dalla classe Attribute.
 * 
 * @author Luca Suriano
 */

public class DiscreteAttribute extends Attribute implements Iterable<String> {
	
	/**
	 * TreeSet di oggetti String, uno per ciascun valore del dominio discreto.
	 * I valori del dominio sono univoci e sono ordinati seguendo un ordine lessicografico.
	 * 
	 * @see java.util.TreeSet
	 */
	private TreeSet<String> values;
	
	/**
	 * Il costruttore invoca anzitutto il costruttore della classe madre e inizializza 
	 * un attributo discreto specificando il nome dell'attributo, l'identificativo numerico 
	 * dell'attributo e il TreeSet di stringhe rappresentanti il dominio dell'attributo
	 * 
	 * @param name Nome dell'attributo
	 * @param index Identificativo dell'attributo
	 * @param values TreeSet rappresentante il dominio di valori per l'attributo
	 * 
	 * @see java.util.TreeSet
	 */
	
	public DiscreteAttribute (String name, int index, TreeSet<String> values)
	{
		super(name, index);
		
		this.values = values;
	}
	
	/**
	 * Ritoran il numero di elementi caratterizzanti il dominio dell'attributo presenti
	 * nel TreeSet {@link #values}
	 * 
	 * @return {@link java.util.TreeSet#size()}
	 */
	
	public int getNumberOfDistinctValues()
	{
		return this.values.size();
	}
	
	/**
	 * Ritorna l'iteratore del TreeSet {@link #values}
	 * 
	 * @return {@link java.util.TreeSet#iterator()}
	 */

	public Iterator<String> iterator()
	{
		return this.values.iterator();
	}
	
	/**
	 * Determina il numero di volte che il valore v compare in corrispondenza dell'attributo 
	 * corrente (indice di colonna) negli esempi memorizzati in data e indicizzate (per riga) 
	 * da idList
	 * 
	 * @param data 		Riferimento ad un oggetto istanza della classe Data
	 * 					che rappresenta la tabella in cui effettuare il calcolo
	 * @param idList	Paramentro di tipo Set indicante l'insieme di alcuni indici di riga 
	 * 					per cui effettuare il calcolo
	 * @param v			Valore di cui calcolare la frequenza in corrispondenza degli
	 * 					indici di riga idList e la corrispondente colonna dell'attributo
	 * 					corrente.
	 */
	
	int frequency(Data data, Set idList, String v)
	{
		int freq = 0, attrbIndex = this.getIndex();
		
		for(Object o : idList)
			if(v.equals(data.getAttributeValue((Integer)o, attrbIndex)))
				freq++;
		
		return freq;	
	}
}
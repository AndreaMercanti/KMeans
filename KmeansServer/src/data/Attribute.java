package data;

import java.io.Serializable;

/**
 * Classe astratta che modella un attributo.
 * 
 * @author Luca Suriano
 *
 */

public abstract class Attribute implements Serializable {
	
	/**
	 * Nome simbolico dell'attributo
	 * 
	 * @see java.lang.String
	 */
	protected String name;
	
	/**
	 * Identificativo numerico dell'attributo
	 */
	protected int index;
	
	/**
	 * Inizializza l'attributo assegnandoli un nome e un indice.
	 * 
	 * @param name Nome dell'attributo
	 * @param index Identificativo numerico dell'attributo
	 */
	
	Attribute(String name, int index)
	{
		this.index = index;
		this.name = new String(name);
	}
	
	/**
	 * Ritorna un oggetto istanza della classe String corrispondente
	 * al nome dell'attributo
	 *  
	 * @return {@link #name}
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Ritorna un intero corrispondente all'identificativo dell'attributo
	 * 
	 * @return {@link #index}
	 */
	public int getIndex()
	{
		return this.index;
	}
	
	/**
	 * Metodo toString. Il metodo ritorna semplicemente il nome dell'attributo.
	 * 
	 * @return {@link #name}
	 */
	
	public String toString()
	{
		return this.name;
	}
}

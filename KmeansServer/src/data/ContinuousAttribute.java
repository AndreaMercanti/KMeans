package data;

/**
 * La classe ContinuousAttribute estende la classe Attribute e modella un attributo
 * continuo (numerico). Tale classe include i metodi per la “normalizzazione” del 
 * dominio dell'attributo nell'intervallo [0,1] al fine da rendere confrontabili 
 * attributi aventi domini diversi.
 * 
 * @author Luca Suriano
 *
 */

public class ContinuousAttribute extends Attribute {
	
	/**
	 * Estremo massimo dell'intervallo di valori (dominio) che l'attributo può reamente assumere.
	 */
	private double max;
	
	/**
	 * Estremo minimo dell'intervallo di valori (dominio) che l'attributo può reamente assumere.
	 */
	private double min;
	
	/**
	 * Il costruttore invoca il costruttore della classe madre e inizializza i membri aggiunti 
	 * per estensione
	 * 
	 * @param name Nome dell'attributo 
	 * @param index Identificativo dell'atributo
	 * @param min Estremo minimo dell'intervallo
	 * @param max Estremo massimo dell'intervallo
	 * 
	 * @see Attribute#name
	 * @see Attribute#index
	 */
	
	public ContinuousAttribute (String name, int index, double min, double max)
	{
		super(name, index);
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Ritorna il valore massimo dell'intervallo
	 * 
	 * @see #min
	 */
	
	public double getMin(){
		return this.min;
	}
	
	/**
	 * Ritorna il valore massimo dell'intervallo
	 * 
	 * @see #max
	 */
	
	public double getMax(){
		return this.max;
	}
	
	/**
	 * Calcola e restituisce il valore normalizzato del parametro passato in input. 
	 * La normalizzazione ha come codominio lo intervallo [0,1]. 
	 * La normalizzazione di v è quindi calcolata come segue:
	 * v'=(v-min)/(max-min)
	 * 
	 * @param v Valore da normalizzare
	 * 
	 * @return double che rappresenta il valore normalizzato
	 */
	
	double getScaledValue(double v)
	{
		double v1;
		
		v1 = (v - this.min)/(this.max - this.min);
		
		return v1;
	}
}
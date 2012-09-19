package data;

/**
 * La classe ContinuousItem estende la classe astratta Item e rappresenta una coppia 
 * <Attributo continuo - valore numerico> Per esempio Temperature=10.5 (assumendo che
 * Temperature sia un attributo continuo.
 * 
 * @author Luca Suriano
 */

public class ContinuousItem extends Item {

	/**
	 * Invoca il costruttore della classe madre inizializzando l'attributo coninvolto
	 * nell'item di tipo {@link ContinuousAttribute} assegnandoli il valore specificato.
	 * 
	 * @param attribute ContinuousAttribute coinvolto nell'item corrente.
	 * @param value		Valore da assegnare all'item.
	 */
	ContinuousItem(Attribute attribute, Double value){
		super(attribute, value);
	}
	
	/**
	 * Determina la distanza (in valore assoluto) tra il valore
	 * scalato memorizzato nello item corrente {@link Item#getValue()} e quello scalato
	 * associalto al parametro a.<br>
	 * Per ottenere valori scalati si utilizza {@link ContinuousAttribute#getScaledValue(double)}
	 */
	
	double distance(Object a){
		 
		double dist;
		
		Attribute objAttribute = ((ContinuousItem)a).getAttribute();
		
		double currVal = ((ContinuousAttribute)this.getAttribute()).getScaledValue((Double)this.getValue());
		
		double tupVal = ((ContinuousAttribute)objAttribute).getScaledValue(   (Double)((ContinuousItem)a).getValue()   );
		
		return dist = Math.abs(currVal - tupVal);
	}
}

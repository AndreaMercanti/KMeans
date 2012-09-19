package data;

/**
 * La classe DiscreteItem estende la classe astratta Item e rappresenta una coppia 
 * <Attributo discreto- valore discreto> (per esempio Outlook="Sunny")
 * <br>
 * Il valore che far&agrave parte di un DiscreteItem sar&agrave uno dei valori
 * appartenenti al dominio dell'attributo discreto specificato.
 * 
 * @author Luca Suriano
 */

public class DiscreteItem extends Item {

	/**
	 * Invoca il costruttore della classe madre inizializzando l'attributo coninvolto
	 * nell'item di tipo {@link DiscreteAttribute} assegnandoli il valore specificato.
	 * 
	 * @param attribute DiscreteAttribute coinvolto nell'item corrente.
	 * @param value		Valore da assegnare all'item. (Far&agrave parte del dominio dei
	 * 					valori di un DiscreteAttribute)
	 */
	DiscreteItem(DiscreteAttribute attribute, String value)
	{
		super(attribute, value);
	}
	
	/**
	 * Implementa il metodo definito come astratto nella classe madre.<br>
	 * Esso effettua un controllo tra stringhe.<br>
	 * In particolare verr&agrave restituito 0 se (getValue().equals(a)), cio&egrave se
	 * il valore dell'attributo corrente (di tipo String) &egrave uguale al valore 
	 * specificato come parametro (verr&agrave richiamato il {@link Object#toString()}
	 * dell'oggetto specificato). Se le due stringhe sono diverse viene restituito 1.
	 */
	double distance(Object a) {
		double dist;
		boolean conf = false;
		
		conf = getValue().equals(a.toString());
		if(conf == true)
			dist = 0;
		else
			dist = 1;
		return dist;
	}

}

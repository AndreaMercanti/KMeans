package database;
/**
 * La classe estende Exception e modella un'eccezione che si verifica nel caso di
 * assenza di un valore all’interno di un resultset
 * 
 * @author Luca Suriano
 */
public class NoValueException extends Exception{
	/**
	 * Richiama il costruttore della classe madre per creare l'oggetto eccezione
	 * corrispondente con il messaggio specificato come parametro.
	 * 
	 * @param msg Messaggio da visualizzare nel caso in cui l'eccezione &egrave sollevata
	 */
	NoValueException(String msg){
		super(msg);
	}

}

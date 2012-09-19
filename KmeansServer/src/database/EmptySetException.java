package database;
/**
 * La classe EmptySetException estende Exception e modella
 * un'eccezione che si verifica nel caso in cui viene restituito un resultset vuoto.
 * 
 * @author Luca Suriano
 */
public class EmptySetException extends Exception{
		/**
		 * Richiama il costruttore della classe madre per creare l'oggetto eccezione
		 * corrispondente con il messaggio specificato come parametro.
		 * 
		 * @param msg Messaggio da visualizzare nel caso in cui l'eccezione &egrave sollevata
		 */
		EmptySetException(String msg){
			super(msg);
		}
}

package database;

/**
 * La classe estende Exception per modellare il fallimento nella connessione al database.
 * 
 * @author Luca Suriano
 *
 */
public class DatabaseConnectionException extends Exception{
	
	/**
	 * Richiama il costruttore della classe base specificando il messaggio
	 * indicato come parametro.
	 * 
	 * @param msg Messaggio da mostrare per l'eccezzione.
	 */
	DatabaseConnectionException(String msg){
		super(msg);
	}
}

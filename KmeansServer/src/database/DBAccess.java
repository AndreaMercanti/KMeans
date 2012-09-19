package database;

import java.sql.*;
import java.util.List;

/**
 * La classe DbAccess che realizza l'accesso alla base di dati.<br>
 * La classe si occupa di effettuare una connessione alla base di dati sfruttando i driver di
 * connessione mysql.
 * La connessione &egrave effettuata in locale attraverso una url del tipo:<br>
 * "jdbc:mysql://127.0.0.1:PORT/MapDBBig"<br>
 * Dove PORT sar&agrave la porta in cui &egrave disponibile il servizio mysql<br>
 * Per la connessione saranno necessaria anche uno userid e una password.<br>
 * Sia la porta, sia lo userid che la password saranno specificati all'interno del 
 * {@link server.DatabaseDialog}. I dati specificati all'interno della finestra modale,
 * verranno utilizzati all'interno del costruttore.
 * 
 * @author Luca Suriano
 *
 */

public class DBAccess {
	/**
	 * Nome del driver utilizzato per la connessione al database.<br>
	 * Sar&agrave inizializzato con: org.gjt.mm.mysql.Driver
	 */
	private static String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
	
	/**
	 * Tipo di DBMS.<br>
	 * Sar&agrave inizializzato con jdbc:mysql
	 */
	private static final String DBMS ="jdbc:mysql";
	
	/**
	 * Indirizzo necessario per la connessione al database<br>
	 * Sar&agrave inizializzato con 127.0.0.1
	 */
	private static final String SERVER = "127.0.0.1";
	
	/**
	 * Nome del database<br>
	 * Sar&agrave inizializzato con MapDBBig
	 */
	private static final String DATABASE = "MapDBBig";

	/**
	 * Porta sulla quale sar&agrave disponibile il servizio mysql
	 */
	private static String PORT;
	
	/**
	 * User id necessario per la connessione
	 */
	private static String USER_ID;
	
	/**
	 * Password necessaria per la connessione
	 */
	private static String PASSWORD;
	
	/**
	 * Oggetto istanza della classe Connection.
	 * 
	 * @see java.sql.Connection
	 */
	private static Connection conn;
	
	
	/**
	 * Costruttore della classe che si occupa di inizializzare i membri {@link #PORT}, 
	 * {@link #USER_ID} e {@link #PASSWORD}.<br>
	 * L'inizializzazione avviene grazie ai dati inseriti all'interno del {@link server.DatabaseDialog}
	 * Verr&agrave quindi passata una {@link java.util.List} di stringhe in cui la prima corrisponde
	 * alla porta, la seconda allo userid e la terza alla password.<br>
	 * Nel costruttore quindi viene letta la lista per inizializzare i membri.
	 * 
	 * @param selectedValues Lista contenente i valori selezioni nel {@link server.DatabaseDialog}
	 */
	
	public DBAccess(List<String> selectedValues){
		DBAccess.PORT = selectedValues.get(0);
		DBAccess.USER_ID = selectedValues.get(1);
		DBAccess.PASSWORD = selectedValues.get(2);
	}
	
	/**
	 * Impartisce al class loader l’ordine di caricare il driver mysql, inizializza la connessione 
	 * riferita da conn.<br>
	 * Il metodo solleva e propaga una eccezione di tipo DatabaseConnectionException in caso di 
	 * fallimento nella connessione al database.
	 * 
	 * @throws DatabaseConnectionException
	 * @throws ClassNotFoundException
	 */
	
	public static void initConnection() throws DatabaseConnectionException, ClassNotFoundException
	{
		String url = DBMS+"://" + SERVER + ":" + PORT + "/" + DATABASE;
		Class.forName(DRIVER_CLASS_NAME);
		
		try {
			conn = DriverManager.getConnection(url, USER_ID, PASSWORD);
		} catch (SQLException e) {
			throw new DatabaseConnectionException("Errore! - Impossibile Connettersi al Database!");
		}
	}
	
	/**
	 * Restituisce l'attributo di classe {@link #conn}
	 */
	
	public static Connection getConnection(){
		return conn;
	}
	
	/**
	 * Chiude la connessione {@link #conn}
	 */
	
	public static void closeConnection() throws SQLException{
		conn.close();
	}
}
package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import database.TableSchema.Column;

/**
 * La classe si occupa di ottenere in maniera appropriata la tuple dalla base di dati e renderle
 * disponibili al sistema.<br>
 * In particolare le tuple vengono estratte singolarmente, quindi ci saranno tuple univoche nel
 * sistema, nonostante la base di dati pu&ograve possedere duplicati.<br>
 * Lo schema originale della tabella (che quindi equivale a quello della base di dati)
 * &egrave ottenuto grazie alla classe {@link database.TableSchema}.<br>
 * Il risultato fornito da questa classe viene trattato dai metodi qui presenti. In particolare 
 * grazie ai metodi
 * {@link TableData#getDistinctTransazioni(String)}, 
 * {@link TableData#getDistinctColumnValues(String, TableSchema.Column)} e 
 * {@link TableData#getAggregateColumnValue(String, TableSchema.Column, QUERY_TYPE)} lo schema
 * verr&agrave utilizzato per potare a termine particolari operazioni (si veda la definizione
 * dei metodi per osservare come lo schema viene utilizzato).
 * 
 * @author Luca Suriano
 * 
 * @see TableSchema
 * @see TableSchema.Column
 * @see QUERY_TYPE
 */

public class TableData {
	
	/**
	 * Fornisce alla classe l'accesso alla base di dati.
	 * 
	 *  @see database.DBAccess
	 */
	
	DBAccess db;
	
	/**
	 * Il costruttore della classe si occupa di inizializzare il membro {@link #db}
	 * in modo da rendere disponibile l'accesso alla base di dati all'intera classe.
	 * 
	 * @param db Riferimento ad un oggetto istanza della classe {@link database.DBAccess}
	 */
	
	public TableData(DBAccess db) {
		this.db = db;
	}
	
	/**
	 * Ricava lo schema della tabella con nome table. Esegue una interrogazione per estrarre le 
	 * tuple distinte da tale tabella. Per ogni tupla del resultset, si crea un oggetto, istanza 
	 * della classe Example, il cui riferimento va incluso nella lista da restituire.<br>
	 * In particolare, per la tupla corrente nel resultset, si estraggono i valori dei singoli 
	 * campi (usando getFloat() o getString()), e li si aggiungono all'oggetto istanza della classe 
	 * Example che si sta costruendo.<br>
	 * 
	 * @param table La tabella a partire della quale si effettuer&agrave l'interrogazione
	 * 
	 * @return Lista completa contente tutte le tuple distinte.
	 * 
	 * @throws SQLException		   L'eccezione &egrave sollevata e propagata in presenza di errori 
	 * 							   nella esecuzione della query.
	 * @throws EmptySetException   Se il resultset, ottenuto dalla query, è vuoto.
	 * 
	 * @see java.util.List
	 * @see Example
	 */

	public List<Example> getDistinctTransazioni(String table) throws SQLException, EmptySetException{
		Example tr;
		List<Example> comp = new ArrayList<Example>();
		
		String query = "SELECT DISTINCT * FROM " + table + " ;";
		Statement s = DBAccess.getConnection().createStatement();
		
		ResultSet r = s.executeQuery(query);
		TableSchema tbs = new TableSchema(this.db, table);
		
		
		if(r.next()){
			   do{
				   tr  = new Example();
					
					for(int i = 0; i < tbs.getNumberOfAttributes(); i++){
						String currColl = tbs.getColumn(i).getColumnName();
						
						if( tbs.getColumn(i).isNumber())
							tr.add(r.getDouble(currColl));
						else
							tr.add(r.getString(currColl));
					}
					
					comp.add(tr);
			      
			   }while(r.next());
		} else
			   throw new EmptySetException("Errore! - Risultato della query vuoto! Nessuna tupla.");
		
		r.close();
		
		return comp;
	}
	
	/**
	 * Formula ed esegue una interrogazione SQL per estrarre i valori distinti ordinati di 
	 * column e popolare un insieme da restituire. L'insieme restituito sar&agrave un oggetto
	 * istanza della classe TreeSet.<br>
	 * Il metodo pu&ograve propagare un eccezione di tipo SQLException (in presenza di errori 
	 * nella esecuzione della query)
	 * 
	 * @param table 	La tabella a partire della quale si effettuer&agrave l'interrogazione 
	 * @param column	La colonna da considerare per la tabella specificata
	 * 
	 * @return				Valori distinti e oridinati appartenenti alla colonna specificata
	 * 
	 * @throws SQLException		   L'eccezione &egrave sollevata e propagata in presenza di errori 
	 * 							   nella esecuzione della query.
	 * 
	 * @see java.util.TreeSet
	 * @see TableSchema.Column
	 */

	
	public  Set<Object> getDistinctColumnValues(String table,Column column) throws SQLException{
		
		Set<Object> res = new TreeSet<Object>();
		
		Statement s = DBAccess.getConnection().createStatement();
		
		String query = "SELECT " + column.getColumnName() + " FROM " + table + " ORDER BY " + column.getColumnName() + " ASC ;";

		ResultSet r = s.executeQuery(query);
		
		int index = 1;
		
		while(r.next()) {
			res.add(r.getObject(column.getColumnName()));
			index++;
		}
		
		r.close();
		
		return res;

	}
	
	/**
	 * Formula ed esegue una interrogazione SQL per estrarre il valore aggregato (valore minimo 
	 * o valore massimo) cercato nella colonna di nome column della tabella di nome table.<br>
	 * Il metodo solleva e propaga una NoValueException se il resultset è vuoto o il valore 
	 * calcolato &egravè pari a null.
	 * 
	 * @param table		   La tabella a partire della quale si effettuer&agrave l'interrogazione
	 * @param column	   La colonna da considerare per la tabella specificata
	 * @param aggregate	   Valore enumerativo (MAX o MIN) per determinare se effettuare una query
	 * 					   per estrarre il valore minimo o il valore massimo
	 * 
	 * @return Il valore di massimo o minimo calcolato per la colonna specificata come parametro
	 * 
	 * @throws SQLException		   L'eccezione &egrave sollevata e propagata in presenza di errori 
	 * 							   nella esecuzione della query.
	 * @throws NoValueException	   Eccezione sollevata se il resultset &egrave vuoto o il valore
	 * 							   calcolato &egrave pari a null
	 */

	public  Object getAggregateColumnValue(String table, Column column, QUERY_TYPE aggregate) throws SQLException, NoValueException{
		
		Object res = new Object();
				
		Statement s = DBAccess.getConnection().createStatement();
		
		String query = "SELECT " + aggregate + "(" + column.getColumnName() + ") FROM " + table + " ;";

		ResultSet r = s.executeQuery(query);
		
		if(r.next() && res != null) {
			
					do{
						res = r.getObject(1);
					}while(r.next());
					
		} else if(res == null){
			
					String aggVal = "";
					
					aggVal = (aggregate == QUERY_TYPE.MAX) ?  "Massimo" : "Minimo";
					
					throw new NoValueException("Errore! - Impossibile calcolare: " + aggVal + " sulla colonna: " + column.getColumnName());
					
				}else
					
					throw new NoValueException("Errore! - Risultato della query vuoto, nessuna tupla!");
		
		r.close();
		
		return res;
	}
}
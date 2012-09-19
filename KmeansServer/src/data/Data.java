package data;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

import database.DBAccess;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.Example;
import database.NoValueException;
import database.QUERY_TYPE;
import database.TableData;
import database.TableSchema;


/**
 * La classe Data modella l'insieme di transazioni (o tuple) ottenute dalla base di dati.
 * La classe fa ampio uso delle classi presenti all'interno del package database, per leggere le
 * singole tuple appartenenti alla base di dati rendendole utilizzabili all'interno dell'intero 
 * sistema.<br>
 * La gestione delle tuple avviene tramite i metodi della classe {@link database.TableData} in
 * particolare: {@link database.TableData#getDistinctTransazioni(String)}, 
 * {@link database.TableData#getAggregateColumnValue(String, database.TableSchema.Column, QUERY_TYPE)},
 * {@link database.TableData#getDistinctColumnValues(String, database.TableSchema.Column)},
 * sfruttando il risultato della classe {@link database.TableSchema} (tale classe infatti
 * si occupa di ottenere una versione - utilizzabile all'interno del sistema - della tabella
 * nella base di dati).<br>
 * L'accesso alla base di dati avviene attraverso la classe {@link database.DBAccess}
 * <p>
 * Le tuple che caratterizzeranno la tabella data sono univoche, quindi duplicati presenti
 * all'interno della base di dati verranno considerati una sola volta (migliorando cos&igrave le 
 * operazioni di scoperta dei cluster e rendendole pi&ugrave efficienti).<br>
 * Quindi la tabella Data rappresenta tutti i dati che verranno utilizzati all'interno del sistema,
 * non a caso prima di effettuare qualsiasi operazione da parte del server, viene innanzitutto 
 * istanziato un oggetto di questa classe e i dati prodotti vengono resi disponibili per tutti 
 * le attivit&agrave del sistema (a meno che non sia un'attivit&agrave di
 * scoperta di cluster da file, in questo caso la tabbella data non &egrave istanziata e i cluster
 * sono ottenuto dal file).<br>
 * Per ottenere correttamente i dati &grave necessario che all'atto dell'istanziazione della classe
 * sia specificato il nome della tabella, che coincide esattamente con quello della tabella contenuta
 * nella base di dati.<br>
 * La classe rende disponibili attraverso i metodi getter:<br>
 * <li>Il numero dell tuple</li>
 * <li>Il numero degli attributi</li>
 * <li>Il valore che appartiene ad un attributo</li>
 * <li>Un determinato attributo</li>
 * 
 * @author Luca Suriano
 * @see database.TableData
 * @see database.TableSchema
 *
 */

public class Data implements Serializable{

	private List<Example> data;
	private int numberOfExamples;
	private List<Attribute> attributeSet;
	private int distinctTuples;

	public Data(String tableName, List<String> selectedValues) throws SQLException, EmptySetException, DatabaseConnectionException, ClassNotFoundException, NoValueException{
		DBAccess dba = new DBAccess(selectedValues);
		DBAccess.initConnection();
		TableData tb = new TableData(dba);

		TableSchema tbs = new TableSchema(dba, tableName);


		this.data = tb.getDistinctTransazioni(tableName);
		this.numberOfExamples = this.data.size();
		this.attributeSet = new LinkedList<Attribute>();

		for(int i = 0; i < tbs.getNumberOfAttributes(); i++){
			String ColName = tbs.getColumn(i).getColumnName();
			if(tbs.getColumn(i).isNumber()){
				double max = ((Float)tb.getAggregateColumnValue(tableName, tbs.getColumn(i), QUERY_TYPE.MAX)).doubleValue();
				double min = ((Float)tb.getAggregateColumnValue(tableName, tbs.getColumn(i), QUERY_TYPE.MIN)).doubleValue();
				attributeSet.add(new ContinuousAttribute(ColName, i, min, max));
			} else {

				Set<Object> resVal = tb.getDistinctColumnValues(tableName, tbs.getColumn(i));

				TreeSet<String> attributeValues = new TreeSet<String>();

				for(Object item : resVal)
					attributeValues.add((String)item);


				attributeSet.add( new DiscreteAttribute(ColName, i, attributeValues) );
			}
		}

		distinctTuples = this.data.size();

	}

	public int getNumberOfExamples(){
		return this.numberOfExamples;
	}

	public int getNumberOfExplanatorySet(){
		return this.attributeSet.size();
	}

	public Object getAttributeValue(int exampleIndex, int attributeIndex){
		return this.data.get(exampleIndex).get(attributeIndex);
	}

	public Attribute getAttributeSchema(int index){
		return this.attributeSet.get(index);
	}

	public Tuple getItemSet(int index){

		Tuple tuple = new Tuple(attributeSet.size());

		for(int i=0; i<attributeSet.size(); i++)
		{
			if(data.get(index).get(i) instanceof Double)
				tuple.add(new ContinuousItem((ContinuousAttribute)attributeSet.get(i), (Double)data.get(index).get(i)), i);
			else
				tuple.add(new DiscreteItem((DiscreteAttribute)attributeSet.get(i), (String)data.get(index).get(i)), i);
		}

		return tuple;
	}

	public int[] sampling(int k) throws OutOfRangeSampleSize{
		int centroidIndexes[]=new int[k];
		//choose k random different centroids in data.

		Random rand=new Random();
		rand.setSeed(System.currentTimeMillis());

		if(k <= 0 || k > this.distinctTuples)	
			throw new OutOfRangeSampleSize("Errore! - Numero di iterate inserito non valido!\nIntervallo valori accettati per k: 1 - " + this.distinctTuples);
		{
			for (int i=0; i<k; i++){
				boolean found=false;
				int c;
				do
				{
					found=false;
					c=rand.nextInt(getNumberOfExamples());
					// verify that centroid[c] is not equal to a centroide already stored in CentroidIndexes
					for(int j=0;j<i;j++)
						if(compare(centroidIndexes[j],c)){
							found=true;
							break;
						}
				}
				while(found);
				centroidIndexes[i]=c;
			}
		}

		return centroidIndexes;
	}

	private boolean compare(int i,int j){
		int ind = 0;
		boolean cond = true;
		Object first = new Object();
		Object sec = new Object();

		while(cond && ind < getNumberOfExplanatorySet()){
			first = getAttributeValue(i,ind);
			sec = getAttributeValue(j,ind);
			if(!first.equals(sec))
				cond = false;
			ind++;
		}

		return cond;

	}

	Object computePrototype(Set<Integer> idList, Attribute attribute){

		if(attribute instanceof DiscreteAttribute)
			return computePrototype(idList, (DiscreteAttribute)attribute);
		else
			return computePrototype(idList, (ContinuousAttribute)attribute);

	}

	String  computePrototype(Set<Integer> idList, DiscreteAttribute attribute)
	{
		int maxFreq = 0, attrFreq = 0; 
		String prot = new String();

		for(String val : attribute)
		{
			attrFreq = attribute.frequency(this, idList, val);

			if(maxFreq < attrFreq){
				maxFreq = attrFreq;
				prot = val;
			}
		}
		return prot;
	}

	Double computePrototype(Set<Integer> idList, ContinuousAttribute attribute){
		double sum = 0.0;
		double media = 0.0;

		for(Integer val : idList)
			sum = sum + (Double)(data.get(val).get(1));

		media = sum/idList.size();

		return media;
	}

	public String[] getAttributesString(){
		int attrLen = this.getNumberOfExplanatorySet();
		String[] attributes = new String[attrLen];

		for(int i = 0; i < attrLen; i++)
			attributes[i] = this.getAttributeSchema(i).getName();

		return attributes;
	}

	public String toString(){
		int i,j;

		String schema = new String();

		for(i = 1; i <= getNumberOfExamples(); i++){
			schema = schema + i + ": ";

			for(j = 0; j < 5; j++){
				schema = schema + data.get(i-1).get(j) + ", ";
			}

			schema = schema + "\n";
		}

		return schema;
	}
	
	public static void main(String[] args) throws SQLException, EmptySetException, DatabaseConnectionException, ClassNotFoundException, NoValueException{
		List<String> val = new ArrayList<String>();
		val.add("8889");
		val.add("root");
		val.add("root");
		Data res = new Data("playtennis", val);
		System.out.println(res);
	}
}
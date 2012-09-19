import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.Rotation;

/**
 * StreamChart &egrave la classe che si occupa di creare un determinato tipo di grafico basandosi
 * su un insieme di dati passati dal server attraverso lo stream di I/O.
 * Una volta creato il grafico sar&agrave necessario richiamare la funzione createChartPanel()
 * che restituisce il pannello (JPanel) contenente il grafico da assegnare ad un contenitore.
 * <br>
 * Il grafico creato pu&ograve pi&ugrave o meno contenere una legenda e se necessario (sopratutto nel
 * grafico ad assi cartesiani) verr&agrave creato un pannello a parte per la legenda contenente 
 * anche delle funzioni per migliorare la visibilit&agrave del grafico.
 * 
 * @author Luca Suriano
 *
 */
class StreamChart {
	/**
	 * Pannello che conterr&agrave se necessario la legenda del grafico.
	 * @see javax.swing.JPanel
	 */
	private JPanel legendPanel;
	
	/**
	 * Numero totale di elementi per il grafico. Il campo &egrave utilizzato all'interno
	 * di un grafico a torta, per indicare la totalit&agrave di elementi in base ai quali
	 * &egrave calcolata ciascuna percentuale assegnata ad ogni parte del grafico.
	 */
	static private int totalElem;
	
	/**
	 * Il costruttore della classe si occupa di inizializzare il pannello per la legenda
	 * , in modo da renderlo utilizzabile successivamente e settare il layout per lo stesso.
	 */

	StreamChart(){
		this.legendPanel = new JPanel();
		this.legendPanel.setLayout(new BoxLayout(this.legendPanel, BoxLayout.PAGE_AXIS));
	}
	
	/**
	 * Inizializza l'attributo statico {@link #totalElem}
	 * 
	 * @param tot paramentro intero che inizializer&agrave #totalElem
	 */
	
	static void setTotalElem(int tot){
		StreamChart.totalElem = tot;
	}
	
	/**
	 * Il metodo si occupa di creare un grafico a torta detto anche di tipo PIE. 
	 * Il grafico si basa sui dati passati
	 * come parametro. Esso potr&agrave possedere o meno la legenda in base al numero di elementi
	 * contenuti nel dataset.
	 * La legenda sar&agrave quella di default 
	 * {@link org.jfree.chart.ChartFactory#createPieChart3D(java.lang.String, org.jfree.data.general.PieDataset, boolean, boolean, boolean)}
	 * <p>
	 * Il dataset  di tipo {@link org.jfree.data.general.PieDataset} sar&agrave creato dal server
	 * e conterr&agrave le diverse sezioni del grafico. Verr&agrave visualizzato un messaggio
	 * nel caso in cui il dataset &egrave vuoto.
	 * &Egrave disponibile la funzionalit&agrave di rotazione con lo scroll del mouse soprattutto
	 * nel caso in cui i dati presenti nel dataset sono molti.
	 * 
	 * @param dataset Insieme dei dati su cui si baser&agrave il grafico.
	 * @return Il grafico da mostrare.
	 * @see org.jfree.chart.JFreeChart
	 */

	JFreeChart createChart(PieDataset dataset) {
		boolean flaglegend = true;

		if(dataset.getItemCount() > 50)
			flaglegend = false;

		JFreeChart chart = ChartFactory.createPieChart3D(
				"Grafico KMeans - " + StreamChart.totalElem + " Esempi",  
				dataset,           
				flaglegend,              
				true,
				false
		);


		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {2}"));
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.7f);
		plot.setNoDataMessage("Nessun dato da visualizzare!");

		return chart;
	}
	
	/**
	 * Il metodo si occupa di creare un grafico con assi cartesiani detto anche di tipo SCATTER.
	 * I dati da inserire sono contenuti all'interno del parametro dataset di tipo {@link org.jfree.data.xy.XYDataset}
	 * Il grafico avr&agrave come valore massimo per l'asse X il valore maxX e 
	 * come valore massimo per l'asse Y il valore maxY.<br>
	 * Ciascun asse &egrave rappresentato dalle variabili xLabel e yLabel, che contribuiscono
	 * a descrivere meglio il significato dei corrispondenti assi.
	 * Nel caso in cui il dataset risulter&agrave essere vuoto verr&agrave mostrato un messaggio
	 * a video.
	 * In questo tipo di grafico se il numero di valori presenti all'interno del dataset
	 * supera il quantitativo di 25, allora verr&agrave creato un pannello apposito per la
	 * legenda. Tale legenda verr&agrave creata con l'ausilio della classe LegendPanel.
	 * La legenda quindi se creata possieder&agrave delle funzioni per migliorare la visibilit&agrave
	 * del grafico.
	 * <p>
	 * &Egrave disponibile uno zoom per il grafico utilizzabile attraverso lo scroll del mouse
	 * per osservare meglio i dati (si veda la guida utente per un utilizzo della funzione)
	 * 
	 * @param dataset L'insieme di dati che verranno inseriti all'interno del grafico
	 * @param xLabel Rappresenta il nome da assegnare all'asse X
	 * @param yLabel Rappresenta il nome da assegnare all'asse Y
	 * @param maxX Massimo valore per l'asse X
	 * @param maxY Massimo valore per l'asse Y
	 * 
	 * @return Il grafico da mostrare.
	 * @see org.jfree.chart.JFreeChart
	 * @see LegendPanel
	 * 
	 */

	JFreeChart createChart(XYDataset dataset, String xLabel, String yLabel, Double maxX, Double maxY) {

		JFreeChart chart = ChartFactory.createScatterPlot(
				"Grafico KMeans", 
				xLabel, 
				yLabel, 
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false
		);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setDomainZeroBaselineVisible(true);
		plot.setRangeZeroBaselineVisible(true);
		plot.setNoDataMessage("Nessun dato da visualizzare!");

		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();

		renderer.setSeriesOutlinePaint(0, Color.black);
		renderer.setUseOutlinePaint(true);

		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);

		if(renderer.getLegendItems().getItemCount() > 25){
			legend.setVisible(false);
			this.setLegend(renderer);
		}
		else
			legend.setVisible(true);


		NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setAutoRangeIncludesZero(false);
		domainAxis.setTickMarkInsideLength(2.0f);
		domainAxis.setTickMarkOutsideLength(0.0f);
		domainAxis.setRange(-2.0, maxX + 2);
		if(!xLabel.equals("temperature"))
			domainAxis.setTickUnit(new NumberTickUnit(1.0));

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setTickMarkInsideLength(2.0f);
		rangeAxis.setTickMarkOutsideLength(0.0f);
		rangeAxis.setRange(-2.0, maxY + 2);
		if(!yLabel.equals("temperature"))
			rangeAxis.setTickUnit(new NumberTickUnit(1.0));



		return chart;
	}
	
	/**
	 * Il metodo si occuper&agrave di creare un pannello apposito per la legenda (chiamato
	 * solo quando necessario da {@link #createChart(XYDataset, String, String, Double, Double)}).
	 * Il pannello per la legenda sar&agrave contenuto all'interno dell'attributo #legendPanel.
	 * Il metodo prende in input il parametro render che contiene la legenda di default
	 * creata per un grafico di tipo SCATTER {@link org.jfree.chart.ChartFactory#createScatterPlot(java.lang.String, java.lang.String, java.lang.String, org.jfree.data.xy.XYDataset, org.jfree.chart.plot.PlotOrientation, boolean, boolean, boolean)}.
	 * A partire da questa verr&agrave creata la nuova legenda utilizzando le varie etichette e 
	 * colori nella legenda di default.
	 * Il pannello conterr&agrave anche una JComboBox utile per visualizzare solo determinati
	 * dati sul grafico piuttosto che tutti i dati contemporaneamente.
	 * Il pannello creato infine verr&agrave posto subito sotto il grafico.
	 * Tale metodo quindi assicura quindi, di visualizzare il grafico anche a fronte di una grossa
	 * mole di dati, evitando di essere coperto interamente dalla legenda.
	 * 
	 * @param renderer {@link org.jfree.chart.renderer.xy.XYLineAndShapeRenderer}
	 */

	private void setLegend(final XYLineAndShapeRenderer renderer){

		Iterator it = renderer.getLegendItems().iterator();
		int i = 0, j = 0;
		final JComboBox comboSer = new JComboBox();
		JLabel title = new JLabel("Legenda");
		JPanel serPanel = new JPanel();

		title.setSize(new Dimension(200, 30));
		title.setFont(new Font(null, Font.BOLD, 20));
		serPanel.setLayout(new FlowLayout());
		serPanel.add(new JLabel("Visualizza solo Cluster: "));
		serPanel.add(comboSer);
		comboSer.addActionListener(new ActionListener(){
			int lastSer = 1;
			public void actionPerformed(ActionEvent e) {
				renderer.setBaseSeriesVisible(false);
				Object item = comboSer.getSelectedItem();
				if(item.equals("Tutti")){
					renderer.setBaseSeriesVisible(true);
				}else{
					Integer index = (Integer)item;
					renderer.setSeriesVisible(this.lastSer, false);
					renderer.setSeriesVisible(index, true);
					this.lastSer = index;
				}
			}

		});
		this.legendPanel.add(serPanel);
		this.legendPanel.add(title);

		comboSer.addItem("Tutti");

		while(it.hasNext()){
			LegendItem item = (LegendItem)it.next();
			LegendPanel lp = new LegendPanel(item.getLabel(), renderer.getSeriesPaint(i));
			comboSer.addItem(j);
			this.legendPanel.add(lp);

			j++;
			i++;
		}

	}
	
	/**
	 * Il metodo si occupa di creare un pannello contenente il grafico da visualizzare.
	 * Il grafico sar&agrave quello specificato come paramentro.<br>
	 * Il metodo crea il pannello a partire da {@link org.jfree.chart.ChartPanel} e inserisce
	 * tale pannello in un JPanel in modo da renderlo utilizzabile all'esterno.
	 * 
	 * @param chart Il grafico da inserire all'interno del pannello
	 * @return Il pannello da utilizzare contenente il grafico
	 * @see javax.swing.JPanel
	 * @see org.jfree.chart.ChartPanel
	 */

	JPanel createChartPanel(JFreeChart chart) {

		JPanel mainContainer = new JPanel();
		JPanel chartContainer = new JPanel();
		JScrollPane outputScroll = new JScrollPane(chartContainer);

		chartContainer.setLayout(new BorderLayout());

		ChartPanel chartPanel = new ChartPanel(chart);				
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setMouseZoomable(true);

		chartContainer.add(chartPanel, BorderLayout.CENTER);
		chartContainer.add(this.legendPanel, BorderLayout.SOUTH);
		outputScroll.setPreferredSize(new Dimension(700, 650));
		mainContainer.add(outputScroll);

		return mainContainer;
	}

}
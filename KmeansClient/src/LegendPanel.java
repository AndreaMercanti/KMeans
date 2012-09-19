import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * LegendPanel &egrave la classe che modella ciascun elemento della legenda per un determinato grafico.
 * Ogni elemento &egrave un JPanel ed &egrave cos&igrave formato:<br><br>
 * - In evidenza un singolo LegendPanel nel riquadro rosso
 * <center><img src="images/legendpanel.jpg"/></center>
 * Quindi ogni elemento &egrave costituito da una figura (quadrato) e da un'etichetta entrambi allineati 
 * secondo un FlowLayout.
 *  
 * @author Luca Suriano
 * 
 * @see javax.swing.JPanel
 * @see java.awt.FlowLayout
 */

class LegendPanel extends JPanel{
	/**
	 * Rappresenta il nome che ciascun elemento avr&agrave nella legenda 
	 */
	private String label;
	
	/**
	 * Rappresenta il colore assegnato ad ogni forma disegnata nel pannello.
	 */
	private Paint paint;
	
	/**
	 * Il costruttore si occupa di assegnare al pannello un layout di tipo FlowLayout ed 
	 * inizializzare gli attributi della classe
	 * 
	 * @param lbl   L'etichetta che ciascun elemento della legenda avr&agrave
	 * @param p     Il colore che verr&agrave assegnato a ciascuna forma.
	 */

	LegendPanel(String lbl, Paint p){
		this.setLayout(new FlowLayout());
		this.label = lbl;
		this.paint = p;
		add(new JLabel(label));
	}
	
	/**
	 * Sovrascrive il metodo della classe JComponent. Richiamato non appena il pannello
	 * per questo elemento della legenda risulter&agrave essere visibile. 
	 * Si occupa di creare un quadrato da assegnare a ciascun elemento della legenda, 
	 * fornendo per ciascuno di essi un colore ({@link #paint})
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(this.paint);
		g2.fillRect(0, 0, 20, 20);
	}
}
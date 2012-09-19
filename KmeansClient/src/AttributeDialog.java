import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

/**
 * AttributeDialog &egrave; la classe che modella una finestra modale con lo scopo di
 * acquisire gli attributi da utilizzare e il tipo di grafico in cui tali attributi
 * verranno utilizzati.
 * La finestra modale sar&agrave; composta da 3 JComboBox. Di queste, 2 servono per la lettura
 * degli attributi e la terza serve per la scelta del grafico.  
 * 
 * @author Luca Suriano
 *
 */

class AttributeDialog extends JDialog {
	/**
	 * JComboBox per la selezione dell'attributo relativo all'asse X
	 */
	private JComboBox xCombo;
	/**
	 * JComboBox per la selezione dell'attributo relativo all'asse Y
	 */
	private JComboBox yCombo;
	/**
	 * JComboBox per la selezione del tipo di grafico
	 */
	private JComboBox chartCombo;
	/**
	 * Conterr&agrave; i valori delle selezioni effettuate nella finestra.
	 */
	private ArrayList values = new ArrayList();
	
	/**
	 * Inizializza la finestra di dialogo modale che verr&agrave; aggiunta al Frame specificato
	 * come parametro. 
	 * L'argomento attributeList conterr&agrave; gli attributi passati dal server utili per specificare
	 * quali di questi verranno utilizzati nel grafico.
	 * L'array verr&agrave; letto per utilizzare i suoi elementi all'interno delle 2 JComboBox per gli assi
	 * cartesiani.
	 * <p>
	 * Il click sul bottone della finestra comporter&agrave il salvataggio della selezione effettuta
	 * (riguardante le JComboBox) e la chiusura della finestra stessa.
	 * 
	 * @param frame 		Frame di appartenenza per l'AttributeDialog
	 * @param attributeList Array contenente gli attributi che verranno inseriti nelle JComboBox
	 * 						all'atto dell'inizializzazione
	 */
	AttributeDialog(Frame frame, String[] attributeList) {
		super(frame, "Scegli gli attributi e grafico", true);
		JPanel main = new JPanel();
		JButton btnSend = new JButton("OK");

		super.setLocationRelativeTo(frame);
		super.setLocation(100, 100);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); 
		this.setSize(new Dimension(300, 250));
		this.setResizable(false);

		main.setLayout(new FlowLayout());

		xCombo = new JComboBox(attributeList);
		yCombo = new JComboBox(attributeList);
		chartCombo = new JComboBox();
		chartCombo.addItem("scatter");
		chartCombo.addItem("pie");
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.add(new JLabel("Scegli Attributo Asse x: "));
		topPanel.add(xCombo);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new FlowLayout());
		centerPanel.add(new JLabel("Scegli Attributo Asse y: "));
		centerPanel.add(yCombo);
		
		JPanel downPanel = new JPanel();
		downPanel.setLayout(new FlowLayout());
		downPanel.add(new JLabel("Scegli tipo di Grafico: "));
		downPanel.add(chartCombo);
		
		JPanel adv = new JPanel();
		adv.setLayout(new BorderLayout());
		adv.add(new JLabel("La scelta degli attributi è ininfluente"), BorderLayout.NORTH);
		adv.add(new JLabel("nel caso del grafico a torta (pie)"), BorderLayout.CENTER);
		adv.add(new JSeparator(), BorderLayout.SOUTH);
		
		
		main.add(topPanel);
		main.add(centerPanel);
		main.add(adv);
		main.add(downPanel);
		btnSend.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					values.add(xCombo.getSelectedIndex());
					values.add(yCombo.getSelectedIndex());	
					values.add(chartCombo.getSelectedItem());
					chiudi();
			}
		});
		main.add(btnSend);
		add(main);
	}
	
	/**
	 * Metodo privato utilizzato dalla classe per chiudere la finestra una volta che &egrave stato
	 * cliccato il bottone presente all'interno della stessa.
	 * Il metodo si occupa semplicemente di nascondere la finestra, le risorse associate alla
	 * finestra non vengono distrutte.
	 * 
	 * @see JDialog#setVisible(boolean)
	 */
	
	private void chiudi(){
		this.setVisible(false);
	}
	
	/**
	 * Il metodo si occupa di ritornare l'insieme dei valori selezionati dall'utente nella
	 * finestra
	 * 
	 * @return {@link #values} 
	 */
	
	ArrayList getselectedValues(){
		return this.values;
	}
}
package es.ucm.fdi.isbc.g17.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import es.ucm.fdi.isbc.g17.ViviendasRecommender;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda.TipoVivienda;

public final class MainFrame extends JFrame {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String[] cards = {"Card1", "Card2"};
	private ViviendasRecommender recommender;
	
	private JPanel bgpanel = new JPanel(new CardLayout());
	private JPanel panel1 = new JPanel();
	private JPanel panel2 = new JPanel();
	private JPanel panelOptions = new JPanel();
	private JPanel panelResults = new JPanel();
	
	private JComboBox comboVivienda = new JComboBox(TipoVivienda.values());
	private JTextField textLocalidad = new JTextField();
	private JSpinner spinHabitaciones = new JSpinner();
	private JSpinner spinPrecio = new JSpinner();
	private JButton buscarBtn = new JButton("Buscar");

	/**
     * Creates an empty frame and fills it with the necessary components to work.
     */
    public MainFrame (ViviendasRecommender cbr) {    	    	
    	try {
    		recommender = cbr;
    		cbr.configure();
			cbr.preCycle();		
	    	
	        setTitle("Grupo 17 - ISBC");
	        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	
	        /* Schedule the GUI creation for later for greater responsivity */
	        SwingUtilities.invokeLater(new Runnable() {
	            @Override
	            public void run () {
	                setupGui();
	            }
	        });
        
    	} catch (ExecutionException e) {
			e.printStackTrace();
		}
    }

    /* package */void setupGui () {
    	setupContent();
    	setupListeners();
        pack();
    }
    
    private void setupContent () {
    	setContentPane(bgpanel); 
    	
    	//panel1
    	setupPanel1();
        
        //panel2 
      	setupPanel2();
      	
      	//add panels to card layout
        bgpanel.add(panel1, cards[0]);
        bgpanel.add(panel2, cards[1]);
        
    }
    
    private void setupListeners(){
    	 buscarBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed (final ActionEvent arg0) {
             	try {
 	            	DescripcionVivienda initialDescription = new DescripcionVivienda(0);
 	            	initialDescription.setTipo(TipoVivienda.valueOf(comboVivienda.getSelectedItem().toString()));
 	            	initialDescription.setLocalizacion(textLocalidad.getText());
 	            	initialDescription.setHabitaciones(((Number)spinHabitaciones.getValue()).intValue());
 	            	initialDescription.setPrecio(((Number)spinPrecio.getValue()).intValue());
 	            	CBRQuery query =  new CBRQuery();
 					query.setDescription(initialDescription);
 					recommender.cycle(query);
 					showCases(recommender.getSelectedCases());
 					//cambiar al siguiente panel
 					CardLayout cl = (CardLayout)(bgpanel.getLayout());
 				    cl.show(bgpanel, cards[1]);
 				} catch (ExecutionException e) {
 					e.printStackTrace();
 				}
             }
         });
    }
    
    private void setupPanel1(){
	  FormLayout layout1 = new FormLayout(
    	      "right:pref, 6dlu, 50dlu, 4dlu, default",  // columns
    	      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");           // rows
  	  panel1.setLayout(layout1);               
      CellConstraints cc = new CellConstraints();
      panel1.add(new JLabel("Vivienda"),  cc.xy  (1, 1));
      panel1.add(comboVivienda,           cc.xywh(3, 1, 3, 1));
      panel1.add(new JLabel("Localidad"), cc.xy  (1, 3));
      panel1.add(textLocalidad,           cc.xy  (3, 3));
      panel1.add(new JLabel("Habitaciones"), cc.xy  (1, 5));
      spinHabitaciones.setModel(new SpinnerNumberModel(1, 1, 10, 1));
      panel1.add(spinHabitaciones,        cc.xy  (3, 5));
      panel1.add(new JLabel("Precio"),    cc.xy  (1, 7));
      spinPrecio.setModel(new SpinnerNumberModel(350000, 50000, 5000000, 100000));
      panel1.add(spinPrecio,              cc.xy  (3, 7));
      panel1.add(buscarBtn,               cc.xy  (5, 7));  
    }
    
    private void setupPanel2(){
    	panel2.setLayout(new BorderLayout());
      	
      	FormLayout layout3 = new FormLayout(
        	      "right:pref, 6dlu, 50dlu, 4dlu, default",  // columns
        	      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");           // rows
      	panelOptions.setLayout(layout3);      	
      	panelResults.setLayout(new BoxLayout(panelResults,BoxLayout.PAGE_AXIS));
      	
      	panel2.add(panelOptions, BorderLayout.WEST);
      	panel2.add(panelResults, BorderLayout.EAST);
    }
    
    private void showCases(Collection<CBRCase> cases){
    	Iterator it = cases.iterator();
    	while(it.hasNext()){
    		//get case
    		CBRCase casoCBR =  (CBRCase) it.next();
    		DescripcionVivienda desc = (DescripcionVivienda) casoCBR.getDescription();
    		
    		//create panel to display case
    		panel2.add(new ViviendaPanel(desc));
    	}
    }

}

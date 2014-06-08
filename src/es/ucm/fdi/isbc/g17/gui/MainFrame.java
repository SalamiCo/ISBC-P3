package es.ucm.fdi.isbc.g17.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

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
	private ViviendasRecommender recommender;
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
        pack();
    }
    
    private void setupContent () {
    	FormLayout layout = new FormLayout(
      	      "right:pref, 6dlu, 50dlu, 4dlu, default",  // columns
      	      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");           // rows
    	final JPanel panel = new JPanel(layout);
        setContentPane(panel);        
        CellConstraints cc = new CellConstraints();
        panel.add(new JLabel("Vivienda"),  cc.xy  (1, 1));
        panel.add(comboVivienda,           cc.xywh(3, 1, 3, 1));
        panel.add(new JLabel("Localidad"), cc.xy  (1, 3));
        panel.add(textLocalidad,           cc.xy  (3, 3));
        panel.add(new JLabel("Habitaciones"), cc.xy  (1, 5));
        spinHabitaciones.setModel(new SpinnerNumberModel(1, 1, 10, 1));
        panel.add(spinHabitaciones,        cc.xy  (3, 5));
        panel.add(new JLabel("Precio"),    cc.xy  (1, 7));
        spinPrecio.setModel(new SpinnerNumberModel(350000, 50000, 5000000, 100000));
        panel.add(spinPrecio,              cc.xy  (3, 7));
        panel.add(buscarBtn,               cc.xy  (5, 7));
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
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
            }
        });
    }

}

package es.ucm.fdi.isbc.g17.gui;

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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda.TipoVivienda;

public final class MainFrame extends JFrame {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates an empty frame and fills it with the necessary components to work.
     */
    public MainFrame () {
        setTitle("Grupo 17 - ISBC");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        /* Schedule the GUI creation for later for greater responsivity */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run () {
                setupGui();
            }
        });
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
        panel.add(new JLabel("Vivienda"),     cc.xy  (1, 1));
        panel.add(new JComboBox(TipoVivienda.values()),       cc.xywh(3, 1, 3, 1));
        panel.add(new JLabel("Localidad"),    cc.xy  (1, 3));
        panel.add(new JTextField(),           cc.xy  (3, 3));
        panel.add(new JLabel("Habitaciones"), cc.xy  (1, 5));
        SpinnerModel model = new SpinnerNumberModel(1, 1, 10, 1); //init,min.max,step
        panel.add(new JSpinner(model),        cc.xy  (3, 5));
        panel.add(new JLabel("Precio"),       cc.xy  (1, 7));
        SpinnerModel model1 = new SpinnerNumberModel(350000, 50000, 5000000, 100000); //init,min.max,step
        panel.add(new JSpinner(model1),           cc.xy  (3, 7));
        panel.add(new JButton("Buscar"),      cc.xy  (5, 7));
    }

}

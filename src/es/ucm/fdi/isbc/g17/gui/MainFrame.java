package es.ucm.fdi.isbc.g17.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

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
        final JPanel panel = new JPanel();
        setContentPane(panel);
        panel.setLayout(new BorderLayout());
        panel.invalidate();
    }

}

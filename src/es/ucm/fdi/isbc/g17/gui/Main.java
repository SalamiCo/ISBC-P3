package es.ucm.fdi.isbc.g17.gui;

import java.awt.Frame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import es.ucm.fdi.isbc.g17.ViviendasRecommender;


public final class Main {

    /**
     * Setup the Look and Feel so the app does not look awful.
     */
    /* package */static void setupLookAndFeel () {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (final Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Application entry point.
     * 
     * @param args Unused
     */
    public static void main (final String[] args) {
        setupLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run () {
            	ViviendasRecommender cbr = new ViviendasRecommender();
                final MainFrame frame = new MainFrame(cbr);
                frame.setLocationRelativeTo(null);
                frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
                frame.setVisible(true);
            }
        });
    }

    /** Private constructor to avoid instantiation */
    private Main () {
        throw new AssertionError();
    }

}

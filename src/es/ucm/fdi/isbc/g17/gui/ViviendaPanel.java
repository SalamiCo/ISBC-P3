package es.ucm.fdi.isbc.g17.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;

public final class ViviendaPanel extends JPanel {

    private DescripcionVivienda description;

    private JLabel title;
    private JLabel image;

    /**
     * Constructs a panel that shown no case.
     */
    public ViviendaPanel () {
        this(null);
    }

    /**
     * Constructs a panel that shows an initial case.
     * 
     * @param description The case this panel should display
     */
    public ViviendaPanel (DescripcionVivienda description) {
        setupInterface();
        setDescription(description);
    }

    /** @param description The case this panel should display */
    public void setDescription (DescripcionVivienda description) {
        this.description = description;
        updateInterface();
    }

    /** @return The case this panel is showing */
    public DescripcionVivienda getDescription () {
        return description;
    }

    // ============================
    // === SETUP AND UPDATE GUI ===

    private void setupInterface () {
        setLayout(new BorderLayout());

        /* Label for the title */
        title = new JLabel();
        add(title, BorderLayout.PAGE_START);

        /* Image for the house */
        image = new JLabel();
        add(title, BorderLayout.CENTER);
    }

    private void updateInterface () {
        title.setText(description == null ? "" : description.getTitulo());
        image.setIcon(description == null ? null : new ImageIcon(description.getUrlFoto()));
    }

}

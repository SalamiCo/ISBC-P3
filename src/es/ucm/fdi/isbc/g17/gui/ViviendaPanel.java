package es.ucm.fdi.isbc.g17.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;

public final class ViviendaPanel extends JPanel {

    private DescripcionVivienda description;

    private JLabel title, price, rooms;

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

        title = new JLabel();
        price = new JLabel();
        rooms = new JLabel();

        FormLayout layout = new FormLayout( //
            "right:pref, 6dlu, left:pref", // columns
            "pref, 3dlu, pref, 3dlu, pref"); // rows
        CellConstraints cc = new CellConstraints();
        setLayout(layout);

        add(title, cc.xyw(1, 1, 3));

        add(new JLabel("Precio"), cc.xy(1, 3));
        add(new JTextField(), cc.xy(3, 3));

        add(new JLabel("Habitaciones"), cc.xy(1, 5));
        add(new JTextField(), cc.xy(3, 5));
    }

    private void updateInterface () {
        title.setText(description == null ? "" : description.getTitulo());
        price.setText(description == null ? "" : String.valueOf(description.getPrecio()));
        rooms.setText(description == null ? "" : String.valueOf(description.getHabitaciones()));
    }
}

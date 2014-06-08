package es.ucm.fdi.isbc.g17.gui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;

public final class ViviendaPanel extends JPanel {

    private DescripcionVivienda description;

    private JLabel title, price, rooms, location, bathrooms, surface, state;

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
        location = new JLabel();
        bathrooms = new JLabel();
        surface = new JLabel();
        state = new JLabel();

        FormLayout layout = new FormLayout( //
            "right:pref, 6dlu, left:pref", // columns
            "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref"); // rows
        CellConstraints cc = new CellConstraints();
        setLayout(layout);

        add(title, cc.xyw(1, 1, 3));

        add(new JLabel("Precio"), cc.xy(1, 3));
        add(price, cc.xy(3, 3));

        add(new JLabel("Localización"), cc.xy(1, 5));
        add(location, cc.xy(3, 5));

        add(new JLabel("Habitaciones"), cc.xy(1, 7));
        add(rooms, cc.xy(3, 7));

        add(new JLabel("Baños"), cc.xy(1, 9));
        add(bathrooms, cc.xy(3, 9));

        add(new JLabel("Superficie"), cc.xy(1, 11));
        add(surface, cc.xy(3, 11));

        add(new JLabel("Estado"), cc.xy(1, 13));
        add(state, cc.xy(3, 13));
    }

    private void updateInterface () {
        title.setText(description == null ? "" : String.valueOf(description.getTitulo()));
        price.setText(description == null ? "" : String.valueOf(description.getPrecio()) + " €");
        rooms.setText(description == null ? "" : String.valueOf(description.getHabitaciones()));
        location.setText(description == null ? "" : String.valueOf(description.getLocalizacion()));
        bathrooms.setText(description == null ? "" : String.valueOf(description.getBanios()));
        surface.setText(description == null ? "" : String.valueOf(description.getSuperficie())+ " m\u00B2");
        state.setText(description == null ? "" : String.valueOf(description.getEstado()));
    }
}

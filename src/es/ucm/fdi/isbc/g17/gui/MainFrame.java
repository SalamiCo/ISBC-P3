package es.ucm.fdi.isbc.g17.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import es.ucm.fdi.isbc.g17.ViviendasRecommender;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda.TipoVivienda;

public final class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = { "#", "Nombre", "Habitaciones", "Tamaño", "Precio" };
    private ViviendasRecommender recommender;

    private JPanel panelSearch;
    private JPanel panelFilter;
    private JPanel panelResults;

    private JTable tableResults;
    private DefaultTableModel tableModel;

    private JComboBox comboVivienda = new JComboBox(TipoVivienda.values());
    private JTextField textLocalidad = new JTextField();
    private JSpinner spinHabitaciones = new JSpinner();
    private JSpinner spinPrecio = new JSpinner();
    private JButton buscarBtn = new JButton("Buscar");

    /**
     * Creates an empty frame and fills it with the necessary components to
     * work.
     */
    public MainFrame (ViviendasRecommender cbr) {
        recommender = cbr;

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
        setupListeners();
        pack();
        setLocationRelativeTo(null);
    }

    private void setupContent () {
        panelSearch = setupSearchPanel();
        panelFilter = setupFilterPanel();
        panelResults = setupResultsPanel();

        // +---+---+
        // | A | B |
        // +---+---+
        // | C |
        // +-------+
        Box box_A_B = Box.createHorizontalBox();
        box_A_B.add(panelSearch);
        box_A_B.add(panelFilter);

        Box box_AB_C = Box.createVerticalBox();
        box_AB_C.add(box_A_B);
        box_AB_C.add(panelResults);

        box_AB_C.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setContentPane(box_AB_C);

        // bgpanel.add(setupPanel2(), cards[1]);
    }

    private void setupListeners () {
        buscarBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (final ActionEvent arg0) {
                executeCbr();
            }
        });
    }

    private JPanel setupSearchPanel () {
        JPanel panel = new JPanel();

        FormLayout layout = new FormLayout("right:pref, 6dlu, pref, 4dlu, pref", // columns
                "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref"); // rows
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();
        panel.add(new JLabel("Vivienda"), cc.xy(1, 1));
        panel.add(comboVivienda, cc.xywh(3, 1, 3, 1));
        panel.add(new JLabel("Localidad"), cc.xy(1, 3));
        panel.add(textLocalidad, cc.xyw(3, 3, 3));
        panel.add(new JLabel("Habitaciones"), cc.xyw(1, 5, 1));
        spinHabitaciones.setModel(new SpinnerNumberModel(1, 1, 10, 1));
        panel.add(spinHabitaciones, cc.xyw(3, 5, 3));
        panel.add(new JLabel("Precio"), cc.xy(1, 7));
        spinPrecio.setModel(new SpinnerNumberModel(350000, 50000, 5000000, 100000));
        panel.add(spinPrecio, cc.xy(3, 7));
        panel.add(buscarBtn, cc.xy(5, 7));

        return panel;
    }

    private JPanel setupFilterPanel () {
        JPanel panel = new JPanel();

        FormLayout layout = new FormLayout("right:pref, 6dlu, pref, 6dlu, pref", // columns
                "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref"); // rows
        panel.setLayout(layout);

        // TODO fill

        return panel;
    }

    private JPanel setupResultsPanel () {
        JPanel panel = new JPanel();

        tableResults = new JTable();

        panel.add(new JScrollPane(tableResults));
        return panel;
    }

    private void displayCases (Collection<CBRCase> cases) {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            private static final long serialVersionUID = -5522053746224748015L;

            @Override
            public boolean isCellEditable (final int row, final int column) {
                return false;
            }
        };
        tableResults.setModel(tableModel);

        for (CBRCase casoCBR : cases) {
            DescripcionVivienda desc = (DescripcionVivienda) casoCBR.getDescription();
            tableModel.addRow(tableRow(desc));
        }
    }

    private Object[] tableRow (DescripcionVivienda desc) {
        return new Object[] { //
        /*    */desc.getId(), //
                desc.getTitulo(), //
                desc.getHabitaciones(), //
                desc.getSuperficie() + " m²", //
                desc.getPrecio() + " €" //
        };

    }

    private CBRQuery obtainQuery () {
        DescripcionVivienda initialDescription = new DescripcionVivienda(0);
        initialDescription.setTipo(TipoVivienda.valueOf(comboVivienda.getSelectedItem().toString()));
        initialDescription.setLocalizacion(textLocalidad.getText());
        initialDescription.setHabitaciones(((Number) spinHabitaciones.getValue()).intValue());
        initialDescription.setPrecio(((Number) spinPrecio.getValue()).intValue());

        CBRQuery query = new CBRQuery();
        query.setDescription(initialDescription);

        return query;
    }

    private void executeCbr () {
        try {
            recommender.setNumberOfResults(15);
            
            recommender.configure();
            recommender.preCycle();

            recommender.cycle(obtainQuery());

            displayCases(recommender.getSelectedCases());
            recommender.postCycle();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}

package es.ucm.fdi.isbc.g17.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.FilterBasedRetrieval.FilterConfig;
import jcolibri.method.retrieve.FilterBasedRetrieval.predicates.Equal;
import jcolibri.method.retrieve.FilterBasedRetrieval.predicates.Threshold;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import es.ucm.fdi.isbc.g17.ViviendasRecommender;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda.TipoVivienda;

public final class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = { "Título", "Tipo", "Habitaciones", "Tamaño", "Precio" };
    private ViviendasRecommender recommender;

    private JPanel panelSearch;
    private JPanel panelFilter;
    private JPanel panelResults;

    private JTable tableResults;
    private DefaultTableModel tableModel;

    private JComboBox comboVivienda = new JComboBox(TipoVivienda.values());
    private JTextField textLocalidad = new JTextField();
    private JSpinner spinHabitaciones;
    private JSpinner spinPrecio;
    private JSpinner spinResults;
    private JButton buscarBtn = new JButton("Buscar");
    

    private JSpinner spinMargenPrecio;
    private JSpinner spinMargenHabitaciones;

    private JCheckBox cboxFVivienda;
    private JCheckBox cboxFHabitaciones;
    private JCheckBox cboxFPrecio;

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
        // | ..C.. |
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

        spinHabitaciones = new JSpinner();
        spinHabitaciones.setModel(new SpinnerNumberModel(1, 1, 10, 1));

        spinPrecio = new JSpinner();
        spinPrecio.setModel(new SpinnerNumberModel(350000, 50000, 5000000, 100000));

        spinResults = new JSpinner();
        spinResults.setModel(new SpinnerNumberModel(15, 1, 100, 5));

        FormLayout layout = new FormLayout("right:pref, 4dlu, pref, 4dlu, pref", // columns
                "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref"); // rows
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();

        panel.add(new JLabel("Vivienda"), cc.xy(1, 1));
        panel.add(comboVivienda, cc.xyw(3, 1, 3));

        panel.add(new JLabel("Localidad"), cc.xy(1, 3));
        panel.add(textLocalidad, cc.xyw(3, 3, 3));

        panel.add(new JLabel("Habitaciones"), cc.xyw(1, 5, 1));
        panel.add(spinHabitaciones, cc.xyw(3, 5, 3));

        panel.add(new JLabel("Precio"), cc.xy(1, 7));
        panel.add(spinPrecio, cc.xyw(3, 7, 3));

        panel.add(new JLabel("Nº Resultados"), cc.xy(1, 9));
        panel.add(spinResults, cc.xy(3, 9));
        panel.add(buscarBtn, cc.xy(5, 9));

        return panel;
    }

    private JPanel setupFilterPanel () {
        JPanel panel = new JPanel();

        cboxFVivienda = new JCheckBox();
        cboxFHabitaciones = new JCheckBox();
        cboxFPrecio = new JCheckBox();

        spinMargenPrecio = new JSpinner();
        spinMargenPrecio.setModel(new SpinnerNumberModel(50000, 1000, 1000000, 10000));
        
        spinMargenHabitaciones = new JSpinner();
        spinMargenHabitaciones.setModel(new SpinnerNumberModel(1, 1, 10, 1));

        FormLayout layout = new FormLayout("pref, 4dlu, left:pref, 4dlu, right:pref, 4dlu, pref", // columns
                "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref"); // rows
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();

        panel.add(cboxFVivienda, cc.xy(1, 1));
        panel.add(new JLabel("Filtrar por Tipo"), cc.xyw(3, 1, 3));

        panel.add(cboxFHabitaciones, cc.xy(1, 3));
        panel.add(new JLabel("Filtrar por Habitaciones"), cc.xy(3, 3));
        panel.add(new JLabel("Margen:"), cc.xy(5, 3));
        panel.add(spinMargenHabitaciones, cc.xy(7, 3));

        panel.add(cboxFPrecio, cc.xy(1, 5));
        panel.add(new JLabel("Filtrar por Precio"), cc.xy(3, 5));
        panel.add(new JLabel("Margen:"), cc.xy(5, 5));
        panel.add(spinMargenPrecio, cc.xy(7, 5));
        
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
        /*    */desc.getTitulo(), //
                desc.getTipo(), //
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

    private FilterConfig obtainFilter () {
        FilterConfig filter = new FilterConfig();

        // Tipo de vivienda
        if (cboxFVivienda.isSelected()) {
            filter.addPredicate(new Attribute("tipo", DescripcionVivienda.class), new Equal());
        }

        // Habitaciones
        if (cboxFHabitaciones.isSelected()) {
            Number threshold = ((Number) spinMargenHabitaciones.getValue());
            filter.addPredicate(new Attribute("habitaciones", DescripcionVivienda.class), new Threshold(threshold));
        }

        // Precio
        if (cboxFPrecio.isSelected()) {
            Number threshold = ((Number) spinMargenPrecio.getValue());
            filter.addPredicate(new Attribute("precio", DescripcionVivienda.class), new Threshold(threshold));
        }

        return filter;
    }

    private void executeCbr () {
        try {
            recommender.setFilterConfig(obtainFilter());
            recommender.setNumberOfResults(obtainNumberOfResults());

            recommender.configure();
            recommender.preCycle();

            recommender.cycle(obtainQuery());

            displayCases(recommender.getSelectedCases());
            recommender.postCycle();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private int obtainNumberOfResults () {
        return ((Number) spinResults.getValue()).intValue();
    }

}

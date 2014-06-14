package es.ucm.fdi.isbc.g17.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
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

    private static final String[] COLUMN_NAMES = { "Título", "Tipo", "Zona", "Habitaciones", "Tamaño", "Precio" };
    private ViviendasRecommender recommender;
    private List<CBRCase> results = Collections.emptyList();

    private JPanel panelSearch;
    private JPanel panelFilter;
    private JPanel panelResults;

    private JTable tableResults;
    private DefaultTableModel tableModel;

    private JComboBox comboVivienda = new JComboBox(TipoVivienda.values());
    private JComboBox comboZona;
    private JSpinner spinHabitaciones;
    private JSpinner spinPrecio;
    private JSpinner spinResults;
    private JButton buscarBtn = new JButton("Buscar");

    private JSpinner spinMargenPrecio;
    private JSpinner spinMargenHabitaciones;

    private JCheckBox cboxFVivienda;
    private JCheckBox cboxFHabitaciones;
    private JCheckBox cboxFPrecio;

    private JSpinner spinBanios;
    private JCheckBox cboxFBanios;
    private JSpinner spinMargenBanios;

    private JSpinner spinSuperf;

    private JSpinner spinMargenSuperf;

    private JCheckBox cboxFSuperf;

    private JLabel labelMargenHabitaciones;

    private JLabel labelMargenBanios;

    private JLabel labelMargenPrecio;

    private JLabel labelMargenSuperf;

    public MainFrame (ViviendasRecommender cbr) throws ExecutionException {
        recommender = cbr;
        recommender.configure();
        CBRCaseBase cases = recommender.preCycle();

        setTitle("Grupo 17 - ISBC");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setupGui(cases);
    }

    /* package */void setupGui (CBRCaseBase cases) {
        setupContent(cases);
        setupListeners();
        updateGui();
        
        pack();
        setLocationRelativeTo(null);
    }

    private void updateGui () {
        spinBanios.setEnabled(cboxFBanios.isSelected());
        spinMargenBanios.setEnabled(cboxFBanios.isSelected());
        labelMargenBanios.setEnabled(cboxFBanios.isSelected());

        spinSuperf.setEnabled(cboxFSuperf.isSelected());
        spinMargenSuperf.setEnabled(cboxFSuperf.isSelected());
        labelMargenSuperf.setEnabled(cboxFSuperf.isSelected());

        spinMargenHabitaciones.setEnabled(cboxFHabitaciones.isSelected());
        labelMargenHabitaciones.setEnabled(cboxFHabitaciones.isSelected());
        
        spinMargenPrecio.setEnabled(cboxFPrecio.isSelected());
        labelMargenPrecio.setEnabled(cboxFPrecio.isSelected());
    }

    private void setupContent (CBRCaseBase cases) {
        panelSearch = setupSearchPanel(cases);
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
                performSearch();
            }
        });
        
        ActionListener checkBoxListener = new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                updateGui();
            }
        };

        cboxFBanios.addActionListener(checkBoxListener);
        cboxFHabitaciones.addActionListener(checkBoxListener);
        cboxFPrecio.addActionListener(checkBoxListener);
        cboxFSuperf.addActionListener(checkBoxListener);
        cboxFVivienda.addActionListener(checkBoxListener);
    }

    private JPanel setupSearchPanel (CBRCaseBase cases) {
        JPanel panel = new JPanel();

        spinHabitaciones = new JSpinner();
        spinHabitaciones.setModel(new SpinnerNumberModel(1, 1, 10, 1));

        spinPrecio = new JSpinner();
        spinPrecio.setModel(new SpinnerNumberModel(350000, 50000, 5000000, 100000));

        spinResults = new JSpinner();
        spinResults.setModel(new SpinnerNumberModel(15, 1, 100, 5));

        comboZona = new JComboBox(zonas(cases));

        FormLayout layout = new FormLayout("right:pref, 4dlu, pref, 4dlu, pref", // columns
                "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref"); // rows
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();

        panel.add(new JLabel("Vivienda"), cc.xy(1, 1));
        panel.add(comboVivienda, cc.xyw(3, 1, 3));

        panel.add(new JLabel("Zona"), cc.xy(1, 3));
        panel.add(comboZona, cc.xyw(3, 3, 3));

        panel.add(new JLabel("Habitaciones"), cc.xyw(1, 5, 1));
        panel.add(spinHabitaciones, cc.xyw(3, 5, 3));

        panel.add(new JLabel("Precio"), cc.xy(1, 7));
        panel.add(spinPrecio, cc.xyw(3, 7, 3));

        panel.add(new JLabel("Nº Resultados"), cc.xy(1, 9));
        panel.add(spinResults, cc.xy(3, 9));
        panel.add(buscarBtn, cc.xy(5, 9));

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Búsqueda"));
        return panel;
    }

    private String[] zonas (CBRCaseBase cases) {
        Set<String> all = new HashSet<String>();

        for (CBRCase cbrc : cases.getCases()) {
            DescripcionVivienda dv = (DescripcionVivienda) cbrc.getDescription();
            all.add(dv.getLocalizacion());
        }

        List<String> sorted = new ArrayList<String>(all);
        Collections.sort(sorted);
        return sorted.toArray(new String[all.size()]);
    }

    private JPanel setupFilterPanel () {
        JPanel panel = new JPanel();

        cboxFVivienda = new JCheckBox();
        cboxFHabitaciones = new JCheckBox();
        cboxFBanios = new JCheckBox();
        cboxFPrecio = new JCheckBox();
        cboxFSuperf = new JCheckBox();

        spinMargenPrecio = new JSpinner();
        spinMargenPrecio.setModel(new SpinnerNumberModel(50000, 1000, 1000000, 10000));

        spinMargenHabitaciones = new JSpinner();
        spinMargenHabitaciones.setModel(new SpinnerNumberModel(1, 1, 10, 1));

        spinBanios = new JSpinner();
        spinBanios.setModel(new SpinnerNumberModel(1, 1, 10, 1));
        
        spinMargenBanios = new JSpinner();
        spinMargenBanios.setModel(new SpinnerNumberModel(1, 1, 10, 1));

        spinSuperf = new JSpinner();
        spinSuperf.setModel(new SpinnerNumberModel(80, 15, 800, 5));
        
        spinMargenSuperf = new JSpinner();
        spinMargenSuperf.setModel(new SpinnerNumberModel(30, 5, 800, 5));

        FormLayout layout = new FormLayout("pref, 4dlu, left:pref, 4dlu, left:pref, 4dlu, right:pref, 4dlu, pref", // columns
                "pref, 4dlu, pref, 4dlu, pref, 4dlu, pref, 4dlu, pref"); // rows
        panel.setLayout(layout);

        CellConstraints cc = new CellConstraints();

        panel.add(cboxFVivienda, cc.xy(1, 1));
        panel.add(new JLabel("Filtrar por Tipo"), cc.xyw(3, 1, 5));

        labelMargenHabitaciones = new JLabel("Margen:");
        panel.add(cboxFHabitaciones, cc.xy(1, 3));
        panel.add(new JLabel("Filtrar por Habitaciones"), cc.xyw(3, 3, 3));
        panel.add(labelMargenHabitaciones, cc.xy(7, 3));
        panel.add(spinMargenHabitaciones, cc.xy(9, 3));

        labelMargenBanios = new JLabel("Margen:");
        panel.add(cboxFBanios, cc.xy(1, 5));
        panel.add(new JLabel("Filtrar por Baños"), cc.xy(3, 5));
        panel.add(spinBanios, cc.xy(5,  5));
        panel.add(labelMargenBanios, cc.xy(7, 5));
        panel.add(spinMargenBanios, cc.xy(9, 5));

        labelMargenPrecio = new JLabel("Margen:");
        panel.add(cboxFPrecio, cc.xy(1, 7));
        panel.add(new JLabel("Filtrar por Precio"), cc.xyw(3, 7, 3));
        panel.add(labelMargenPrecio, cc.xy(7, 7));
        panel.add(spinMargenPrecio, cc.xy(9, 7));

        labelMargenSuperf = new JLabel("Margen:");
        panel.add(cboxFSuperf, cc.xy(1, 9));
        panel.add(new JLabel("Filtrar por Superficie"), cc.xy(3, 9));
        panel.add(spinSuperf, cc.xy(5,  9));
        panel.add(labelMargenSuperf, cc.xy(7, 9));
        panel.add(spinMargenSuperf, cc.xy(9, 9));

        // TODO More filters

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filtros"));
        return panel;
    }

    private JPanel setupResultsPanel () {
        Box box = Box.createVerticalBox();

        tableResults = new JTable();
        tableResults.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked (final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final JTable target = (JTable) e.getSource();
                    final int row = target.getSelectedRow();
                    showDetails(results.get(row));
                }
            }
        });

        box.add(new JLabel("Doble Click en una fila para ver detalles de un piso"));
        box.add(new JScrollPane(tableResults));

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Resultados"));
        panel.add(box);
        return panel;
    }

    private void displayCases (Collection<CBRCase> cases) {
        results = Collections.unmodifiableList(new ArrayList<CBRCase>(cases));

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            private static final long serialVersionUID = -5522053746224748015L;

            @Override
            public boolean isCellEditable (final int row, final int column) {
                return false;
            }
        };
        tableResults.setModel(tableModel);

        for (CBRCase casoCBR : results) {
            DescripcionVivienda desc = (DescripcionVivienda) casoCBR.getDescription();
            tableModel.addRow(tableRow(desc));
        }

    }

    /* package */void showDetails (CBRCase cbrCase) {
        DescripcionVivienda desc = (DescripcionVivienda) cbrCase.getDescription();

        final JDialog dialog = createDetailsDialog(desc);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);

        setEverythingEnabled(false);
        dialog.pack();
        dialog.setVisible(true);

        performSearch();
    }

    private JDialog createDetailsDialog (final DescripcionVivienda desc) {
        final JDialog dialog = new JDialog();
        dialog.getContentPane().setLayout(new BorderLayout());

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.LINE_AXIS));

        /* Details */
        JPanel details = new JPanel();
        details.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        details.setLayout(new FormLayout(
                "right:pref, 6dlu, left:pref",
                "pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 30dlu"));

        CellConstraints cc = new CellConstraints();

        details.add(new JLabel("Título:"), cc.xy(1, 1));
        details.add(new JLabel(desc.getTitulo()), cc.xy(3, 1));

        JTextArea descriptionArea = new JTextArea(desc.getDescripcion());
        descriptionArea.setEditable(false);
        descriptionArea.setColumns(50);
        descriptionArea.setLineWrap(true);
        details.add(new JLabel("Descripción:"), cc.xy(1, 3));
        details.add(descriptionArea, cc.xy(3, 3));

        details.add(new JLabel("Tipo:"), cc.xy(1, 5));
        details.add(new JLabel(desc.getTipo().toString()), cc.xy(3, 5));

        details.add(new JLabel("Zona:"), cc.xy(1, 7));
        details.add(new JLabel(desc.getLocalizacion()), cc.xy(3, 7));

        details.add(new JLabel("Superficie:"), cc.xy(1, 9));
        details.add(new JLabel(desc.getSuperficie() + " m²"), cc.xy(3, 9));

        details.add(new JLabel("Habitaciones:"), cc.xy(1, 11));
        details.add(new JLabel(desc.getHabitaciones().toString()), cc.xy(3, 11));

        details.add(new JLabel("Baños:"), cc.xy(1, 13));
        details.add(new JLabel(desc.getBanios().toString()), cc.xy(3, 13));

        details.add(new JLabel("Precio:"), cc.xy(1, 15));
        details.add(new JLabel(desc.getPrecio() + " €"), cc.xy(3, 15));

        JTextArea descriptionExBasicos = new JTextArea(desc.getExtrasBasicos().toString());
        descriptionExBasicos.setEditable(false);
        descriptionExBasicos.setColumns(50);
        descriptionExBasicos.setLineWrap(true);
        details.add(new JLabel("Extras (básicos):"), cc.xy(1, 17));
        details.add(descriptionExBasicos, cc.xy(3, 17));

        JTextArea descriptionExFinca = new JTextArea(desc.getExtrasFinca().toString());
        descriptionExFinca.setEditable(false);
        descriptionExFinca.setColumns(50);
        descriptionExFinca.setLineWrap(true);
        details.add(new JLabel("Extras (finca):"), cc.xy(1, 19));
        details.add(descriptionExFinca, cc.xy(3, 19));

        JTextArea descriptionExOtros = new JTextArea(desc.getExtrasOtros().toString());
        descriptionExOtros.setEditable(false);
        descriptionExOtros.setColumns(50);
        descriptionExOtros.setLineWrap(true);
        details.add(new JLabel("Extras (otros):"), cc.xy(1, 21));
        details.add(descriptionExOtros, cc.xy(3, 21));

        dialog.getContentPane().add(details, BorderLayout.CENTER);

        /* Blacklist button */
        JButton buttonBlacklist = new JButton("Ignorar");
        buttonBlacklist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(dialog,
                        "Este piso no se volverá a mostrar en ninguna búsqueda\n¿Estás seguro?", "Confirmación",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    recommender.blacklist(desc.getId());
                    dialog.dispose();
                }
            }
        });
        panelButtons.add(buttonBlacklist);

        /* "Like"button */
        JButton buttonLike = new JButton("Más como este");
        buttonLike.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                comboVivienda.setSelectedItem(desc.getTipo());
                comboZona.setSelectedItem(desc.getLocalizacion());
                spinHabitaciones.setValue(desc.getHabitaciones());
                spinPrecio.setValue(desc.getPrecio());
                dialog.dispose();
            }
        });
        panelButtons.add(buttonLike);

        /* Close button */
        JButton buttonClose = new JButton("Cerrar");
        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                dialog.dispose();
            }
        });
        panelButtons.add(buttonClose);

        dialog.add(panelButtons, BorderLayout.PAGE_END);

        dialog.pack();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        return dialog;
    }

    private Object[] tableRow (DescripcionVivienda desc) {
        return new Object[] { //
        /*    */desc.getTitulo(), //
                desc.getTipo(), //
                desc.getLocalizacion(), //
                desc.getHabitaciones(), //
                desc.getSuperficie() + " m²", //
                desc.getPrecio() + " €" //
        };

    }

    private CBRQuery obtainQuery () {
        DescripcionVivienda initialDescription = new DescripcionVivienda(0);
        initialDescription.setTipo(TipoVivienda.valueOf(comboVivienda.getSelectedItem().toString()));
        initialDescription.setLocalizacion(comboZona.getSelectedItem().toString());
        initialDescription.setHabitaciones(((Number) spinHabitaciones.getValue()).intValue());
        initialDescription.setPrecio(((Number) spinPrecio.getValue()).intValue());
        initialDescription.setBanios(((Number) spinBanios.getValue()).intValue());
        initialDescription.setSuperficie(((Number) spinSuperf.getValue()).intValue());

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

        // Baños
        if (cboxFBanios.isSelected()) {
            Number threshold = ((Number) spinMargenBanios.getValue());
            filter.addPredicate(new Attribute("banios", DescripcionVivienda.class), new Threshold(threshold));
        }

        // Precio
        if (cboxFPrecio.isSelected()) {
            Number threshold = ((Number) spinMargenPrecio.getValue());
            filter.addPredicate(new Attribute("precio", DescripcionVivienda.class), new Threshold(threshold));
        }
        
        // Superficie
        if (cboxFSuperf.isSelected()) {
            Number threshold = ((Number) spinMargenSuperf.getValue());
            filter.addPredicate(new Attribute("superficie", DescripcionVivienda.class), new Threshold(threshold));
        }

        // TODO Apply filters

        return filter;
    }

    private void performSearch () {
        searchStarted();

        SwingWorker<?, ?> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground () throws Exception {
                recommender.setFilterConfig(obtainFilter());
                recommender.setNumberOfResults(obtainNumberOfResults());

                recommender.cycle(obtainQuery());

                displayCases(recommender.getSelectedCases());

                return null;
            }

            @Override
            protected void done () {
                searchEnded();
            }
        };

        worker.execute();
    }

    private void searchStarted () {
        updateGui();
        setEverythingEnabled(false);
    }

    /* package */void searchEnded () {
        setEverythingEnabled(true);
        updateGui();
    }

    private void setEverythingEnabled (boolean enabled) {
        for (Component comp : getAllChildren()) {
            comp.setEnabled(enabled);
        }
    }

    private Collection<Component> getAllChildren () {
        Collection<Component> components = new ArrayList<Component>();

        Deque<Component> pending = new ArrayDeque<Component>();
        pending.add(getContentPane());

        while (!pending.isEmpty()) {
            Component comp = pending.removeFirst();
            components.add(comp);

            if (comp instanceof Container) {
                for (Component subComp : ((Container) comp).getComponents()) {
                    pending.addLast(subComp);
                }
            }
        }

        return components;
    }

    private int obtainNumberOfResults () {
        return ((Number) spinResults.getValue()).intValue();
    }

}

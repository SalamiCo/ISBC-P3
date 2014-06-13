package es.ucm.fdi.isbc.g17;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.Connector;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.FilterBasedRetrieval.FilterBasedRetrievalMethod;
import jcolibri.method.retrieve.FilterBasedRetrieval.FilterConfig;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import es.ucm.fdi.isbc.viviendas.ViviendasConnector;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;

public final class ViviendasRecommender implements StandardCBRApplication {

    private Connector connector;
    private CBRCaseBase caseBase;

    private Collection<CBRCase> selectedCases;
    private int numberOfResults = 5;
    private FilterConfig filterConfig;

    private Collection<Integer> blacklist = new HashSet<Integer>();

    public Collection<CBRCase> getSelectedCases () {
        return selectedCases;
    }

    public void setNumberOfResults (int n) {
        numberOfResults = n;
    }

    public void setFilterConfig (FilterConfig fc) {
        this.filterConfig = fc;
    }

    public void blacklist (Integer id) {
        blacklist.add(id);
    }

    @Override
    public void configure () throws ExecutionException {
        long timeAtStart = System.nanoTime();
        connector = new ViviendasConnector();
        caseBase = new LinealCaseBase();
        long timeAtCreate =  System.nanoTime();
        
        double timeForCreate = (timeAtCreate - timeAtStart) / (double) TimeUnit.SECONDS.toNanos(1);
        System.out.printf("%.3fs configure%n", timeForCreate);
    }

    @Override
    public CBRCaseBase preCycle () throws ExecutionException {
        long timeAtStart = System.nanoTime();
        caseBase.init(connector);
        long timeAtInit =  System.nanoTime();
        
        double timeForInit = (timeAtInit - timeAtStart) / (double) TimeUnit.SECONDS.toNanos(1);
        System.out.printf("%.3fs pre-cycle%n", timeForInit);
        
        return caseBase;
    }

    @Override
    public void cycle (CBRQuery query) throws ExecutionException {
        // KNN
        NNConfig simConfig = new NNConfig();

        // Global similarity function
        simConfig.setDescriptionSimFunction(new Average());

        // Local similarity functions
        simConfig.addMapping(new Attribute("tipo", DescripcionVivienda.class), new Equal());
        simConfig.addMapping(new Attribute("localizacion", DescripcionVivienda.class), new Equal());
        simConfig.addMapping(new Attribute("habitaciones", DescripcionVivienda.class), new Interval(12));
        simConfig.addMapping(new Attribute("precio", DescripcionVivienda.class), new Interval(20000));

        // Execute NN
        long timeAtStart = System.nanoTime();

        Collection<CBRCase> casesWithoutBlacklistIds = new ArrayList<CBRCase>(caseBase.getCases());
        for (Iterator<CBRCase> it = casesWithoutBlacklistIds.iterator(); it.hasNext();) {
            CBRCase cbrCase = it.next();
            DescripcionVivienda dv = (DescripcionVivienda) cbrCase.getDescription();
            if (blacklist.contains(dv.getId())) {
                it.remove();
            }
        }

        long timeAtBlacklist = System.nanoTime();

        Collection<CBRCase> filtered = FilterBasedRetrievalMethod.filterCases(casesWithoutBlacklistIds, query,
                filterConfig);

        long timeAtFilter = System.nanoTime();

        Collection<RetrievalResult> eval = NNScoringMethod.evaluateSimilarity(filtered, query, simConfig);

        long timeAtEval = System.nanoTime();

        // Select k cases
        selectedCases = selectAndFilter(eval, numberOfResults);

        // Show result
        // DisplayCasesTableMethod.displayCasesInTableBasic(selectedCases);
        /*
         * Compute a direct proportion between the "Duration" and "Price"
         * attributes. NumericDirectProportionMethod.directProportion( new
         * Attribute("Duration",DescripcionVivienda.class), new
         * Attribute("price",DescripcionVivienda.class), query, selectedcases);
         * 
         * // Compute a direct proportion between the "Duration" and "Price"
         * attributes. NumericDirectProportionMethod.directProportion( new
         * Attribute("NumberOfPersons",DescripcionVivienda.class), new
         * Attribute("price",DescripcionVivienda.class), query, selectedcases);
         */

        // Revise

        // Retain
        /*
         * retainDialog.showCases(selectedcases, caseBase.getCases().size());
         * retainDialog.setVisible(true); Collection<CBRCase> casesToRetain =
         * retainDialog.getCasestoRetain(); caseBase.learnCases(casesToRetain);
         */

        double timeForBlacklist = (timeAtBlacklist - timeAtStart) / (double) TimeUnit.SECONDS.toNanos(1);
        double timeForFilter = (timeAtFilter - timeAtBlacklist) / (double) TimeUnit.SECONDS.toNanos(1);
        double timeForEval = (timeAtEval - timeAtFilter) / (double) TimeUnit.SECONDS.toNanos(1);
        double timeForAll = (timeAtEval - timeAtStart) / (double) TimeUnit.SECONDS.toNanos(1);

        System.out.printf("%.3fs retrieval (%.3fs blacklist + %.3fs filter + %.3fs search)%n", //
                timeForAll, timeForBlacklist, timeForFilter, timeForEval);
    }

    private Collection<CBRCase> selectAndFilter (Collection<RetrievalResult> eval, int n) {
        List<CBRCase> cases = new ArrayList<CBRCase>();

        int left = n;
        for (Iterator<RetrievalResult> it = eval.iterator(); it.hasNext() && left > 0;) {
            CBRCase cbrCase = it.next().get_case();
            if (filter(cbrCase)) {
                cases.add(cbrCase);
                left--;
            }
        }

        return cases;
    }

    private boolean filter (CBRCase cbrCase) {
        return true;
    }

    @Override
    public void postCycle () throws ExecutionException {
        caseBase.close();
    }

    public static void main (String[] args) {
        ViviendasRecommender recommender = new ViviendasRecommender();
        // recommender.showMainFrame();
        try {
            recommender.configure();
            recommender.preCycle();

            boolean cont = true;
            while (cont) {
                DescripcionVivienda desc = new DescripcionVivienda(0);
                desc.setPrecio(1000000000);

                CBRQuery query = new CBRQuery();
                query.setDescription(desc);

                recommender.cycle(query);
                cont = false;
            }
            recommender.postCycle();
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(ViviendasRecommender.class).error(e);
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
        }
        System.exit(0);
    }

}

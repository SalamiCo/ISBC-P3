package es.ucm.fdi.isbc.g17;

import es.ucm.fdi.isbc.viviendas.ViviendasConnector;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;
import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.Connector;
import jcolibri.exception.ExecutionException;

public final class ViviendasRecommender implements StandardCBRApplication {
	
	private Connector connector;
	private CBRCaseBase caseBase;
	

	@Override
	public void configure() throws ExecutionException {
		connector = new ViviendasConnector();
		caseBase = new LinealCaseBase();	
	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		caseBase.init(connector);
		return caseBase;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		
	}

	@Override
	public void postCycle() throws ExecutionException {
		caseBase.close();
	}

	public static void main(String[] args) {

		ViviendasRecommender recommender = new ViviendasRecommender();
		//recommender.showMainFrame();
		try {
			recommender.configure();
			recommender.preCycle();
			
			boolean cont = true;
			while (cont) {
				DescripcionVivienda desc = new DescripcionVivienda(0);
				
				CBRQuery query =  new CBRQuery();
				query.setDescription(desc);
				
				recommender.cycle(query);
				cont = false;
			}
			recommender.postCycle();
		} catch (Exception e) {
			org.apache.commons.logging.LogFactory.getLog(
					ViviendasRecommender.class).error(e);
			javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
		}
		System.exit(0);
	}

}

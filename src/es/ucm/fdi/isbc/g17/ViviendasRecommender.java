package es.ucm.fdi.isbc.g17;

import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;

public final class ViviendasRecommender implements StandardCBRApplication {

	@Override
	public void configure() throws ExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public CBRCaseBase preCycle() throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cycle(CBRQuery query) throws ExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void postCycle() throws ExecutionException {
		// TODO Auto-generated method stub

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

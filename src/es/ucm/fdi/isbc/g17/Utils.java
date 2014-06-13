package es.ucm.fdi.isbc.g17;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jcolibri.cbrcore.CBRCase;
import es.ucm.fdi.isbc.viviendas.representacion.DescripcionVivienda;

public final class Utils {

    public static String toHumanReadable (String string) {
        String[] pieces = string.split("\\b+");

        StringBuilder sb = new StringBuilder();
        for (String piece : pieces) {
            piece = piece.trim();
            if (!piece.isEmpty()) {
                sb.append(' ').append(Character.toUpperCase(piece.charAt(0))).append(piece.substring(1).toLowerCase());
            }
        }

        return sb.toString().trim();
    }

    public static Collection<String> getCities (Collection<CBRCase> cases) {
        Set<String> cities = new HashSet<String>();

        for (CBRCase cs : cases) {
            DescripcionVivienda dv = (DescripcionVivienda) cs.getDescription();
            String city = getCity(dv);
            cities.add(city);
        }

        return cities;
    }

    public static String getCity (DescripcionVivienda dv) {
        return dv.getUrl().split("/")[4];
    }

    private Utils () {
    }
}

package es.ucm.fdi.isbc.viviendas.representacion;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CaseComponent;

public class ExtrasFinca implements CaseComponent {

    Integer id;
    boolean ascensor;
    boolean trastero;
    boolean energiaSolar;
    boolean servPorteria;
    boolean parkingComunitario;
    boolean garajePrivado;
    boolean videoportero;

    public ExtrasFinca (int id) {
        super();
        this.id = id;
    }

    public ExtrasFinca (String stringRep) {
        String[] values = stringRep.split(",");
        id = Integer.valueOf(values[0]);
        ascensor = Boolean.valueOf(values[1]);
        trastero = Boolean.valueOf(values[2]);
        energiaSolar = Boolean.valueOf(values[3]);
        servPorteria = Boolean.valueOf(values[4]);
        parkingComunitario = Boolean.valueOf(values[5]);
        garajePrivado = Boolean.valueOf(values[6]);
        videoportero = Boolean.valueOf(values[7]);
    }

    @Override
    public String toString () {
        // No nos podían dar un toString útil, no
        // return id+ "," + ascensor + "," + trastero + "," + energiaSolar + ","
        // + servPorteria + "," + parkingComunitario + "," + garajePrivado
        // + "," + videoportero;

        return sincon("ascensor", ascensor, true) //
                + sincon("trastero", trastero, true) //
                + sincon("enería solar", energiaSolar, true) + "\n"//
                + sincon("portero", servPorteria, true) //
                + sincon("aparcamiento", parkingComunitario, true) //
                + sincon("garaje (privado)", garajePrivado, true) + "\n" //
                + sincon("videoportero", videoportero, false);
    }

    private String sincon (String string, boolean value, boolean comma) {
        return (value ? "con " : "sin ") + string + (comma ? ", " : "");
    }

    @Override
    public Attribute getIdAttribute () {
        return new Attribute("id", DescripcionVivienda.class);
    }

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public boolean isAscensor () {
        return ascensor;
    }

    public void setAscensor (boolean ascensor) {
        this.ascensor = ascensor;
    }

    public boolean isTrastero () {
        return trastero;
    }

    public void setTrastero (boolean trastero) {
        this.trastero = trastero;
    }

    public boolean isEnergiaSolar () {
        return energiaSolar;
    }

    public void setEnergiaSolar (boolean energiaSolar) {
        this.energiaSolar = energiaSolar;
    }

    public boolean isServPorteria () {
        return servPorteria;
    }

    public void setServPorteria (boolean servPorteria) {
        this.servPorteria = servPorteria;
    }

    public boolean isParkingComunitario () {
        return parkingComunitario;
    }

    public void setParkingComunitario (boolean parkingComunitario) {
        this.parkingComunitario = parkingComunitario;
    }

    public boolean isGarajePrivado () {
        return garajePrivado;
    }

    public void setGarajePrivado (boolean garajePrivado) {
        this.garajePrivado = garajePrivado;
    }

    public boolean isVideoportero () {
        return videoportero;
    }

    public void setVideoportero (boolean videoportero) {
        this.videoportero = videoportero;
    }

}

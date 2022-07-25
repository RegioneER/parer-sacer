package it.eng.parer.slite.gen.tablebean;

import it.eng.parer.entity.FasRespFascicolo;

/**
 *
 * @author Moretti_Lu
 */
public class FasRespFascicoloRowBean_ext extends FasRespFascicoloRowBean {

    private final String STR_FASCICOLO = "Gestione fascicoli";
    private final String STR_PROC_AMMIN = "Gestione procedimento amministrativo";

    @Override
    public void entityToRowBean(Object obj) {
        super.entityToRowBean(obj);

        final String s = this.getTiResp();

        if (s.equalsIgnoreCase("FASCICOLO")) {
            this.setTiResp(STR_FASCICOLO);
        } else if (s.equalsIgnoreCase("PROC_AMMIN")) {
            this.setTiResp(STR_PROC_AMMIN);
        }
    }

    @Override
    public FasRespFascicolo rowBeanToEntity() {
        FasRespFascicolo result = super.rowBeanToEntity();

        switch (this.getTiResp()) {
        case STR_FASCICOLO:
            result.setTiResp("FASCICOLO");
            break;
        case STR_PROC_AMMIN:
            result.setTiResp("PROC_AMMIN");
            break;
        }

        return result;
    }
}
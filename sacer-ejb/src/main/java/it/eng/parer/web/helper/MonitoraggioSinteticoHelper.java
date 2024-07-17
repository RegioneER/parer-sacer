/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.web.helper;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.MonVChkAggAmb;
import it.eng.parer.viewEntity.MonVChkAggAmbId;
import it.eng.parer.viewEntity.MonVChkAggEnte;
import it.eng.parer.viewEntity.MonVChkAggEnteId;
import it.eng.parer.viewEntity.MonVChkAggStrut;
import it.eng.parer.viewEntity.MonVChkDocAmb;
import it.eng.parer.viewEntity.MonVChkDocAmbId;
import it.eng.parer.viewEntity.MonVChkDocEnte;
import it.eng.parer.viewEntity.MonVChkDocEnteId;
import it.eng.parer.viewEntity.MonVChkDocNonversAmb;
import it.eng.parer.viewEntity.MonVChkDocNonversAmbId;
import it.eng.parer.viewEntity.MonVChkDocNonversEnte;
import it.eng.parer.viewEntity.MonVChkDocNonversEnteId;
import it.eng.parer.viewEntity.MonVChkDocNonversStrut;
import it.eng.parer.viewEntity.MonVChkDocStrut;
import it.eng.parer.viewEntity.MonVChkDocTipoUd;
import it.eng.parer.viewEntity.MonVChkUdAmb;
import it.eng.parer.viewEntity.MonVChkUdAmbId;
import it.eng.parer.viewEntity.MonVChkUdAnnulAmb;
import it.eng.parer.viewEntity.MonVChkUdAnnulAmbId;
import it.eng.parer.viewEntity.MonVChkUdAnnulEnte;
import it.eng.parer.viewEntity.MonVChkUdAnnulEnteId;
import it.eng.parer.viewEntity.MonVChkUdAnnulStrut;
import it.eng.parer.viewEntity.MonVChkUdAnnulTipoUd;
import it.eng.parer.viewEntity.MonVChkUdEnte;
import it.eng.parer.viewEntity.MonVChkUdEnteId;
import it.eng.parer.viewEntity.MonVChkUdNonversAmb;
import it.eng.parer.viewEntity.MonVChkUdNonversAmbId;
import it.eng.parer.viewEntity.MonVChkUdNonversEnte;
import it.eng.parer.viewEntity.MonVChkUdNonversEnteId;
import it.eng.parer.viewEntity.MonVChkUdNonversStrut;
import it.eng.parer.viewEntity.MonVChkUdStrut;
import it.eng.parer.viewEntity.MonVChkUdTipoUd;
import it.eng.parer.viewEntity.MonVChkVersAmb;
import it.eng.parer.viewEntity.MonVChkVersAmbId;
import it.eng.parer.viewEntity.MonVChkVersEnte;
import it.eng.parer.viewEntity.MonVChkVersEnteId;
import it.eng.parer.viewEntity.MonVChkVersStrut;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class MonitoraggioSinteticoHelper extends GenericHelper {

    Logger log = LoggerFactory.getLogger(MonitoraggioSinteticoHelper.class);

    /*
     * GENERAZIONE RIEPILOGO SINTETICO
     */
    /*
     * Generazione MonVChk in base ai parametri scelti dall'utente
     */
    public Object getMonVChk(String viewUd, String parameters, BigDecimal param1, Long param2) {
        String queryUd = "SELECT view FROM " + viewUd + " view WHERE " + parameters;
        Query query = getEntityManager().createQuery(queryUd);
        query.setParameter("param1", param1);
        if (param2 != null) {
            query.setParameter("param2", bigDecimalFromLong(param2));
        }
        return query.getSingleResult();
    }

    /*
     * Generazione MonVpChk in base ai parametri scelti dall'utente
     */
    public Object getMonVpChk(String viewUd, String parameters, BigDecimal param1, Long param2) {
        String queryUd = "SELECT * FROM " + viewUd + parameters;
        Query query = getEntityManager().createNativeQuery(queryUd);
        query.setParameter("param1", param1);
        if (param2 != null) {
            query.setParameter("param2", bigDecimalFromLong(param2));
        }

        Object[] oQuery = (Object[]) query.getSingleResult();

        // Converto il risultato in RowBean dell'entity associata
        if (viewUd.equals("Mon_Vp_Chk_Ud_Tipo_Ud")) {
            MonVChkUdTipoUd mon = new MonVChkUdTipoUd();
            mon.setIdTipoUnitaDoc((BigDecimal) oQuery[0]);
            mon.setFlUdCorr(((Character) oQuery[1]).toString());
            mon.setFlUd30gg(((Character) oQuery[2]).toString());
            mon.setFlUdAttesaMemCorr(((Character) oQuery[3]).toString());
            mon.setFlUdAttesaMem30gg(((Character) oQuery[4]).toString());
            mon.setFlUdAttesaSchedCorr(((Character) oQuery[5]).toString());
            mon.setFlUdAttesaSched30gg(((Character) oQuery[6]).toString());
            mon.setFlUdNoselSchedCorr(((Character) oQuery[7]).toString());
            mon.setFlUdNoselSched30gg(((Character) oQuery[8]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Doc_Tipo_Ud")) {
            MonVChkDocTipoUd mon = new MonVChkDocTipoUd();
            mon.setIdTipoUnitaDoc((BigDecimal) oQuery[0]);
            mon.setFlDocCorr(((Character) oQuery[1]).toString());
            mon.setFlDoc30gg(((Character) oQuery[2]).toString());
            mon.setFlDocAttesaMemCorr(((Character) oQuery[3]).toString());
            mon.setFlDocAttesaMem30gg(((Character) oQuery[4]).toString());
            mon.setFlDocAttesaSchedCorr(((Character) oQuery[5]).toString());
            mon.setFlDocAttesaSched30gg(((Character) oQuery[6]).toString());
            mon.setFlDocNoselSchedCorr(((Character) oQuery[7]).toString());
            mon.setFlDocNoselSched30gg(((Character) oQuery[8]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Annul_Tipo_Ud")) {
            MonVChkUdAnnulTipoUd mon = new MonVChkUdAnnulTipoUd();
            mon.setIdTipoUnitaDoc((BigDecimal) oQuery[0]);
            mon.setFlUdAnnulDafarePing(((Character) oQuery[1]).toString());
            mon.setFlUdAnnulDafareSacer(((Character) oQuery[2]).toString());
            mon.setFlUdAnnul(((Character) oQuery[3]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Strut")) {
            MonVChkUdStrut mon = new MonVChkUdStrut();
            mon.setIdStrut((BigDecimal) oQuery[0]);
            mon.setFlUdCorr(((Character) oQuery[1]).toString());
            mon.setFlUd30gg(((Character) oQuery[2]).toString());
            mon.setFlUdAttesaMemCorr(((Character) oQuery[3]).toString());
            mon.setFlUdAttesaMem30gg(((Character) oQuery[4]).toString());
            mon.setFlUdAttesaSchedCorr(((Character) oQuery[5]).toString());
            mon.setFlUdAttesaSched30gg(((Character) oQuery[6]).toString());
            mon.setFlUdNoselSchedCorr(((Character) oQuery[7]).toString());
            mon.setFlUdNoselSched30gg(((Character) oQuery[8]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Doc_Strut")) {
            MonVChkDocStrut mon = new MonVChkDocStrut();
            mon.setIdStrut((BigDecimal) oQuery[0]);
            mon.setFlDocCorr(((Character) oQuery[1]).toString());
            mon.setFlDoc30gg(((Character) oQuery[2]).toString());
            mon.setFlDocAttesaMemCorr(((Character) oQuery[3]).toString());
            mon.setFlDocAttesaMem30gg(((Character) oQuery[4]).toString());
            mon.setFlDocAttesaSchedCorr(((Character) oQuery[5]).toString());
            mon.setFlDocAttesaSched30gg(((Character) oQuery[6]).toString());
            mon.setFlDocNoselSchedCorr(((Character) oQuery[7]).toString());
            mon.setFlDocNoselSched30gg(((Character) oQuery[8]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Vers_Strut")) {
            MonVChkVersStrut mon = new MonVChkVersStrut();
            mon.setIdStrut((BigDecimal) oQuery[0]);
            mon.setFlVersRisoltiCorr(((Character) oQuery[1]).toString());
            mon.setFlVersRisolti30gg(((Character) oQuery[2]).toString());
            mon.setFlVersNoverifCorr(((Character) oQuery[3]).toString());
            mon.setFlVersNoverif30gg(((Character) oQuery[4]).toString());
            mon.setFlVersVerifCorr(((Character) oQuery[5]).toString());
            mon.setFlVersVerif30gg(((Character) oQuery[6]).toString());
            mon.setFlVersNorisolubCorr(((Character) oQuery[7]).toString());
            mon.setFlVersNorisolub30gg(((Character) oQuery[8]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Agg_Strut")) {
            MonVChkAggStrut mon = new MonVChkAggStrut();
            mon.setIdStrut((BigDecimal) oQuery[0]);
            mon.setFlAggRisoltiCorr(((Character) oQuery[1]).toString());
            mon.setFlAggRisolti30gg(((Character) oQuery[2]).toString());
            mon.setFlAggNoverifCorr(((Character) oQuery[3]).toString());
            mon.setFlAggNoverif30gg(((Character) oQuery[4]).toString());
            mon.setFlAggVerifCorr(((Character) oQuery[5]).toString());
            mon.setFlAggVerif30gg(((Character) oQuery[6]).toString());
            mon.setFlAggNorisolubCorr(((Character) oQuery[7]).toString());
            mon.setFlAggNorisolub30gg(((Character) oQuery[8]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Nonvers_Strut")) {
            MonVChkUdNonversStrut mon = new MonVChkUdNonversStrut();
            mon.setIdStrut((BigDecimal) oQuery[0]);
            mon.setFlUdNonversNoverif(((Character) oQuery[1]).toString());
            mon.setFlUdNonversVerif(((Character) oQuery[2]).toString());
            mon.setFlUdNonversNorisolub(((Character) oQuery[3]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Doc_Nonvers_Strut")) {
            MonVChkDocNonversStrut mon = new MonVChkDocNonversStrut();
            mon.setIdStrut((BigDecimal) oQuery[0]);
            mon.setFlDocNonversNoverif(((Character) oQuery[1]).toString());
            mon.setFlDocNonversVerif(((Character) oQuery[2]).toString());
            mon.setFlDocNonversNorisolub(((Character) oQuery[3]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Annul_Strut")) {
            MonVChkUdAnnulStrut mon = new MonVChkUdAnnulStrut();
            mon.setIdStrut((BigDecimal) oQuery[0]);
            mon.setFlUdAnnulDafarePing(((Character) oQuery[1]).toString());
            mon.setFlUdAnnulDafareSacer(((Character) oQuery[2]).toString());
            mon.setFlUdAnnul(((Character) oQuery[3]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Ente")) {
            MonVChkUdEnte mon = new MonVChkUdEnte();
            MonVChkUdEnteId id = new MonVChkUdEnteId();
            id.setIdEnte((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkUdEnteId(id);
            mon.setFlUdCorr(((Character) oQuery[2]).toString());
            mon.setFlUd30gg(((Character) oQuery[3]).toString());
            mon.setFlUdAttesaMemCorr(((Character) oQuery[4]).toString());
            mon.setFlUdAttesaMem30gg(((Character) oQuery[5]).toString());
            mon.setFlUdAttesaSchedCorr(((Character) oQuery[6]).toString());
            mon.setFlUdAttesaSched30gg(((Character) oQuery[7]).toString());
            mon.setFlUdNoselSchedCorr(((Character) oQuery[8]).toString());
            mon.setFlUdNoselSched30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Doc_Ente")) {
            MonVChkDocEnte mon = new MonVChkDocEnte();
            MonVChkDocEnteId id = new MonVChkDocEnteId();
            id.setIdEnte((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkDocEnteId(id);
            mon.setFlDocCorr(((Character) oQuery[2]).toString());
            mon.setFlDoc30gg(((Character) oQuery[3]).toString());
            mon.setFlDocAttesaMemCorr(((Character) oQuery[4]).toString());
            mon.setFlDocAttesaMem30gg(((Character) oQuery[5]).toString());
            mon.setFlDocAttesaSchedCorr(((Character) oQuery[6]).toString());
            mon.setFlDocAttesaSched30gg(((Character) oQuery[7]).toString());
            mon.setFlDocNoselSchedCorr(((Character) oQuery[8]).toString());
            mon.setFlDocNoselSched30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Vers_Ente")) {
            MonVChkVersEnte mon = new MonVChkVersEnte();
            MonVChkVersEnteId id = new MonVChkVersEnteId();
            id.setIdEnte((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkVersEnteId(id);
            mon.setFlVersRisoltiCorr(((Character) oQuery[2]).toString());
            mon.setFlVersRisolti30gg(((Character) oQuery[3]).toString());
            mon.setFlVersNoverifCorr(((Character) oQuery[4]).toString());
            mon.setFlVersNoverif30gg(((Character) oQuery[5]).toString());
            mon.setFlVersVerifCorr(((Character) oQuery[6]).toString());
            mon.setFlVersVerif30gg(((Character) oQuery[7]).toString());
            mon.setFlVersNorisolubCorr(((Character) oQuery[8]).toString());
            mon.setFlVersNorisolub30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Agg_Ente")) {
            MonVChkAggEnte mon = new MonVChkAggEnte();
            MonVChkAggEnteId id = new MonVChkAggEnteId();
            id.setIdEnte((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkAggEnteId(id);
            mon.setFlAggRisoltiCorr(((Character) oQuery[2]).toString());
            mon.setFlAggRisolti30gg(((Character) oQuery[3]).toString());
            mon.setFlAggNoverifCorr(((Character) oQuery[4]).toString());
            mon.setFlAggNoverif30gg(((Character) oQuery[5]).toString());
            mon.setFlAggVerifCorr(((Character) oQuery[6]).toString());
            mon.setFlAggVerif30gg(((Character) oQuery[7]).toString());
            mon.setFlAggNorisolubCorr(((Character) oQuery[8]).toString());
            mon.setFlAggNorisolub30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Nonvers_Ente")) {
            MonVChkUdNonversEnte mon = new MonVChkUdNonversEnte();
            MonVChkUdNonversEnteId id = new MonVChkUdNonversEnteId();
            id.setIdEnte((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkUdNonversEnteId(id);
            mon.setFlUdNonversNoverif(((Character) oQuery[2]).toString());
            mon.setFlUdNonversVerif(((Character) oQuery[3]).toString());
            mon.setFlUdNonversNorisolub(((Character) oQuery[4]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Doc_Nonvers_Ente")) {
            MonVChkDocNonversEnte mon = new MonVChkDocNonversEnte();
            MonVChkDocNonversEnteId id = new MonVChkDocNonversEnteId();
            id.setIdEnte((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setFlDocNonversNoverif(((Character) oQuery[2]).toString());
            mon.setFlDocNonversVerif(((Character) oQuery[3]).toString());
            mon.setFlDocNonversNorisolub(((Character) oQuery[4]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Annul_Ente")) {
            MonVChkUdAnnulEnte mon = new MonVChkUdAnnulEnte();
            MonVChkUdAnnulEnteId id = new MonVChkUdAnnulEnteId();
            id.setIdEnte((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setFlUdAnnulDafarePing(((Character) oQuery[2]).toString());
            mon.setFlUdAnnulDafareSacer(((Character) oQuery[3]).toString());
            mon.setFlUdAnnul(((Character) oQuery[4]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Amb")) {
            MonVChkUdAmb mon = new MonVChkUdAmb();
            MonVChkUdAmbId id = new MonVChkUdAmbId();
            id.setIdAmbiente((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkUdAmbId(id);
            mon.setFlUdCorr(((Character) oQuery[2]).toString());
            mon.setFlUd30gg(((Character) oQuery[3]).toString());
            mon.setFlUdAttesaMemCorr(((Character) oQuery[4]).toString());
            mon.setFlUdAttesaMem30gg(((Character) oQuery[5]).toString());
            mon.setFlUdAttesaSchedCorr(((Character) oQuery[6]).toString());
            mon.setFlUdAttesaSched30gg(((Character) oQuery[7]).toString());
            mon.setFlUdNoselSchedCorr(((Character) oQuery[8]).toString());
            mon.setFlUdNoselSched30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Doc_Amb")) {
            MonVChkDocAmb mon = new MonVChkDocAmb();
            MonVChkDocAmbId id = new MonVChkDocAmbId();
            id.setIdAmbiente((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkDocAmbId(id);
            mon.setFlDocCorr(((Character) oQuery[2]).toString());
            mon.setFlDoc30gg(((Character) oQuery[3]).toString());
            mon.setFlDocAttesaMemCorr(((Character) oQuery[4]).toString());
            mon.setFlDocAttesaMem30gg(((Character) oQuery[5]).toString());
            mon.setFlDocAttesaSchedCorr(((Character) oQuery[6]).toString());
            mon.setFlDocAttesaSched30gg(((Character) oQuery[7]).toString());
            mon.setFlDocNoselSchedCorr(((Character) oQuery[8]).toString());
            mon.setFlDocNoselSched30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Vers_Amb")) {
            MonVChkVersAmb mon = new MonVChkVersAmb();
            MonVChkVersAmbId id = new MonVChkVersAmbId();
            id.setIdAmbiente((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkVersAmbId(id);
            mon.setFlVersRisoltiCorr(((Character) oQuery[2]).toString());
            mon.setFlVersRisolti30gg(((Character) oQuery[3]).toString());
            mon.setFlVersNoverifCorr(((Character) oQuery[4]).toString());
            mon.setFlVersNoverif30gg(((Character) oQuery[5]).toString());
            mon.setFlVersVerifCorr(((Character) oQuery[6]).toString());
            mon.setFlVersVerif30gg(((Character) oQuery[7]).toString());
            mon.setFlVersNorisolubCorr(((Character) oQuery[8]).toString());
            mon.setFlVersNorisolub30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Agg_Amb")) {
            MonVChkAggAmb mon = new MonVChkAggAmb();
            MonVChkAggAmbId id = new MonVChkAggAmbId();
            id.setIdAmbiente((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkAggAmbId(id);
            mon.setFlAggRisoltiCorr(((Character) oQuery[2]).toString());
            mon.setFlAggRisolti30gg(((Character) oQuery[3]).toString());
            mon.setFlAggNoverifCorr(((Character) oQuery[4]).toString());
            mon.setFlAggNoverif30gg(((Character) oQuery[5]).toString());
            mon.setFlAggVerifCorr(((Character) oQuery[6]).toString());
            mon.setFlAggVerif30gg(((Character) oQuery[7]).toString());
            mon.setFlAggNorisolubCorr(((Character) oQuery[8]).toString());
            mon.setFlAggNorisolub30gg(((Character) oQuery[9]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Nonvers_Amb")) {
            MonVChkUdNonversAmb mon = new MonVChkUdNonversAmb();
            MonVChkUdNonversAmbId id = new MonVChkUdNonversAmbId();
            id.setIdAmbiente((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setMonVChkUdNonversAmbId(id);
            mon.setFlUdNonversNoverif(((Character) oQuery[2]).toString());
            mon.setFlUdNonversVerif(((Character) oQuery[3]).toString());
            mon.setFlUdNonversNorisolub(((Character) oQuery[4]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Doc_Nonvers_Amb")) {
            MonVChkDocNonversAmb mon = new MonVChkDocNonversAmb();
            MonVChkDocNonversAmbId id = new MonVChkDocNonversAmbId();
            id.setIdAmbiente((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setFlDocNonversNoverif(((Character) oQuery[2]).toString());
            mon.setFlDocNonversVerif(((Character) oQuery[3]).toString());
            mon.setFlDocNonversNorisolub(((Character) oQuery[4]).toString());
            return mon;
        } else if (viewUd.equals("Mon_Vp_Chk_Ud_Annul_Amb")) {
            MonVChkUdAnnulAmb mon = new MonVChkUdAnnulAmb();
            MonVChkUdAnnulAmbId id = new MonVChkUdAnnulAmbId();
            id.setIdAmbiente((BigDecimal) oQuery[0]);
            id.setIdUserIam((BigDecimal) oQuery[1]);
            mon.setFlUdAnnulDafarePing(((Character) oQuery[2]).toString());
            mon.setFlUdAnnulDafareSacer(((Character) oQuery[3]).toString());
            mon.setFlUdAnnul(((Character) oQuery[4]).toString());
            return mon;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public List getMonVCnt(String viewUd, String parameters, BigDecimal param1, Long param2) {
        String queryUd = "SELECT view FROM " + viewUd + " view WHERE " + parameters;
        Query query = getEntityManager().createQuery(queryUd);
        query.setParameter("param1", param1);
        if (param2 != null) {
            query.setParameter("param2", bigDecimalFromLong(param2));
        }
        return query.getResultList();
    }
}

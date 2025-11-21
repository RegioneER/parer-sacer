/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.informazioni.noteRilascio.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.grantedEntity.SIAplApplic;
import it.eng.parer.grantedEntity.SIAplNotaRilascio;
import it.eng.parer.informazioni.noteRilascio.helper.NoteRilascioHelper;
import it.eng.parer.slite.gen.tablebean.SIAplApplicRowBean;
import it.eng.parer.slite.gen.tablebean.SIAplNotaRilascioRowBean;
import it.eng.parer.slite.gen.tablebean.SIAplNotaRilascioTableBean;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;

/**
 * Ejb note di rilascio di Sacer
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class NoteRilascioEjb {

    public NoteRilascioEjb() {
    }

    @EJB
    private NoteRilascioHelper noteRilascioHelper;
    private static final Logger log = LoggerFactory.getLogger(NoteRilascioEjb.class);

    public SIAplNotaRilascioTableBean getAplNoteRilascioTableBean(String nmApplic) throws EMFError {
        SIAplNotaRilascioTableBean noteRilascioTableBean = new SIAplNotaRilascioTableBean();
        long idApplic = noteRilascioHelper.getAplApplic(nmApplic).getIdApplic();
        List<SIAplNotaRilascio> list = noteRilascioHelper
                .getAplNoteRilascioList(BigDecimal.valueOf(idApplic));
        try {
            if (!list.isEmpty()) {
                for (SIAplNotaRilascio notaRilascio : list) {
                    SIAplNotaRilascioRowBean row = new SIAplNotaRilascioRowBean();
                    row = (SIAplNotaRilascioRowBean) Transform.entity2RowBean(notaRilascio);
                    row.setString("nm_applic", notaRilascio.getSiAplApplic().getNmApplic());
                    noteRilascioTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return noteRilascioTableBean;
    }

    public SIAplApplicRowBean getAplApplicRowBean(BigDecimal idApplic) {
        SIAplApplicRowBean applicRowBean = new SIAplApplicRowBean();
        SIAplApplic applic = noteRilascioHelper.getAplApplicById(idApplic);
        try {
            if (applic != null) {
                applicRowBean = (SIAplApplicRowBean) Transform.entity2RowBean(applic);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return applicRowBean;
    }

    public SIAplNotaRilascioTableBean getAplNoteRilascioPrecTableBean(BigDecimal idApplic,
            BigDecimal idNotaRilascio, Date dtVersione) throws EMFError {
        SIAplNotaRilascioTableBean noteRilascioPrecTableBean = new SIAplNotaRilascioTableBean();
        List<SIAplNotaRilascio> noteRilascioPrecList = noteRilascioHelper
                .getAplNoteRilascioPrecList(idApplic, idNotaRilascio, dtVersione);
        try {
            if (noteRilascioPrecList != null && !noteRilascioPrecList.isEmpty()) {
                noteRilascioPrecTableBean = (SIAplNotaRilascioTableBean) Transform
                        .entities2TableBean(noteRilascioPrecList);
                // for (SIAplNotaRilascio notaRilascio : noteRilascioPrecList) {
                // SIAplNotaRilascioRowBean row = new SIAplNotaRilascioRowBean();
                // row = (SIAplNotaRilascioRowBean) Transform.entity2RowBean(notaRilascio);
                // row.setString("nm_applic", notaRilascio.getSiAplApplic().getNmApplic());
                // noteRilascioPrecTableBean.add(row);
                // }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return noteRilascioPrecTableBean;
    }

    public SIAplNotaRilascioRowBean getAplNotaRilascioRowBean(BigDecimal idNotaRilascio)
            throws EMFError {
        SIAplNotaRilascioRowBean notaRilascioRowBean = new SIAplNotaRilascioRowBean();
        if (idNotaRilascio != null) {
            SIAplNotaRilascio notaRilascio = noteRilascioHelper
                    .getAplNotaRilascioById(idNotaRilascio);
            if (notaRilascio != null) {
                try {
                    notaRilascioRowBean = (SIAplNotaRilascioRowBean) Transform
                            .entity2RowBean(notaRilascio);
                    notaRilascioRowBean.setString("nm_applic",
                            notaRilascio.getSiAplApplic().getNmApplic());
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    log.error("Errore durante il recupero della nota rilascio "
                            + ExceptionUtils.getRootCauseMessage(e), e);
                }
            }
        }
        return notaRilascioRowBean;
    }
}

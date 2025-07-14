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

package it.eng.parer.volume.ejb;

import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.volume.helper.VolumeHelper;

/**
 *
 * @author Agati_D
 */
@Stateless
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class VolumeEjb {

    Logger log = LoggerFactory.getLogger(VolumeEjb.class);
    @EJB
    private VolumeHelper volumeHelper;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
    // Inserire la riga sotto nel file di property
    String BATCH_USERNAME = "JOB_CREAZIONE_VOLUMI";

    public VolumeEjb() {
    }

    public byte[] retrieveFileByIdVolume(long idVolume, String fileType) {
	VolVolumeConserv volume = volumeHelper.retrieveVolumeById(idVolume);
	return volumeHelper.retrieveFile(volume, fileType);
    }
}

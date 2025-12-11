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

package it.eng.parer.grantedEntity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SACER_LOG.LOG_LOGIN_USER database table.
 */
@Entity
@Table(schema = "SACER_LOG", name = "LOG_LOGIN_USER")
public class SLLogLoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idLoginUser;

    private String nmUserid;

    private String cdIndIpClient;

    private String cdIndServer;

    private String tipoEvento;

    private Date dtEvento;

    private String cdIdEsterno;

    private String tipoUtenteAuth;

    private SIAplApplic sIAplApplic;

    public SLLogLoginUser() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_LOGIN_USER")
    @GenericGenerator(name = "SLOG_LOGIN_USER_ID_LOGIN_USER_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = PersistentIdentifierGenerator.SCHEMA, value = "SACER_LOG"),
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SLOG_LOGIN_USER"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SLOG_LOGIN_USER_ID_LOGIN_USER_GENERATOR")
    public Long getIdLoginUser() {
        return idLoginUser;
    }

    public void setIdLoginUser(Long idLoginUser) {
        this.idLoginUser = idLoginUser;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "CD_IND_IP_CLIENT")
    public String getCdIndIpClient() {
        return cdIndIpClient;
    }

    public void setCdIndIpClient(String cdIndIpClient) {
        this.cdIndIpClient = cdIndIpClient;
    }

    @Column(name = "CD_IND_SERVER")
    public String getCdIndServer() {
        return cdIndServer;
    }

    public void setCdIndServer(String cdIndServer) {
        this.cdIndServer = cdIndServer;
    }

    @Column(name = "TIPO_EVENTO")
    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_EVENTO")
    public Date getDtEvento() {
        return dtEvento;
    }

    public void setDtEvento(Date dtEvento) {
        this.dtEvento = dtEvento;
    }

    @Column(name = "CD_ID_ESTERNO")
    public String getCdIdEsterno() {
        return cdIdEsterno;
    }

    public void setCdIdEsterno(String cdIdEsterno) {
        this.cdIdEsterno = cdIdEsterno;
    }

    @Column(name = "TIPO_UTENTE_AUTH")
    public String getTipoUtenteAuth() {
        return tipoUtenteAuth;
    }

    public void setTipoUtenteAuth(String tipoUtenteAuth) {
        this.tipoUtenteAuth = tipoUtenteAuth;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_APPLIC")
    public SIAplApplic getsIAplApplic() {
        return sIAplApplic;
    }

    public void setsIAplApplic(SIAplApplic sIAplApplic) {
        this.sIAplApplic = sIAplApplic;
    }
}

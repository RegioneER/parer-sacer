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

/// *
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
// package it.eng.parer.web.security;
//
// import it.eng.parer.ws.replicaUtente.ejb.ReplicaUtenteEjb;
// import it.eng.saceriam.ws.reputente.CancellaUtenteRisposta;
// import it.eng.saceriam.ws.reputente.InserimentoUtenteRisposta;
// import it.eng.saceriam.ws.reputente.ListaOrganizAbil;
// import it.eng.saceriam.ws.reputente.ModificaUtenteRisposta;
// import it.eng.saceriam.ws.reputente.ReplicaUtenteInterface;
// import java.util.Date;
// import javax.ejb.EJB;
//
/// **
// *
// * @author Quaranta_M
// */
// public class ReplicaUtenteImpl implements ReplicaUtenteInterface {
//
// @EJB(mappedName = "java:app/Parer-ejb/ReplicaUtenteEjb")
// private ReplicaUtenteEjb repUtenteEjb;
//
// @Override
// public InserimentoUtenteRisposta inserimentoUtente(Integer idUserIam, String nmUserid,
// String cdPsw, String nmCognomeUser, String nmNomeUser,
// String flAttivo, Date dtRegPsw, Date dtScadPsw,
// ListaOrganizAbil listaOrganizAbil) {
// return repUtenteEjb.inserimentoUtente(idUserIam, nmUserid, cdPsw, nmCognomeUser, nmNomeUser,
/// flAttivo, dtRegPsw,
/// dtScadPsw, listaOrganizAbil);
// }
//
// @Override
// public CancellaUtenteRisposta cancellaUtente(Integer idUserIam) {
// return repUtenteEjb.cancellaUtente(idUserIam);
// }
//
// @Override
// public ModificaUtenteRisposta modificaUtente(Integer idUserIam, String nmUserid,
// String cdPsw, String nmCognomeUser, String nmNomeUser,
// String flAttivo, Date dtRegPsw, Date dtScadPsw,
// ListaOrganizAbil listaOrganizAbil) {
// return repUtenteEjb.modificaUtente(idUserIam, nmUserid, cdPsw, nmCognomeUser, nmNomeUser,
/// flAttivo, dtRegPsw,
/// dtScadPsw, listaOrganizAbil);
// }
//
// }

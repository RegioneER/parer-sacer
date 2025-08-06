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

package it.eng.parer.web.dto;

public class PairAuth<Appl, Auth> {

    private final Appl appl;
    private final Auth auth;

    public PairAuth(Appl appl, Auth auth) {
	this.appl = appl;
	this.auth = auth;
    }

    public Appl getAppl() {
	return appl;
    }

    public Auth getAuth() {
	return auth;
    }

    @Override
    public int hashCode() {
	return appl.hashCode() ^ auth.hashCode();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object o) {
	if (o == null) {
	    return false;
	}
	if (!(o instanceof PairAuth)) {
	    return false;
	}
	PairAuth pairo = (PairAuth) o;
	return this.appl.equals(pairo.getAppl()) && this.auth.equals(pairo.getAuth());
    }

    @Override
    public String toString() {
	return "PairAuth{" + "appl=" + appl + ", auth=" + auth + '}';
    }
}

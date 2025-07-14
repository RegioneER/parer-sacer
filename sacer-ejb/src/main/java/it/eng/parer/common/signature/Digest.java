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

package it.eng.parer.common.signature;

/**
 * Constants about digest (hash of a file)
 *
 * @author Moretti_Lu
 */
public abstract class Digest {

    private Digest() {
    }

    /**
     * Type of encoding
     */
    public enum Encoding {
	base64
    }

    /**
     * Type of digest algorithm
     */
    public enum DigestAlgorithm {
	SHA1("SHA-1"), SHA256("SHA-256");

	private final String value;

	private DigestAlgorithm(final String alg) {
	    this.value = alg;
	}

	public String getValue() {
	    return this.value;
	}
    }
}

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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.exception;

import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;

/**
 *
 * @author sinatti_s
 */
public class SacerRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -8085057602273373872L;
    private final SacerErrorCategory category;

    public SacerRuntimeException() {
        super();
        this.category = SacerErrorCategory.INTERNAL_ERROR; // default
    }

    public SacerRuntimeException(SacerErrorCategory category) {
        super();
        this.category = category;
    }

    public SacerRuntimeException(String message, Throwable throwable, SacerErrorCategory category) {
        super(message, throwable);
        this.category = category;
    }

    public SacerRuntimeException(Throwable throwable, SacerErrorCategory category) {
        super(throwable);
        this.category = category;
    }

    public SacerRuntimeException(String message, SacerErrorCategory category) {
        super(message);
        this.category = category;
    }

    public SacerErrorCategory getCategory() {
        return category;
    }

    @Override
    public String getLocalizedMessage() {
        return "[" + getCategory().toString() + "]" + "  " + super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return "[" + getCategory().toString() + "]" + "  " + super.getMessage();
    }

}

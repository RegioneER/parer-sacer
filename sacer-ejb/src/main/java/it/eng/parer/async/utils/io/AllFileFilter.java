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

package it.eng.parer.async.utils.io;

import java.io.File;
import java.io.FileFilter;

/**
 * Fornisce i servizi di base per filtrare il contenuto di una directory.
 * <p>
 * L'implementazione serve ad individuare gli estremi di tutti i file contenuti in una directory: in pratica, non
 * applica nessun filtro sul parametro previsto dal metodo {@link #accept(File)}.
 * <p>
 *
 * @author DiLorenzo_F
 *
 */
public final class AllFileFilter implements FileFilter {

    /** <code>Singleton</code> con cui filtrare il contenuto di una directory */
    private static AllFileFilter mFileFilter = new AllFileFilter();

    /**
     * Costruttore.
     *
     */
    private AllFileFilter() {
    }

    /**
     * Ritorna l'implementazione con cui filtrare il contenuto di una directory.
     *
     * @return Implementazione con cui filtrare il contenuto di una directory.
     */
    public static AllFileFilter getInstance() {
        return mFileFilter;
    }

    @Override
    public boolean accept(final File pathname) {
        return true;
    }

}

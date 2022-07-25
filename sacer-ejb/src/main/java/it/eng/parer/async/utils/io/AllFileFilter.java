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

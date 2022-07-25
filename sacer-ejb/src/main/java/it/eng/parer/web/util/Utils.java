package it.eng.parer.web.util;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Bonora_L
 */
public class Utils {

    /**
     * Metodo statico per ordinare un enum tramite il valore
     *
     * @param <T>
     *            oggetti generics di tipo Enum
     *
     * @param enumValues
     *            l'array di valori dell'enum
     * 
     * @return la collezione ordinata
     */
    public static <T extends Enum<?>> Collection<T> sortEnum(T[] enumValues) {
        SortedMap<String, T> map = new TreeMap<String, T>();
        for (T l : enumValues) {
            map.put(l.name(), l);
        }
        return map.values();
    }
}
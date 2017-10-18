package ca.appostrophe.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * Imported from CBC code on December 16th 2015 and extended since
 */
public class ArrayUtils
{
    private static final Object[] EMPTY = new Object[0];
    private static final int CACHE_SIZE = 73;
    private static final Object[] sCache = new Object[CACHE_SIZE];

    private ArrayUtils() { /* cannot be instantiated */ }

    public static int idealBooleanArraySize(int need) {
        return idealByteArraySize(need);
    }

    public static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++)
            if (need <= (1 << i) - 12)
                return (1 << i) - 12;

        return need;
    }

    public static int idealShortArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealCharArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealFloatArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealObjectArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealLongArraySize(int need) {
        return idealByteArraySize(need * 8) / 8;
    }

    /**
     * Checks if the beginnings of two byte arrays are equal.
     *
     * @param array1 the first byte array
     * @param array2 the second byte array
     * @param length the number of bytes to check
     * @return true if they're equal, false otherwise
     */
    public static boolean equals(byte[] array1, byte[] array2, int length) {
        if (array1 == array2) {
            return true;
        }
        if (array1 == null || array2 == null || array1.length < length || array2.length < length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }


    /**
     * Checks that value is present as at least one of the elements of the array.
     * @param array the array to check in
     * @param value the value to check for
     * @return true if the value is present in the array
     */
    public static <T> boolean contains(T[] array, T value) {
        for (T element : array) {
            if (element == null) {
                if (value == null) return true;
            } else {
                if (value != null && element.equals(value)) return true;
            }
        }
        return false;
    }

    public static <T> T last(ArrayList<T> array) {
        if (array == null || array.size() == 0) {
            return null;
        }
        return array.get(array.size() - 1);
    }

    public static <T, P> P[] project(final List<T> list, final ListProjectionInterface<T, P>
            project) {

        final ArrayList<P> projection = new ArrayList<>();
        for (T t : list) {
            projection.add(project.project(t));
        }
        return projection.toArray(project.getTypeArray(projection.size()));
    }

    public static <T> T[] filter(final List<T> list, final ListFilteringInterface<T> filter) {
        final ArrayList<T> filtered = new ArrayList<>();
        for (T t : list) {
            if (filter.keep(t)) {
                filtered.add(t);
            }
        }
        return filtered.toArray(filter.getTypeArray(filtered.size()));
    }

    public static <T, P> P concat(final List<T> list, P concatenation,
                                  final ListConcatenationInterface<T, P> callback) {
        for (T t : list) {
            concatenation = callback.concat(concatenation, t);
        }
        return concatenation;
    }

    public interface ListProjectionInterface<T, P> extends ListEnumerationInterface<P> {

        P project(final T value);
    }

    public interface ListFilteringInterface<T> extends ListEnumerationInterface<T> {

        boolean keep(final T value);
    }

    public interface ListConcatenationInterface<T, P> {

        P concat(final P concatenation, final T value);
    }

    public interface ListEnumerationInterface<T> {
        T[] getTypeArray(int size);
    }
}

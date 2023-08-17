package me.illusion.skyblockcore.common.utilities.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility class for lists and collections
 */
public final class ListUtils {

    private ListUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Obtains a random element from the collection
     *
     * @param set The collection
     * @param <T> The collection type
     * @return The random element
     */
    public static <T> T getRandom(Collection<T> set) {
        int size = set.size();
        int index = ThreadLocalRandom.current().nextInt(size);
        int iteration = 0;

        for (T t : set) {
            if (iteration == index) {
                return t;
            }

            iteration++;
        }

        return null;
    }

    /**
     * Obtains a random element from the list
     *
     * @param list The list
     * @param <T>  The list type
     * @return The random element
     */
    public static <T> T getRandom(List<T> list) {
        int size = list.size();

        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return list.get(0);
        }

        Random random = ThreadLocalRandom.current();

        return list.get(random.nextInt(size));
    }

    /**
     * Obtains a random element from an array
     *
     * @param array The array
     * @param <T>   The array type
     * @return The random element
     */
    public static <T> T getRandom(T[] array) {
        int size = array.length;

        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return array[0];
        }

        Random random = ThreadLocalRandom.current();

        return array[random.nextInt(size)];
    }

    /**
     * Shuffles the list
     *
     * @param list The list
     * @param <T>  The list type
     * @return The list
     */
    public static <T> List<T> randomize(List<T> list) {
        Collections.shuffle(list);
        return list;
    }

    /**
     * Inserts a value into the list at the specified index, moving all other elements up
     *
     * @param list  The list
     * @param index The index
     * @param value The value
     * @param <T>   The list type
     */
    public static <T> void insertInList(List<T> list, int index, T value) {
        if (index >= list.size()) {
            list.add(value);
            return;
        }

        List<T> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i == index) {
                newList.add(value);
            }

            newList.add(list.get(i));
        }

        list.clear();
        list.addAll(newList);
    }
}

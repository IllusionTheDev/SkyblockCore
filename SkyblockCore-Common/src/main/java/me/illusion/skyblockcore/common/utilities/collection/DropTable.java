package me.illusion.skyblockcore.common.utilities.collection;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A drop table is a collection of items with a weight, the higher the weight, the more likely it is to be chosen.
 *
 * @param <T> The type of the item
 */
public class DropTable<T> {

    private final Random random = ThreadLocalRandom.current();

    private final Map<T, Double> weights = new ConcurrentHashMap<>();
    private double totalWeight = 0;

    public void add(T type, double weight) {
        weights.put(type, weight);
        totalWeight += weight;
    }

    public void remove(T type) {
        totalWeight -= weights.remove(type);
    }

    public T get() {
        double value = random.nextDouble() * totalWeight;

        for (Map.Entry<T, Double> entry : weights.entrySet()) {
            value -= entry.getValue();

            if (value <= 0) {
                return entry.getKey();
            }
        }

        return null;
    }

    public Map<T, Double> getWeights() {
        return new ConcurrentHashMap<>(weights);
    }

    public double getTotalWeight() {
        return totalWeight;
    }


}

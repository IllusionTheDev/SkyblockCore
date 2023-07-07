package me.illusion.skyblockcore.common.utilities.collection;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DropTable<Type> {

    private final Random random = ThreadLocalRandom.current();

    private final Map<Type, Double> weights = new ConcurrentHashMap<>();
    private double totalWeight = 0;

    public void add(Type type, double weight) {
        weights.put(type, weight);
        totalWeight += weight;
    }

    public void remove(Type type) {
        totalWeight -= weights.remove(type);
    }

    public Type get() {
        double value = random.nextDouble() * totalWeight;

        for (Map.Entry<Type, Double> entry : weights.entrySet()) {
            value -= entry.getValue();

            if (value <= 0) {
                return entry.getKey();
            }
        }

        return null;
    }

    public Map<Type, Double> getWeights() {
        return new ConcurrentHashMap<>(weights);
    }

    public double getTotalWeight() {
        return totalWeight;
    }


}

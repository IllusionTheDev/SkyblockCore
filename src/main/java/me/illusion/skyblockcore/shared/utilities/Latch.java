package me.illusion.skyblockcore.shared.utilities;

import java.util.concurrent.CountDownLatch;

public class Latch extends CountDownLatch {

    public Latch() {
        super(1);
    }

    /**
     * Constructs a {@code CountDownLatch} initialized with the given count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     *              before threads can pass through {@link #await}
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public Latch(int count) {
        super(count);
    }
}

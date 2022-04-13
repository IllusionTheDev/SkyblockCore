package me.illusion.skyblockcore.shared.utilities;

import me.illusion.skyblockcore.shared.exceptions.UnsafeSyncOperationException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @Override
    public void await() throws InterruptedException {
        if (SoftwareDetectionUtil.isMainThread())
            throw new UnsafeSyncOperationException();

        super.await();
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (SoftwareDetectionUtil.isMainThread())
            throw new UnsafeSyncOperationException();

        return super.await(timeout, unit);
    }
}

package me.illusion.skyblockcore.shared.packet;

import me.illusion.skyblockcore.shared.packet.data.PacketWaitData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

public class PacketWaiter {

    private final PacketManager manager;
    private final Map<PacketWaitData<?>, CountDownLatch> data = new HashMap<>();
    private final Map<PacketWaitData<?>, Object> results = new HashMap<>();
    private final Set<String> registeredClasses = new HashSet<>();

    public PacketWaiter(PacketManager manager) {
        this.manager = manager;
    }

    public <T extends Packet> T await(Class<T> packetClass, Predicate<T> returnIf) {
        CountDownLatch latch = new CountDownLatch(1);
        PacketWaitData<?> waitData = new PacketWaitData<>(packetClass, returnIf);
        data.put(waitData, latch);

        String name = packetClass.getSimpleName();
        if (!registeredClasses.contains(name)) {
            manager.subscribe(packetClass, new PacketHandler<T>() {
                @Override
                public void onReceive(T packet) {
                    for (Map.Entry<PacketWaitData<?>, CountDownLatch> entry : data.entrySet()) {
                        PacketWaitData<?> data = entry.getKey();
                        CountDownLatch latch = entry.getValue();


                        if (!data.getClazz().equals(packetClass))
                            return;

                        Predicate<T> predicate = (Predicate<T>) data.getPredicate();

                        if (predicate.test(packet)) {
                            results.put(data, packet);
                            latch.countDown();
                        }

                    }
                }
            });

            registeredClasses.add(name);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return (T) results.remove(waitData);
    }
}

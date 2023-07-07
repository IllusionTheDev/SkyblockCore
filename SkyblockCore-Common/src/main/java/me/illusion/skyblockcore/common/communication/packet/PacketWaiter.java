package me.illusion.skyblockcore.common.communication.packet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import me.illusion.skyblockcore.common.communication.packet.data.PacketWaitData;

public class PacketWaiter {

    private final PacketManager manager;
    private final Map<PacketWaitData<?>, CompletableFuture<?>> futures = new HashMap<>();
    private final Set<String> registeredClasses = new HashSet<>();

    public PacketWaiter(PacketManager manager) {
        this.manager = manager;
    }

    public <T extends Packet> CompletableFuture<T> await(Class<T> packetClass, Predicate<T> returnIf) {
        CompletableFuture<T> future = new CompletableFuture<>();
        PacketWaitData<T> waitData = new PacketWaitData<>(packetClass, returnIf);

        futures.put(waitData, future);

        if (registeredClasses.contains(packetClass.getName())) {
            return future;
        }

        registeredClasses.add(packetClass.getName());

        manager.subscribe(packetClass, new PacketHandler<T>() {
            @Override
            public void onReceive(T packet) {
                for (Map.Entry<PacketWaitData<?>, CompletableFuture<?>> entry : futures.entrySet()) {
                    PacketWaitData<?> data = entry.getKey();
                    CompletableFuture<?> future = entry.getValue();

                    if (!data.getClazz().equals(packet.getClass())) {
                        continue;
                    }

                    PacketWaitData<T> castedData = (PacketWaitData<T>) data;

                    if (castedData.getPredicate().test(packet)) {
                        CompletableFuture<T> castedFuture = (CompletableFuture<T>) future;
                        castedFuture.complete(packet);
                    }
                }
            }
        });

        return future;
    }


}

package me.illusion.skyblockcore.shared.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public class PacketWaitData<T> {

    private final Class<T> clazz; // Class that determines what packet should be waited for
    private final Predicate<T> predicate; // Filter to determine wanted packets
}

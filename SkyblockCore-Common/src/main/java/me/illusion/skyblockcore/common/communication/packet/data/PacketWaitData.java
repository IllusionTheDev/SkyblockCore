package me.illusion.skyblockcore.common.communication.packet.data;

import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PacketWaitData<T> {

    private final Class<T> clazz;
    private final Predicate<T> predicate;
}

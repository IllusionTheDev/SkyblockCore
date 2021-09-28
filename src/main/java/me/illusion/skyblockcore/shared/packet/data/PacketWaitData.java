package me.illusion.skyblockcore.shared.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public class PacketWaitData<T> {

    private final Class<T> clazz;
    private final Predicate<T> predicate;
}

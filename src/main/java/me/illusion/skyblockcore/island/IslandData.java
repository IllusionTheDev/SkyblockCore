package me.illusion.skyblockcore.island;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.illusion.skyblockcore.island.generator.OreGenerator;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class IslandData implements Serializable {

    private final String serialized;
    private final UUID owner;
    private final List<OreGenerator> oreGenerators;
    @Setter
    private transient Island island;

    public void addGenerator(OreGenerator generator) {
        oreGenerators.add(generator);
    }

    public List<UUID> getUsers() {
        return Stream.of(serialized.split(" ")).map(UUID::fromString).collect(Collectors.toList());
    }
}

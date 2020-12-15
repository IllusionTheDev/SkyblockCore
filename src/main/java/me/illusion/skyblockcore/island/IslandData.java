package me.illusion.skyblockcore.island;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class IslandData {

    private List<UUID> users;
    private UUID owner;

}

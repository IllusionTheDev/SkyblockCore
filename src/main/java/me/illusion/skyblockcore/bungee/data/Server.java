package me.illusion.skyblockcore.bungee.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Server {

    private String identifier;
    private List<UUID> islands;
    private boolean available;
}

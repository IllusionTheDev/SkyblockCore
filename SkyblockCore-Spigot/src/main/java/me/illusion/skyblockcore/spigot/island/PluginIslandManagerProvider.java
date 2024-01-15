package me.illusion.skyblockcore.spigot.island;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import me.illusion.skyblockcore.common.config.section.ConfigurationSection;
import me.illusion.skyblockcore.server.island.SkyblockIslandManager;
import me.illusion.skyblockcore.server.island.provider.SkyblockIslandManagerProvider;
import org.bukkit.Bukkit;

public class PluginIslandManagerProvider implements SkyblockIslandManagerProvider {

    private final Function<ConfigurationSection, SkyblockIslandManager> function;
    private final List<String> dependencies;

    public PluginIslandManagerProvider(Function<ConfigurationSection, SkyblockIslandManager> function, String... dependencies) {
        this.function = function;
        this.dependencies = List.of(dependencies);
    }

    public static PluginIslandManagerProvider of(Supplier<SkyblockIslandManager> supplier, String... dependencies) {
        return new PluginIslandManagerProvider(section -> supplier.get(), dependencies);
    }

    public static PluginIslandManagerProvider of(Function<ConfigurationSection, SkyblockIslandManager> function, String... dependencies) {
        return new PluginIslandManagerProvider(function, dependencies);
    }

    @Override
    public SkyblockIslandManager provideIslandManager(ConfigurationSection section) {
        return function.apply(section);
    }

    @Override
    public boolean canProvide() {
        for (String dependency : dependencies) {
            if (!Bukkit.getPluginManager().isPluginEnabled(dependency)) {
                return false;
            }
        }

        return true;
    }
}

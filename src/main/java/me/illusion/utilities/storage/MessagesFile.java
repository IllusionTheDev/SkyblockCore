package me.illusion.utilities.storage;

import lombok.Getter;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Function;

public class MessagesFile extends YMLBase {

    @Getter
    private final String prefix;

    public MessagesFile(JavaPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "messages.yml"), true);

        prefix = StringEscapeUtils.unescapeJava(getConfiguration().getString("messages.prefix"));
    }

    public void sendMessage(CommandSender player, String name) {
        sendMessage(player, name, (s) -> s);
    }

    public void sendMessage(CommandSender player, String name, Function<String, String> action) {
        if (!getConfiguration().contains("messages." + name))
            return;

        if (getConfiguration().isList("messages." + name)) {
            for (String str : getConfiguration().getStringList("messages." + name)) {
                String msg = StringEscapeUtils.unescapeJava(str.replace("%prefix%", prefix));
                msg = action.apply(msg);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
            return;
        }

        String msg = StringEscapeUtils.unescapeJava(getMessage(name).replace("%prefix%", prefix));
        msg = action.apply(msg);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public String getMessage(String name) {
        return getConfiguration().getString("messages." + name);
    }
}


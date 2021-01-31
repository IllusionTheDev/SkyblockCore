package me.illusion.skyblockcore.bungee.utilities;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;
import java.util.function.Function;

public class MessagesFile extends YMLBase {

    @Getter
    private final String prefix;


    public MessagesFile(Plugin plugin) {
        super(plugin, "messages.yml");

        prefix = StringEscapeUtils.unescapeJava(getConfiguration().getString("messages.prefix"));
    }

    public void sendMessage(CommandSender player, String name) {
        sendMessage(player, name, (s) -> s);
    }

    public void sendMessage(CommandSender player, String name, Function<String, String> action) {
        if (!getConfiguration().contains("messages." + name))
            return;

        if (getConfiguration().get("messages." + name) instanceof List) {
            for (String str : getConfiguration().getStringList("messages." + name)) {
                String msg = StringEscapeUtils.unescapeJava(str.replace("%prefix%", prefix));
                msg = action.apply(msg);
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
            }
            return;
        }

        String msg = StringEscapeUtils.unescapeJava(getMessage(name).replace("%prefix%", prefix));
        msg = action.apply(msg);

        player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
    }

    public String getMessage(String name) {
        return getConfiguration().getString("messages." + name);
    }
}


package me.illusion.skyblockcore.common.command.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import me.illusion.skyblockcore.common.command.audience.SkyblockAudience;
import me.illusion.skyblockcore.common.command.context.CommandArgument;
import me.illusion.skyblockcore.common.command.context.CommandContext;
import me.illusion.skyblockcore.common.command.context.impl.MutatingCommandContext;
import me.illusion.skyblockcore.common.command.data.SkyblockCommand;
import me.illusion.skyblockcore.common.command.node.ArgumentCommandNode;
import me.illusion.skyblockcore.common.command.node.CommandNode;

public class CommandTree {

    private final Map<String, CommandNode> roots = new ConcurrentHashMap<>();

    public CommandNode getRoot(String name) {
        return roots.get(name);
    }

    public TargetResult getTargetNode(String fullInput) {
        String[] split = fullInput.split(" ");
        CommandNode node = getRoot(split[0]);

        if (node == null) {
            return null;
        }

        CommandNode target = node;
        MutatingCommandContext context = new MutatingCommandContext(fullInput);

        for (int index = 1; index < split.length; index++) {
            String word = split[index];
            List<? extends CommandNode> children = target.getChildren();

            if (children == null) {
                return null;
            }

            CommandNode child = null;

            for (CommandNode nodeChild : children) {
                if (nodeChild.getName().equalsIgnoreCase(word)) {
                    child = nodeChild;
                    break;
                }

                CommandArgument argument = nodeChild.getArgument();
                context.addArgument(word, argument);
            }

            if (child == null) {
                return null;
            }

            target = child;
        }

        return new TargetResult(target, context);
    }

    public List<String> tabComplete(SkyblockAudience audience, String fullInput) {
        String[] split = fullInput.split(" ");
        CommandNode node = getRoot(split[0]);

        if (node == null) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();
        CommandNode target = node;

        MutatingCommandContext context = new MutatingCommandContext(fullInput);

        for (int index = 1; index < split.length; index++) {
            String s = split[index];
            List<? extends CommandNode> children = target.getChildren();

            boolean isLast = index == split.length - 1;

            if (children == null) {
                return Collections.emptyList();
            }

            CommandNode targetChild = null;

            for (CommandNode child : children) {
                if (child.getName().equalsIgnoreCase(s) || (!isLast && context.addArgument(s, child.getArgument()))) {
                    targetChild = child;
                    break;
                }

                if (!isLast) {
                    continue;
                }

                CommandArgument argument = child.getArgument();

                if (!audience.hasPermission(child.getPermission())) {
                    continue;
                }

                List<String> tabComplete = argument.tabComplete(context);

                if (tabComplete == null) {
                    continue;
                }

                completions.addAll(tabComplete);
            }

            if (targetChild == null) {
                return Collections.emptyList();
            }

            target = targetChild;
        }

        return completions;
    }


    public void registerNode(CommandNode node) {
        String name = node.getName();

        if (roots.containsKey(name)) {
            return;
        }

        roots.put(name, node);
    }

    public void registerCommand(SkyblockCommand<?> command) {
        List<CommandNode> nodes = new LinkedList<>();

        for (CommandArgument argument : command.getArguments()) {
            CommandNode node = new ArgumentCommandNode(command, argument);
            nodes.add(node);
        }

        // Do children n stuff

        CommandNode root = nodes.get(0);

        registerNode(root);
        root = getRoot(root.getName()); // Get the node from the map. Registering might've ignored it as the node was previously registered

        for (int index = 1; index < nodes.size(); index++) {
            CommandNode node = nodes.get(index);
            root.registerChildren(node);
            root = node;
        }
    }

    @Getter
    public static class TargetResult {

        private final CommandNode node;
        private final CommandContext context;

        public TargetResult(CommandNode node, CommandContext context) {
            this.node = node;
            this.context = context;
        }

    }

}

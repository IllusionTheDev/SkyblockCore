package me.illusion.skyblockcore.spigot.command.comparison;

import me.illusion.skyblockcore.spigot.command.SkyblockCommand;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComparisonResultFull {
    private final Map<String, SkyblockCommand> commands;
    private final LinkedList<Integer> wildCards;

    public ComparisonResultFull(Map<String, SkyblockCommand> commands) {
        this.commands = commands;
        this.wildCards = new LinkedList<>();
    }

    public SkyblockCommand match(String input) {
        String[] inputs = input.split("\\.");

        //in case input is empty
        if (inputs.length == 0)
            return null;

        //case alias.arg
        //first take out the alias
        for (SkyblockCommand command : commands.values()) {
            if (inputs.length == 1) {
                if (command.getIdentifier().equalsIgnoreCase(input) || searchAliases(input, command)) {
                    return command;
                }
            }
            //case for alias.command
            //if it's an alias of another command
            else {
                if (findAliasingCommand(inputs[0], command)) {
                    if (searchArgs(inputs, command)) {
                        return command;
                    }
                }

                //look for identifiers
                if (searchIds(inputs, command)) {

                    if (searchArgs(inputs, command)) {
                        return command;

                    }
                }
            }
        }
        return null;
    }

    private boolean searchArgs(String[] inputs, SkyblockCommand command) {
        String[] args = command.getIdentifier().split("\\.");

        //System.out.println("Args: " + Arrays.toString(args));
        //System.out.println("Inputs: " + Arrays.toString(inputs));
        if (inputs.length > args.length) return false;
        //inputs has to be smaller than the command for autocomplete to work
        for (int i = 1; i < inputs.length; i++) {
            if (args[i].equals("*")) {
                wildCards.add(i);
                continue;
            }
            if (!args[i].equals(inputs[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean searchAliases(String input, SkyblockCommand command) {
        String[] aliases = command.getAliases();
        for (String alias : aliases) {
            if (alias.equals(input)) {
                return true;
            }
        }
        return false;
    }

    private boolean searchIds(String[] inputs, SkyblockCommand command) {
        String[] ids = command.getIdentifier().split("\\.");
        String id = ids[0];
        return id.equals(inputs[0]);
    }

    private boolean findAliasingCommand(String input, SkyblockCommand command) {
        String id = command.getIdentifier().split("\\.")[0];

        SkyblockCommand it = commands.get(id);

        if (it == null)
            return false;

        if (it.getIdentifier().equals(id))
            for (String alias : it.getAliases()) {
                if (alias.equals(input)) {
                    return true;
                }
            }
        return false;

    }

    public List<Integer> getWildCards() {
        return Collections.unmodifiableList(wildCards);
    }


}
package me.illusion.skyblockcore.spigot.command.comparison;

import me.illusion.skyblockcore.spigot.command.SkyblockCommand;

import java.util.*;

public class ComparisonResult {
    private final Map<String, SkyblockCommand> commands;
    private final LinkedList<Integer> wildCards;
    private SkyblockCommand storedCommand;

    public ComparisonResult(Map<String, SkyblockCommand> commands) {
        this.commands = commands;
        this.wildCards = new LinkedList<>();
    }

    public List<String> match(String input) {
        SortedSet<String> result = new TreeSet<>(new SmallestStringComparator());
        String[] inputs = input.split("\\.");

        //in case input is empty
        if (inputs.length == 0)
            return Collections.emptyList();


        //case alias.arg
        //first take out the alias
        for (SkyblockCommand command : commands.values()) {

            if (findAliasingCommand(inputs[0], command) && searchArgs(inputs, command)) {
                result.add(command.getIdentifier());
                continue;
            }

            //look for identifiers
            if (searchIds(inputs, command) && searchArgs(inputs, command))
                result.add(command.getIdentifier());

        }
        return new ArrayList<>(result);
    }

    private boolean searchArgs(String[] inputs, SkyblockCommand command) {
        String[] args = command.getIdentifier().split("\\.");

        if (inputs.length > args.length) return false;
        //inputs has to be smaller than the command for autocomplete to work
        for (int i = 1; i < inputs.length; i++) {
            if (args[i].equals("*")) {
                wildCards.add(i);
                return true;
            }
            if (!args[i].startsWith(inputs[i]))
                return false;

        }

        return true;
    }

    /*
    private boolean searchAliases(String input, SkyblockCommand command)
    {
        String[] aliases = command.getAliases();
        for (String alias : aliases)
        {
            if (alias.startsWith(input))
            {
                return true;
            }
        }
        return false;
    }

     */

    private boolean searchIds(String[] inputs, SkyblockCommand command) {
        String[] ids = command.getIdentifier().split("\\.");
        String id = ids[0];
        if (inputs.length == 1 && ids.length == 1)
            return id.startsWith(inputs[0]);
        return id.equals(inputs[0]);
    }


    public List<Integer> getWildCards() {
        return Collections.unmodifiableList(wildCards);
    }

    private boolean findAliasingCommand(String input, SkyblockCommand command) {
        String id = command.getIdentifier().split("\\.")[0];

        SkyblockCommand it = commands.get(id);
        if (it.getIdentifier().equals(id))
            for (String alias : it.getAliases())
                if (alias.equals(input))
                    return true;


        return false;

    }
}


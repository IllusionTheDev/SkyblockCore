package me.illusion.skyblockcore.spigot.command.comparison;

import me.illusion.skyblockcore.spigot.command.SkyblockCommand;

import java.util.*;
import java.util.stream.Collectors;

public class ComparisonResult {
    private final Map<String, SkyblockCommand> commands;
    private final LinkedList<Integer> wildCards;

    public ComparisonResult(Map<String, SkyblockCommand> commands) {
        this.commands = commands;
        this.wildCards = new LinkedList<>();
    }

    //get the commands that match the input
    String[] match(String input) {
        SortedSet<String> result = new TreeSet<>(new SmallestStringComparator());
        String[] inputs = input.split("\\.");
        boolean isNextCommand = input.charAt(input.length() - 1) == '.';

        //in case input is empty
        if (inputs.length == 0)
            return new String[0];


        //case alias.arg
        //first take out the alias
        for (SkyblockCommand command : commands.values()) {
            if (findAliasingCommand(inputs[0], command)) {
                if (searchArgs(inputs, command, isNextCommand)) {
                    result.add(command.getIdentifier());
                    continue;
                }
            }

            //look for identifiers
            if (searchIds(inputs, command)) {
                if (searchArgs(inputs, command, isNextCommand)) {
                    result.add(command.getIdentifier());
                }
            }
        }
        return result.toArray(new String[0]);
    }

    private boolean searchArgs(String[] inputs, SkyblockCommand command, boolean isNextCommand) {
        String[] args = command.getIdentifier().split("\\.");

        int next = isNextCommand ? 1 : 0;

        if (inputs.length > args.length) return false;
        //inputs has to be smaller than the command for autocomplete to work
        for (int i = 1; i < inputs.length - 1 + next; i++) {
            if (args[i].equals("*")) {
                wildCards.add(i);
                continue;
            }
            if (!args[i].equals(inputs[i])) {
                return false;
            }
        }
        if (!isNextCommand)
            return args[inputs.length - 1].startsWith(inputs[inputs.length - 1]) || args[inputs.length - 1].equals("*");

        return true;
    }

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
            for (String alias : it.getAliases()) {
                if (alias.equals(input)) {
                    return true;
                }
            }
        return false;

    }

    public List<String> tabComplete(String identifier) {

        String[] results = match(identifier);

        String[] inputs = identifier.split("\\.");
        boolean isNextCommand = identifier.charAt(identifier.length() - 1) == '.';
        int nextCommand = isNextCommand ? 1 : 0;

        // Extra processing
        List<String> toReturn = new ArrayList<>();
        for (String str : results) {
            if (commands.get(str) == null) {
                System.out.println("Command:" + str + " not found");
                continue;
            }

            if (str.equals(identifier)) {
                toReturn.add("");
                continue;
            }

            String[] inputs2 = str.split("\\.");

            //for cases where identifier is "is.invite." but str is "is.invite"
            //or for cases where identifier is "is.i." but partial match still pulls through with "is.invite"
            if (isNextCommand &&
                    (inputs.length == inputs2.length))
                continue;


            //if it is an alias, stop from putting itself
            if (!isNextCommand &&
                    commands.containsKey(inputs2[inputs.length - 1]) &&
                    findAliasingCommand(inputs[inputs.length - 1], commands.get(inputs2[inputs.length - 1]))
            ) {
                toReturn.add("");
                continue;
            }

            //if the one in question is a wildcard
            if (inputs2[inputs.length - 1 + nextCommand].equals("*")) {
                int wildcardN = 0;

                for (int i = 1; i < inputs.length + nextCommand; i++) {
                    if (inputs2[i].equals("*"))
                        wildcardN++;
                }

                //if the command has that wildcard
                if (commands.get(str).tabCompleteWildcards().containsKey(wildcardN)) {
                    List<String> wildcards = commands.get(str).tabCompleteWildcards().get(wildcardN);
                    if (!isNextCommand)
                        wildcards = wildcards.stream().filter(card -> card.startsWith(inputs[inputs.length - 1])).collect(Collectors.toList());

                    toReturn.addAll(wildcards);
                }
            }

            //get the command you're looking for
            else {
                //if the identifier ends with ".", put in next commands
                if (isNextCommand) {
                    toReturn.add(inputs2[inputs.length]);
                    continue;
                }

                //in case the identifier is "island" and comparing to "island.go", "island" shouldn't be in the toReturn
                if (!inputs2[inputs.length - 1].equals(inputs[inputs.length - 1])) {
                    toReturn.add(inputs2[inputs.length - 1]);
                }
            }
        }
        return toReturn.stream().distinct().collect(Collectors.toList());
    }


}


package me.illusion.skyblockcore.spigot.utilities.command.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.illusion.skyblockcore.common.utilities.collection.ListUtils;
import me.illusion.skyblockcore.spigot.utilities.command.language.data.ObjectiveMetadata;
import me.illusion.skyblockcore.spigot.utilities.command.language.parser.ArgumentType;
import me.illusion.skyblockcore.spigot.utilities.command.language.parser.argument.Argument;
import me.illusion.skyblockcore.spigot.utilities.command.language.parser.argument.ParameterArgument;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.Parameter;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.ParameterType;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.ParameterTypes;
import me.illusion.skyblockcore.spigot.utilities.command.language.type.impl.FilteredParameterType;
import org.bukkit.command.CommandSender;

public abstract class AbstractObjectiveModel<T extends CompiledObjective> {

    private final Map<String, Parameter<?>> argumentMap = new HashMap<>();
    private final List<Argument<?>> arguments = new ArrayList<>();

    private final String syntax;

    public AbstractObjectiveModel(String syntax) {
        this.syntax = syntax;

        parseDefaultParameters();
        parseArguments();
    }

    public void registerParameter(Parameter<?> parameter) {
        argumentMap.put(parameter.getName(), parameter); // register the parameter, this will override any existing parameters with the same name

        // optional checks
        arguments.clear();
        parseArguments();
    }

    public abstract T compile(ObjectiveMetadata metadata);

    public boolean canExecute(CommandSender sender) {
        return true;
    }

    public String getPermission() {
        return null;
    }

    /**
     * Parses the input line and returns true if the line is valid
     *
     * @param fullLine The full line to parse
     * @return True if the line is valid
     */
    public T parse(String fullLine) {
        // Syntax looks like: mycommand [tag] <parameter> <parameter> <parameter>
        // example: walk [blocking] <x> <y> <z>
        // A valid input is: walk blocking 123 0 123
        // A valid input is: walk 123 0 123

        // Anything in [] is a tag
        // Anything in <> is a parameter
        // Anything in {} is a variable
        // Anything in () will be parsed as a list of the type in (), so (x) will be parsed as a list of x's

        // Split the line into an array of strings

        // Expected outcome: syntax = "mycommand [blocking] <x> <y> <z> (text)"
        // fullLine = "mycommand blocking 123 0 123 hello world"
        // parameterMap = {x: Parameter type INTEGER, y: Parameter type INTEGER, z: Parameter type INTEGER, text: Parameter type STRING}

        // parameters = {x: 123, y: 0, z: 123, text: ["hello", "world"]}
        // tags = ["blocking"]  

        List<Argument<?>> arguments = new ArrayList<>();

        if (this.arguments.isEmpty() && fullLine.equalsIgnoreCase(syntax)) {
            return compile(new ObjectiveMetadata(this.arguments, arguments, syntax));
        }

        String[] split = fullLine.split(" ");
        String[] syntaxSplit = syntax.split(" ");

        String firstWord = split[0];
        String firstSyntaxWord = syntaxSplit[0];

        if (!firstWord.equalsIgnoreCase(firstSyntaxWord)) {
            return null; // The first word is not the same, so this is not the correct command
        }

        int syntaxIndex = 0;

        for (int index = 0; index < split.length; index++) {
            String word = split[index];
            String syntaxWord = syntaxSplit[syntaxIndex];

            String unformattedSyntaxWord = removeFormat(syntaxWord);
            if (matchesTagFormat(syntaxWord)) {
                if (word.equalsIgnoreCase(unformattedSyntaxWord)) {
                    arguments.add(new Argument<>(word, ArgumentType.TAG, true, word));
                } else {
                    index--;
                }

                syntaxIndex++;
            } else if (matchesParameterFormat(syntaxWord)) {
                Parameter<?> parameterType = argumentMap.get(unformattedSyntaxWord);
                syntaxIndex++;
                if (parameterType == null) {
                    return null;
                }

                ParameterType<?> type = parameterType.getType();

                if (!type.isType(word)) {
                    return null;
                }

                arguments.add(new ParameterArgument<>(unformattedSyntaxWord, type, parameterType.isOptional(), type.parse(word)));
            } else if (matchesListFormat(syntaxWord)) {
                Parameter<?> parameterType = argumentMap.get(unformattedSyntaxWord);
                syntaxIndex++;
                if (parameterType == null) {
                    return null;
                }

                ParameterType<?> type = parameterType.getType();

                List<Object> listValues = new ArrayList<>();
                while (index < split.length) {
                    String listWord = split[index];

                    if (!type.isType(listWord)) {
                        break;
                    }

                    listValues.add(type.parse(listWord));
                    index++;
                }

                arguments.add(new ParameterArgument<>(unformattedSyntaxWord, type, parameterType.isOptional(), listValues));
            } else {
                if (!word.equalsIgnoreCase(syntaxWord)) {
                    return null;
                }

                arguments.add(new Argument<>(syntaxWord, ArgumentType.STRING, false, syntaxWord));
                syntaxIndex++;
            }
        }

        int requiredParameters = 0;

        for (Argument<?> argument : this.arguments) {
            if (argument.getArgumentType() == ArgumentType.TAG) {
                continue;
            }

            boolean found = false;
            for (Argument<?> argument1 : arguments) {
                if (argument1.getName().equalsIgnoreCase(argument.getName())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                if (argument.isOptional()) {
                    // Insert the optional argument, with the default value
                    ListUtils.insertInList(arguments, this.arguments.indexOf(argument), argument);
                    continue;
                }

                requiredParameters++;
            }
        }

        if (requiredParameters > 0) {
            return null;
        }

        return compile(new ObjectiveMetadata(this.arguments, arguments, syntax));
    }

    public List<String> getSuggestions(String fullLine) {
        // The idea is to basically parse it up to the last word, and then get the argument type of the last word (keep in mind the last word is incomplete)
        // Then, we can get the suggestions for that argument type, and return them

        // A valid input is: walk block
        // The last word is incomplete, so we want to get the suggestions for the argument type of the last word
        // In this case, the last word is block, and the argument type is a tag "blocking", given that the syntax is "walk [blocking]"
        // Knowing that "blocking" starts with "block", we can return that as a suggestion

        if (arguments.isEmpty()) {
            if (fullLine.equalsIgnoreCase(syntax)) {
                return Collections.emptyList();
            }

            if (fullLine.startsWith(syntax)) {
                return Collections.singletonList(fullLine);
            }

            return null;
        }

        String[] split = fullLine.split(" ");
        String lastWord = split[0];

        if (fullLine.endsWith(" ")) {
            // we need to add this space to the array
            split = Arrays.copyOf(split, split.length + 1);
            split[split.length - 1] = "";
        }

        List<String> suggestions = new ArrayList<>();

        List<Argument<?>> arguments = new ArrayList<>(this.arguments);

        int argumentIndex = 0;
        Argument<?> lastArgument = null;

        for (String word : split) {
            if (argumentIndex >= arguments.size()) {
                // we are past the last argument, so we can't suggest anything
                break;
            }

            Argument<?> argument = arguments.get(argumentIndex++);

            lastWord = word;
            lastArgument = argument;

            ArgumentType argumentType = argument.getArgumentType();

            // are we past this stage?
            if (argumentType.isLiteral() && argument.getName().equalsIgnoreCase(word)) {
                // we are past this stage, so we can skip it
                continue;
            }

            if (argumentType.isLiteral()) {
                if (!argument.isOptional()) {
                    // we can't skip this argument, so we can't suggest anything
                    break;
                }

                continue;
            }

            // What is not literal is a parameter argument
            ParameterArgument<?> parameterArgument = (ParameterArgument<?>) argument;

            if (parameterArgument.getType().isType(word)) {
                // we are past this stage, so we can skip it
                continue;
            }

            if (!parameterArgument.isOptional()) {
                // we can't skip this argument, so we can't suggest anything
                break;
            }

            addCompletions(suggestions, word, parameterArgument);
        }

        if (lastArgument == null) {
            // we are past the last argument, so we can't suggest anything
            return suggestions;
        }

        if (lastArgument.getArgumentType().isLiteral()) {
            if (lastArgument.getName().equalsIgnoreCase(lastWord)) {
                // we are past this stage, so we can't suggest anything
                return suggestions;
            }

            if (lastArgument.getName().startsWith(lastWord)) {
                suggestions.add(lastArgument.getName());
            }

            // System.out.println("Reached the end of literal argument, so we can't suggest anything");
            return suggestions;
        }

        ParameterArgument<?> parameterArgument = (ParameterArgument<?>) lastArgument;
        addCompletions(suggestions, lastWord, parameterArgument);

        // System.out.println("Reached the end of parameter argument, so we can't suggest anything");
        return suggestions;

    }

    private void parseArguments() {
        String[] split = syntax.split(" ");

        for (String word : split) {
            if (matchesTagFormat(word)) {
                String tag = removeFormat(word);
                arguments.add(new Argument<>(tag, ArgumentType.TAG, true, ""));
            } else if (matchesParameterFormat(word)) {
                String parameter = removeFormat(word);

                Argument<?> argument = new Argument<>(parameter, ArgumentType.PARAMETER, false, "");

                if (this.argumentMap.containsKey(parameter)) {
                    Parameter<?> parameterType = this.argumentMap.get(parameter);
                    argument = new ParameterArgument<>(parameter, parameterType.getType(), parameterType.isOptional(),
                        parameterType.getType().getDefaultValue());
                }

                arguments.add(argument);
            } else if (matchesListFormat(word)) {
                String list = removeFormat(word);

                Argument<?> argument = new Argument<>(list, ArgumentType.LIST, false, new ArrayList<>());

                if (this.argumentMap.containsKey(list)) {
                    Parameter<?> parameterType = this.argumentMap.get(list);
                    argument = new ParameterArgument<>(list, parameterType.getType(), parameterType.isOptional(), new ArrayList<>());
                }

                arguments.add(argument);
            } else {
                arguments.add(new Argument<>(word, ArgumentType.STRING, false, word));
            }
        }
    }

    private void addCompletions(List<String> suggestions, String lastWord, ParameterArgument<?> parameterArgument) {
        ParameterType<?> type = parameterArgument.getType();

        if (type instanceof FilteredParameterType<?> filtered) {
            List<?> filteredValues = filtered.getAllValues();

            for (Object filteredValue : filteredValues) {
                String filteredString = filteredValue.toString();

                if (filteredString.startsWith(lastWord)) {
                    suggestions.add(filteredString);
                }
            }
        }
    }

    private void parseDefaultParameters() {
        String[] split = syntax.split(" ");

        for (String word : split) {
            if (matchesParameterFormat(word) || matchesListFormat(word)) {
                String parameter = removeFormat(word);

                registerParameter(new Parameter<>(parameter, ParameterTypes.STRING, true));
            }
        }
    }

    private boolean matchesListFormat(String input) {
        return input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')';
    }

    private boolean matchesTagFormat(String input) {
        return input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']';
    }

    private boolean matchesParameterFormat(String input) {
        return input.charAt(0) == '<' && input.charAt(input.length() - 1) == '>';
    }

    public T createEmptyObjective() {
        return compile(new ObjectiveMetadata(arguments, arguments, syntax));
    }

    private String removeFormat(String input) {
        if (matchesParameterFormat(input) || matchesTagFormat(input) || matchesListFormat(input)) {
            return input.substring(1, input.length() - 1);
        }

        return input;
    }

    public String getSyntax() {
        return syntax;
    }

}

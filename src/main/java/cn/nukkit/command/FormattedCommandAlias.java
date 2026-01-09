package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import io.netty.util.internal.EmptyArrays;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command alias that formats and executes one or more commands using argument substitution.
 * <p>
 * This class allows the creation of command aliases that can expand to multiple commands, with arguments
 * dynamically substituted into the format strings. It supports required and optional arguments, argument ranges,
 * and error handling for missing or invalid arguments.
 * <p>
 * Features:
 * <ul>
 *   <li>Supports multiple format strings for multi-command aliases.</li>
 *   <li>Substitutes arguments using tokens like $1, $$2, $3-, etc.</li>
 *   <li>Handles required arguments (double $), argument ranges (with -), and escaped tokens (\$).</li>
 *   <li>Provides error feedback for missing or invalid arguments.</li>
 *   <li>Executes all expanded commands and returns true if any succeed.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate with an alias and one or more format strings.</li>
 *   <li>Use argument tokens in format strings to substitute user input.</li>
 *   <li>Call {@link #execute(CommandSender, String, String[])} to process and execute the alias.</li>
 * </ul>
 * <p>
 * Example format tokens:
 * <ul>
 *   <li><b>$1</b>: Substitute the first argument (optional).</li>
 *   <li><b>$$2</b>: Substitute the second argument (required).</li>
 *   <li><b>$3-</b>: Substitute all arguments from the third onward.</li>
 *   <li><b>\$1</b>: Insert a literal $1 (escaped).</li>
 * </ul>
 * <p>
 * Error handling:
 * <ul>
 *   <li>Throws IllegalArgumentException for invalid tokens or missing required arguments.</li>
 *   <li>Sends error messages to the sender for argument issues or execution exceptions.</li>
 * </ul>
 *
 * @author MagicDroidX (Nukkit Project)
 * @see Command
 */
@Slf4j
public class FormattedCommandAlias extends Command {
    /**
     * The format strings used to build the expanded commands.
     */
    private final String[] formatStrings;

    /**
     * Constructs a FormattedCommandAlias with an alias and array of format strings.
     *
     * @param alias the command alias name
     * @param formatStrings the array of format strings
     */
    public FormattedCommandAlias(String alias, String[] formatStrings) {
        super(alias);
        this.formatStrings = formatStrings;
    }

    /**
     * Constructs a FormattedCommandAlias with an alias and a list of format strings.
     *
     * @param alias the command alias name
     * @param formatStrings the list of format strings
     */
    public FormattedCommandAlias(String alias, List<String> formatStrings) {
        super(alias);
        this.formatStrings = formatStrings.toArray(EmptyArrays.EMPTY_STRINGS);
    }

    /**
     * Executes the formatted command alias by expanding all format strings and executing the resulting commands.
     * <p>
     * Arguments are substituted into the format strings using tokens. Errors in argument substitution or command
     * execution are reported to the sender.
     *
     * @param sender the command sender
     * @param commandLabel the command label
     * @param args the command arguments
     * @return true if any command executed successfully, false otherwise
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean result = false;
        ArrayList<String> commands = new ArrayList<>();
        for (String formatString : formatStrings) {
            try {
                commands.add(buildCommand(formatString, args));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(TextFormat.RED + e.getMessage());
                return false;
            } catch (Exception e) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.exception"));
                log.warn("An error has occurred while executing the formatted command alias {} by the sender {}", commandLabel, sender.getName(), e);
                return false;
            }
        }

        for (String command : commands) {
            result |= Server.getInstance().executeCommand(sender, command) > 0;
        }

        return result;
    }

    /**
     * Builds a command string by substituting arguments into the format string using tokens.
     * <p>
     * Supports required arguments ($$), argument ranges (-), and escaped tokens (\$).
     * Throws IllegalArgumentException for invalid tokens or missing required arguments.
     *
     * @param formatString the format string containing tokens
     * @param args the command arguments
     * @return the expanded command string
     */
    private String buildCommand(String formatString, String[] args) {
        int index = formatString.indexOf("$");
        while (index != -1) {
            int start = index;

            if (index > 0 && formatString.charAt(start - 1) == '\\') {
                formatString = formatString.substring(0, start - 1) + formatString.substring(start);
                index = formatString.indexOf("$", index);
                continue;
            }

            boolean required = false;
            if (formatString.charAt(index + 1) == '$') {
                required = true;
                // Move index past the second $
                index++;
            }

            // Move index past the $
            index++;
            int argStart = index;
            while (index < formatString.length() && inRange(((int) formatString.charAt(index)) - 48, 0, 9)) {
                // Move index past current digit
                index++;
            }

            // No numbers found
            if (argStart == index) {
                throw new IllegalArgumentException("Invalid replacement token");
            }

            int position = Integer.parseInt(formatString.substring(argStart, index));

            // Arguments are not 0 indexed
            if (position == 0) {
                throw new IllegalArgumentException("Invalid replacement token");
            }

            // Convert position to 0 index
            position--;

            boolean rest = false;
            if (index < formatString.length() && formatString.charAt(index) == '-') {
                rest = true;
                // Move index past the -
                index++;
            }

            int end = index;

            if (required && position >= args.length) {
                throw new IllegalArgumentException("Missing required argument " + (position + 1));
            }

            StringBuilder replacement = new StringBuilder();
            if (rest && position < args.length) {
                for (int i = position; i < args.length; i++) {
                    if (i != position) {
                        replacement.append(' ');
                    }
                    replacement.append(args[i]);
                }
            } else if (position < args.length) {
                replacement.append(args[position]);
            }

            formatString = formatString.substring(0, start) + replacement.toString() + formatString.substring(end);
            // Move index past the replaced data so we don't process it again
            index = start + replacement.length();

            // Move to the next replacement token
            index = formatString.indexOf("$", index);
        }

        return formatString;
    }

    /**
     * Checks if an integer is within a specified range (inclusive).
     *
     * @param i the value to check
     * @param j the lower bound
     * @param k the upper bound
     * @return true if i is between j and k, false otherwise
     */
    private static boolean inRange(int i, int j, int k) {
        return i >= j && i <= k;
    }

}

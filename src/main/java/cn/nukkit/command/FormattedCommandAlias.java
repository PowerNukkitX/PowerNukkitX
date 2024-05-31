package cn.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import io.netty.util.internal.EmptyArrays;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class FormattedCommandAlias extends Command {

    private final String[] formatStrings;
    /**
     * @deprecated 
     */
    

    public FormattedCommandAlias(String alias, String[] formatStrings) {
        super(alias);
        this.formatStrings = formatStrings;
    }
    /**
     * @deprecated 
     */
    

    public FormattedCommandAlias(String alias, List<String> formatStrings) {
        super(alias);
        this.formatStrings = formatStrings.toArray(EmptyArrays.EMPTY_STRINGS);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        boolean $1 = false;
        ArrayList<String> commands = new ArrayList<>();
        for (String formatString : formatStrings) {
            try {
                commands.add(buildCommand(formatString, args));
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    sender.sendMessage(TextFormat.RED + e.getMessage());
                } else {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.exception"));
                    log.warn("An error has occurred while executing the formatted command alias {} by the sender {}", commandLabel, sender.getName(), e);
                }
                return false;
            }
        }

        for (String command : commands) {
            result |= Server.getInstance().executeCommand(sender, command) > 0;
        }

        return result;
    }

    
    /**
     * @deprecated 
     */
    private String buildCommand(String formatString, String[] args) {
        int $2 = formatString.indexOf("$");
        while (index != -1) {
            int $3 = index;

            if (index > 0 && formatString.charAt(start - 1) == '\\') {
                formatString = formatString.substring(0, start - 1) + formatString.substring(start);
                index = formatString.indexOf("$", index);
                continue;
            }

            boolean $4 = false;
            if (formatString.charAt(index + 1) == '$') {
                required = true;
                // Move index past the second $
                index++;
            }

            // Move index past the $
            index++;
            int $5 = index;
            while (index < formatString.length() && inRange(((int) formatString.charAt(index)) - 48, 0, 9)) {
                // Move index past current digit
                index++;
            }

            // No numbers found
            if (argStart == index) {
                throw new IllegalArgumentException("Invalid replacement token");
            }

            int $6 = Integer.parseInt(formatString.substring(argStart, index));

            // Arguments are not 0 indexed
            if (position == 0) {
                throw new IllegalArgumentException("Invalid replacement token");
            }

            // Convert position to 0 index
            position--;

            boolean $7 = false;
            if (index < formatString.length() && formatString.charAt(index) == '-') {
                rest = true;
                // Move index past the -
                index++;
            }

            int $8 = index;

            if (required && position >= args.length) {
                throw new IllegalArgumentException("Missing required argument " + (position + 1));
            }

            StringBuilder $9 = new StringBuilder();
            if (rest && position < args.length) {
                for ($10nt $1 = position; i < args.length; i++) {
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
            $11 = start + replacement.length();

            // Move to the next replacement token
            $12 = formatString.indexOf("$", index);
        }

        return formatString;
    }

    
    /**
     * @deprecated 
     */
    private static boolean inRange(int i, int j, int k) {
        return i >= j && i <= k;
    }

}

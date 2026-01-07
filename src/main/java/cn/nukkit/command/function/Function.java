package cn.nukkit.command.function;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Represents a function file containing a sequence of commands to be executed in PowerNukkitX.
 * <p>
 * This class loads and stores a list of commands from a file, allowing batch execution as a single function.
 * It supports parsing, filtering, and dispatching commands to a {@link CommandSender}.
 * <p>
 * Features:
 * <ul>
 *   <li>Loads commands from a file, ignoring blank lines and comments (lines starting with '#').</li>
 *   <li>Provides a static factory method {@link #fromPath(Path)} for instantiation.</li>
 *   <li>Stores the full file path and the parsed list of commands.</li>
 *   <li>Executes all commands in order via {@link #dispatch(CommandSender)}.</li>
 *   <li>Returns success status based on the execution of all commands.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Use {@link #fromPath(Path)} to create a Function instance from a file path.</li>
 *   <li>Call {@link #dispatch(CommandSender)} to execute all commands as the given sender.</li>
 *   <li>Access the list of commands or the file path via getters.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * Function function = Function.fromPath(Paths.get("functions/myfunc.mcfunction"));
 * boolean success = function.dispatch(player);
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @since PowerNukkitX 2.0.0
 */
@Getter
public class Function {

    /**
     * The full path to the function file.
     */
    private final Path fullPath;
    /**
     * The list of commands loaded from the function file.
     */
    private List<String> commands;

    /**
     * Loads a function from the specified file path, parsing and filtering commands.
     *
     * @param fullPath the path to the function file
     */
    private Function(Path fullPath) {
        this.fullPath = fullPath;
        try {
            commands = Files.readAllLines(fullPath);
            // Remove blank lines and comments (anything after '#')
            commands = commands.stream().filter(s -> !s.isBlank()).map(s -> s.split("#")[0]).filter(s -> !s.isEmpty()).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Function instance from the given file path.
     *
     * @param path the path to the function file
     * @return a new Function instance
     */
    public static Function fromPath(Path path) {
        return new Function(path);
    }

    /**
     * Executes all commands in the function as the specified sender.
     * <p>
     * Returns true if all commands execute successfully, false if any command fails.
     *
     * @param sender the command sender
     * @return true if all commands succeed, false otherwise
     */
    public boolean dispatch(CommandSender sender) {
        boolean success = true;
        for (String command : commands) {
            if (Server.getInstance().executeCommand(sender, command) <= 0)
                success = false;
        }
        return success;
    }
}

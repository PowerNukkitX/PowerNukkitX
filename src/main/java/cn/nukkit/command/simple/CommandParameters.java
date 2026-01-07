package cn.nukkit.command.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying multiple parameter sets for a command handler method in PowerNukkitX.
 * <p>
 * This annotation is used to declare one or more {@link Parameters} sets for a command method, enabling advanced
 * command argument parsing, tab completion, and help generation. Each {@link Parameters} element describes a possible
 * parameter signature for the command, allowing for overloading and flexible command usage.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Can be applied to command handler methods to specify multiple parameter signatures.</li>
 *   <li>Supports advanced argument parsing and tab completion based on declared parameters.</li>
 *   <li>Used at runtime for command validation, documentation, and IDE support.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Annotate a command method with <code>@CommandParameters(parameters = { @Parameters(...), @Parameters(...) })</code> to declare multiple signatures.</li>
 *   <li>Each {@link Parameters} element describes a possible argument set for the command.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * @CommandParameters(parameters = {
 *     @Parameters(args = {"player", "message"}, description = "Sends a message to a player"),
 *     @Parameters(args = {"player"}, description = "Shows info about a player")
 * })
 * public void onCommand(CommandSender sender, String[] args) {
 *     // Command logic here
 * }
 * </pre>
 *
 * @author nilsbrychzy
 * @since PowerNukkitX 1.19.50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandParameters {
     Parameters[] parameters() default {};
}

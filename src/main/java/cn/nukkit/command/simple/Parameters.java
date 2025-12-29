package cn.nukkit.command.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining a set of command parameters for a command handler method in PowerNukkitX.
 * <p>
 * This annotation is used to describe a named parameter set for a command, specifying the expected argument structure
 * for a particular command usage. Each {@code Parameters} annotation contains a name (for identification or documentation)
 * and an array of {@link Parameter} elements describing each argument in the set.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Can be applied to command handler methods to declare a named set of parameters.</li>
 *   <li>Supports advanced command argument parsing, tab completion, and help generation.</li>
 *   <li>Used at runtime for command validation, documentation, and IDE support.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Annotate a command method with <code>@Parameters(name = "send", parameters = { @Parameter(name = "player", ...), @Parameter(name = "message", ...) })</code>.</li>
 *   <li>Multiple {@code Parameters} annotations can be used via a container annotation (e.g., {@link CommandParameters}).</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * @Parameters(name = "send", parameters = {
 *     @Parameter(name = "player", type = CommandParamType.TARGET),
 *     @Parameter(name = "message", type = CommandParamType.RAWTEXT, optional = true)
 * })
 * public void onSendCommand(CommandSender sender, String[] args) {
 *     // Command logic here
 * }
 * </pre>
 *
 * @author nilsbrychzy
 * @see Parameter
 * @see CommandParameters
 * @since PowerNukkitX 1.19.50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters {
    String name();
    Parameter[] parameters();
}

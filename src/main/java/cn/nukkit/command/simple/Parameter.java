package cn.nukkit.command.simple;

import cn.nukkit.command.data.CommandParamType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining a single command parameter in PowerNukkitX.
 * <p>
 * This annotation is used to describe an individual parameter for a command handler method, specifying its name, type,
 * and whether it is optional. The command framework uses this metadata for argument parsing, tab completion, help generation,
 * and validation of command input.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Can be applied to command handler methods to declare expected parameters.</li>
 *   <li>Specifies the parameter's name, type (from {@link CommandParamType}), and optionality.</li>
 *   <li>Supports advanced command argument parsing and IDE support.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Annotate a command method with <code>@Parameter(name = "player", type = CommandParamType.TARGET, optional = false)</code>.</li>
 *   <li>Multiple @Parameter annotations can be used in conjunction with a container annotation (e.g., {@code @Parameters}).</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * @Parameter(name = "player", type = CommandParamType.TARGET)
 * @Parameter(name = "message", type = CommandParamType.RAWTEXT, optional = true)
 * public void onMessageCommand(CommandSender sender, String[] args) {
 *     // Command logic here
 * }
 * </pre>
 *
 * @author nilsbrychzy
 * @see CommandParamType
 * @since PowerNukkitX 1.19.50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    String name();

    CommandParamType type() default CommandParamType.RAWTEXT;

    boolean optional() default false;
}

package cn.nukkit.command.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining a command handler method in PowerNukkitX.
 * <p>
 * This annotation is used to mark methods as command handlers, specifying the command's name, description, usage message,
 * and aliases. The command framework uses this metadata to register and document commands, generate help messages,
 * and route command execution to the appropriate method.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Can be applied to methods to declare them as command handlers.</li>
 *   <li>Specifies the command's primary name, description, usage message, and aliases.</li>
 *   <li>Used at runtime for command registration, help generation, and execution.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Annotate a method with <code>@Command(name = "mycommand", description = "Does something", usageMessage = "/mycommand &lt;arg&gt;", aliases = {"mc", "cmd"})</code>.</li>
 *   <li>The method should match the expected signature for command handlers in the framework.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * @Command(name = "greet", description = "Sends a greeting", usageMessage = "/greet &lt;player&gt;", aliases = {"hello", "hi"})
 * public void onGreetCommand(CommandSender sender, String[] args) {
 *     // Command logic here
 * }
 * </pre>
 *
 * @author Tee7even
 * @since PowerNukkitX 1.19.50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();

    String description() default "";

    String usageMessage() default "";

    String[] aliases() default {};
}
package cn.nukkit.command.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying the required permission to execute a command handler method in PowerNukkitX.
 * <p>
 * This annotation is used to declare the permission node that a sender must have to execute the annotated command method.
 * The command framework uses this metadata to enforce permission checks before invoking the command logic, ensuring that
 * only authorized users can access certain commands.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Can be applied to command handler methods to specify a required permission node.</li>
 *   <li>Supports integration with the permission system for access control.</li>
 *   <li>Used at runtime for permission validation before command execution.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Annotate a command method with <code>@CommandPermission("myplugin.command.use")</code> to require that permission.</li>
 *   <li>The value should match a registered permission node in the server's permission system.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * @CommandPermission("myplugin.command.admin")
 * public void onAdminCommand(CommandSender sender, String[] args) {
 *     // Command logic here
 * }
 * </pre>
 *
 * @author Tee7even
 * @since PowerNukkitX 1.19.50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {
    String value();
}

package cn.nukkit.command.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a command handler method cannot be executed from the console in PowerNukkitX.
 * <p>
 * This annotation is used to restrict the execution of a command to in-game players only. When applied to a command handler method,
 * the command framework will prevent the console (or any non-player sender) from invoking the annotated command, typically returning
 * an error message or ignoring the command.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Can be applied to command handler methods to forbid console execution.</li>
 *   <li>Enforced at runtime by the command framework.</li>
 *   <li>Improves command security and user experience by preventing inappropriate usage contexts.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Annotate a command method with <code>@ForbidConsole</code> to restrict it to players only.</li>
 *   <li>Typically used for commands that require a player context (e.g., teleport, inventory commands).</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * @ForbidConsole
 * public void onTeleportCommand(CommandSender sender, String[] args) {
 *     // Command logic that requires a player
 * }
 * </pre>
 *
 * @author Tee7even
 * @since PowerNukkitX 1.19.50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForbidConsole {
}

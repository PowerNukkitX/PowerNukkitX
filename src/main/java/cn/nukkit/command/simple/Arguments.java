package cn.nukkit.command.simple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying the minimum and maximum number of arguments accepted by a command method in PowerNukkitX.
 * <p>
 * This annotation is intended to be used on methods that handle command execution, allowing developers to declare
 * the expected argument count for validation and help generation. The command framework can use this metadata to
 * enforce argument constraints and provide user feedback when the number of arguments is outside the allowed range.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Can be applied to command handler methods to specify argument count constraints.</li>
 *   <li>Supports both minimum and maximum argument limits.</li>
 *   <li>Used at runtime for command validation and documentation.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Annotate a command method with <code>@Arguments(min = 1, max = 3)</code> to require between 1 and 3 arguments.</li>
 *   <li>If <code>min</code> or <code>max</code> is not specified, the default is 0 (no constraint).</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * @Arguments(min = 2, max = 4)
 * public void onCommand(CommandSender sender, String[] args) {
 *     // Command logic here
 * }
 * </pre>
 *
 * @author Tee7even
 * @since PowerNukkitX 1.19.50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arguments {
    int min() default 0;

    int max() default 0;
}
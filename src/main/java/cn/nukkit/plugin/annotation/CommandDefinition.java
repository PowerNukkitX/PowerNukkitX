package cn.nukkit.plugin.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link cn.nukkit.command.Command} subclass for automatic registration.
 * <p>
 * The name and the rest of the command metadata live on the annotation, so the
 * annotated class needs <b>no constructor</b> — the PNX annotation processor
 * instantiates it via the no-argument {@link cn.nukkit.command.Command#Command()}
 * constructor, applies the metadata below (name through
 * {@link cn.nukkit.command.Command#setName(String)}, the rest through the
 * matching setters) and registers it with the server command map when the plugin
 * is enabled.
 * <p>
 * Example:
 * <pre>
 * &#64;CommandDefinition(name = "heal", aliases = {"h"}, permission = "myplugin.heal",
 *          description = "Heals a player")
 * public class HealCommand extends Command {
 *     &#64;Override
 *     public boolean execute(CommandSender sender, String label, String[] args) {
 *         // ...
 *         return true;
 *     }
 * }
 * </pre>
 * Constraints (enforced by the processor, each violation is a compile error):
 * <ul>
 *     <li>The annotated type must extend {@link cn.nukkit.command.Command}.</li>
 *     <li>The annotated type must be a concrete (non-abstract) class with an
 *     accessible no-argument constructor (the implicit default is fine).</li>
 * </ul>
 *
 * @see PluginMeta
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CommandDefinition {

    /**
     * The command name (the label players type). Required.
     */
    String name();

    /**
     * Alternative names for the command. Optional.
     */
    String[] aliases() default {};

    /**
     * The permission required to use the command. Optional.
     */
    String permission() default "";

    /**
     * A human readable description. Optional.
     */
    String description() default "";

    /**
     * The usage message. Optional; defaults to {@code "/" + name} when omitted.
     */
    String usage() default "";

    CommandMode commandMode() default CommandMode.COMMAND_TREE;

    enum CommandMode {
        COMMAND_TREE,
        PARAM_TREE,
        RAW
    }
}
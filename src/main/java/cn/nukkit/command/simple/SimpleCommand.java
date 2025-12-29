package cn.nukkit.command.simple;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.lang.TranslationContainer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Represents a reflective command handler for PowerNukkitX using method invocation.
 * <p>
 * This class wraps a method and its owning object to provide a dynamic command handler. It supports argument count validation,
 * permission checks, console restrictions, and usage message feedback. The command is executed by invoking the specified method
 * with the sender, command label, and arguments. This enables flexible, annotation-driven command registration and execution.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Reflectively invokes a method as a command handler.</li>
 *   <li>Supports argument count validation (min/max).</li>
 *   <li>Supports console execution restriction (forbidConsole).</li>
 *   <li>Handles permission checks and usage message feedback.</li>
 *   <li>Logs errors on command execution failure.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Construct with the target object, method, and command metadata (name, description, usage, aliases).</li>
 *   <li>Configure argument limits and console restriction via setters.</li>
 *   <li>Register with the command system to enable reflective command handling.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Suppose you have a method:
 * public boolean myCommandHandler(CommandSender sender, String label, String[] args) { ... }
 * // Register as:
 * new SimpleCommand(this, MyClass.class.getMethod("myCommandHandler", ...), "mycmd", "desc", "/mycmd", new String[]{"alias"});
 * </pre>
 *
 * <b>Thread Safety:</b> This class is not thread-safe for concurrent modification of configuration.
 *
 * @author Tee7even
 * @see cn.nukkit.command.Command
 * @see java.lang.reflect.Method
 * @since PowerNukkitX 1.19.50
 */
@Slf4j
public class SimpleCommand extends Command {
    private Object object;
    private Method method;
    private boolean forbidConsole;
    private int maxArgs;
    private int minArgs;

    public SimpleCommand(Object object, Method method, String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
        this.object = object;
        this.method = method;
    }

    public void setForbidConsole(boolean forbidConsole) {
        this.forbidConsole = forbidConsole;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public void sendUsageMessage(CommandSender sender) {
        if (!this.usageMessage.equals("")) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }
    }

    public void sendInGameMessage(CommandSender sender) {
        sender.sendMessage(new TranslationContainer("nukkit.command.generic.ingame"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (this.forbidConsole && sender instanceof ConsoleCommandSender) {
            this.sendInGameMessage(sender);
            return false;
        } else if (!this.testPermission(sender)) {
            return false;
        } else if (this.maxArgs != 0 && args.length > this.maxArgs) {
            this.sendUsageMessage(sender);
            return false;
        } else if (this.minArgs != 0 && args.length < this.minArgs) {
            this.sendUsageMessage(sender);
            return false;
        }

        boolean success = false;

        try {
            success = (Boolean) this.method.invoke(this.object, sender, commandLabel, args);
        } catch (Exception exception) {
            log.error("Failed to execute {} by {}", commandLabel, sender.getName(), exception);
        }

        if (!success) {
            this.sendUsageMessage(sender);
        }

        return success;
    }
}

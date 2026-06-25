package cn.nukkit.command;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kanelucky
 */
@ApiStatus.Experimental
public class CommandContext {

    private final CommandSender sender;
    private final Map<String, Object> arguments = new HashMap<>();

    public CommandContext(@NotNull CommandSender sender) {
        this.sender = sender;
    }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    public void putArg(@NotNull String name, @NotNull Object value) {
        arguments.put(name, value);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getArg(@NotNull String name) {
        return (T) arguments.get(name);
    }

    public boolean hasArg(@NotNull String name) {
        return arguments.containsKey(name);
    }

    /**
     * Command ran successfully.
     */
    public CommandResult success() {
        return CommandResult.success();
    }

    /**
     * Command failed with no message.
     */
    public CommandResult fail() {
        return CommandResult.fail();
    }

    /**
     * Command failed - message will be sent to the sender.
     */
    public CommandResult fail(@Nullable String message) {
        return CommandResult.fail(message);
    }
}
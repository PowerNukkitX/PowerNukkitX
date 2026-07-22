package org.powernukkitx.command;

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
    public <T> T getArg(@NotNull String name, T fallback) {
        return (T) arguments.getOrDefault(name, fallback);
    }

    public boolean hasArg(@NotNull String name) {
        return arguments.containsKey(name);
    }
}

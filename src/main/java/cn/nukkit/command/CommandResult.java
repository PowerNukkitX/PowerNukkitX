package cn.nukkit.command;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kanelucky
 */
@ApiStatus.Experimental
public class CommandResult {

    private static final CommandResult SUCCESS = new CommandResult(true, null);
    private static final CommandResult FAIL = new CommandResult(false, null);

    private final boolean success;
    @Nullable
    private final String message;

    private CommandResult(boolean success, @Nullable String message) {
        this.success = success;
        this.message = message;
    }

    public static CommandResult success() {
        return SUCCESS;
    }

    public static CommandResult fail() {
        return FAIL;
    }

    public static CommandResult fail(@Nullable String message) {
        return new CommandResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    @Nullable
    public String getMessage() {
        return message;
    }
}
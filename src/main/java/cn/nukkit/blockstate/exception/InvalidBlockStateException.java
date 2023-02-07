package cn.nukkit.blockstate.exception;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
public class InvalidBlockStateException extends IllegalStateException {
    private static final long serialVersionUID = 643372054081065905L;

    @NotNull
    private final BlockState state;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockStateException(@NotNull BlockState state) {
        super(createMessage(state, null));
        this.state = state;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockStateException(@NotNull BlockState state, String message) {
        super(createMessage(state, message));
        this.state = state;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockStateException(@NotNull BlockState state, String message, Throwable cause) {
        super(createMessage(state, message), cause);
        this.state = state;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public InvalidBlockStateException(@NotNull BlockState state, Throwable cause) {
        super(createMessage(state, null), cause);
        this.state = state;
    }

    private static String createMessage(@NotNull BlockState state, @Nullable String message) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("The block state ").append(state).append(" is invalid");
        if (message != null && !message.isEmpty()) {
            sb.append(": ").append(message);
        }
        try {
            String properties = state.getProperties().toString();
            sb.append('\n').append(properties);
        } catch (Throwable e) {
            sb.append("\nProperty.toString() failed: ").append(e);
        }
        return sb.toString();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NotNull
    public BlockState getState() {
        return state;
    }
}

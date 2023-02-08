package cn.nukkit.network.protocol.types;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

@Value
public class CommandOutputMessage {
    public CommandOutputMessage(@NotNull String messageId) {
        this(false, messageId, new String[]{});
    }

    public CommandOutputMessage(@NotNull String messageId, @NotNull String... parameters) {
        this(false, messageId, parameters);
    }

    public CommandOutputMessage(boolean internal, @NotNull String messageId, @NotNull String[] parameters) {
        this.internal = internal;
        this.messageId = messageId;
        this.parameters = parameters;
    }

    boolean internal;
    @NotNull
    String messageId;
    @NotNull
    String[] parameters;
}

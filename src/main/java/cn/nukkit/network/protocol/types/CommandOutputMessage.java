package cn.nukkit.network.protocol.types;

import lombok.NonNull;
import lombok.Value;

@Value
public class CommandOutputMessage {
    public CommandOutputMessage(@NonNull String messageId) {
        this(false, messageId, new String[]{});
    }

    public CommandOutputMessage(@NonNull String messageId, @NonNull String... parameters) {
        this(false, messageId, parameters);
    }

    public CommandOutputMessage(boolean internal, @NonNull String messageId, @NonNull String[] parameters) {
        this.internal = internal;
        this.messageId = messageId;
        this.parameters = parameters;
    }

    boolean internal;
    @NonNull
    String messageId;
    @NonNull
    String[] parameters;
}

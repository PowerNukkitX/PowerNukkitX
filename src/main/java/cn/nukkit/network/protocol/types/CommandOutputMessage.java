package cn.nukkit.network.protocol.types;

import lombok.NonNull;
import lombok.Value;

@Value
public class CommandOutputMessage {
    boolean internal;
    @NonNull
    String messageId;
    @NonNull
    String[] parameters;
}

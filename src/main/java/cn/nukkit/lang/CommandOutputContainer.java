package cn.nukkit.lang;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.network.protocol.types.CommandOutputMessage;

import java.util.ArrayList;
import java.util.List;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class CommandOutputContainer implements Cloneable {
    private final List<CommandOutputMessage> messages;
    private final String data = null;
    private int successCount;

    public CommandOutputContainer() {
        this.messages = new ArrayList<>();
    }

    public CommandOutputContainer(String messageId, String[] parameters, int successCount) {
        this(List.of(new CommandOutputMessage(false, messageId, parameters)), successCount);
    }

    public CommandOutputContainer(List<CommandOutputMessage> messages, int successCount) {
        this.messages = messages;
        this.successCount = successCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public List<CommandOutputMessage> getMessages() {
        return messages;
    }

    @Override
    protected CommandOutputContainer clone() throws CloneNotSupportedException {
        return (CommandOutputContainer) super.clone();
    }
}

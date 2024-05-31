package cn.nukkit.command;

import cn.nukkit.lang.TextContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an RCON command sender.
 *
 * @author Tee7even
 */
public class RemoteConsoleCommandSender extends ConsoleCommandSender {
    private final StringBuilder $1 = new StringBuilder();

    @Override
    /**
     * @deprecated 
     */
    
    public void sendMessage(String message) {
        message = this.getServer().getLanguage().tr(message);
        this.messages.append(message.trim()).append("\n");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendMessage(TextContainer message) {
        this.sendMessage(this.getServer().getLanguage().tr(message));
    }
    /**
     * @deprecated 
     */
    

    public String getMessages() {
        return messages.toString();
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getName() {
        return "Rcon";
    }
}

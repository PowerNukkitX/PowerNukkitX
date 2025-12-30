package cn.nukkit.command;

import cn.nukkit.lang.TextContainer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a remote console (RCON) command sender for the server.
 * <p>
 * This class extends {@link ConsoleCommandSender} to provide RCON-specific message handling and identification.
 * It collects messages sent to the RCON sender, allowing retrieval of all messages as a single string.
 * <p>
 * Features:
 * <ul>
 *   <li>Collects all messages sent via {@link #sendMessage(String)} and {@link #sendMessage(TextContainer)}.</li>
 *   <li>Translates messages using the server's language system before storing.</li>
 *   <li>Provides {@link #getMessages()} to retrieve all collected messages.</li>
 *   <li>Overrides {@link #getName()} to return "Rcon" for identification.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Send messages to the RCON sender using {@link #sendMessage(String)} or {@link #sendMessage(TextContainer)}.</li>
 *   <li>Retrieve all messages sent during a session using {@link #getMessages()}.</li>
 *   <li>Use {@link #getName()} to identify the sender as RCON.</li>
 * </ul>
 * <p>
 * This class is typically used for remote administration and automation via RCON protocol.
 *
 * @author Tee7even
 * @see ConsoleCommandSender
 * @see cn.nukkit.lang.TextContainer
 */
public class RemoteConsoleCommandSender extends ConsoleCommandSender {
    /**
     * Stores all messages sent to the RCON sender during the session.
     */
    private final StringBuilder messages = new StringBuilder();

    /**
     * Sends a plain text message to the RCON sender.
     * The message is translated using the server's language system and appended to the message log.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(String message) {
        message = this.getServer().getLanguage().tr(message);
        this.messages.append(message.trim()).append("\n");
    }

    /**
     * Sends a formatted or translatable message to the RCON sender.
     * The message is translated and appended to the message log.
     *
     * @param message the TextContainer message to send
     */
    @Override
    public void sendMessage(TextContainer message) {
        this.sendMessage(this.getServer().getLanguage().tr(message));
    }

    /**
     * Retrieves all messages sent to the RCON sender as a single string.
     *
     * @return the concatenated messages
     */
    public String getMessages() {
        return messages.toString();
    }

    /**
     * Gets the name of the RCON sender (always "Rcon").
     *
     * @return the string "Rcon"
     */
    @Override
    @NotNull public String getName() {
        return "Rcon";
    }
}

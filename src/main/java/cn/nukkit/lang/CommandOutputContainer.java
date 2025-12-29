package cn.nukkit.lang;

import cn.nukkit.network.protocol.types.CommandOutputMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for command output messages, supporting multiple messages and success count tracking.
 * <p>
 * This class is used to encapsulate the payload of a {@link cn.nukkit.network.protocol.CommandOutputPacket},
 * allowing the server to send multiple command output messages in a single response. It also tracks the number
 * of successful command executions associated with the output.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Stores a list of {@link CommandOutputMessage} objects representing command output lines.</li>
 *   <li>Tracks the number of successful command executions.</li>
 *   <li>Provides constructors for single or multiple messages.</li>
 *   <li>Supports cloning for safe reuse or modification.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 *     CommandOutputContainer output = new CommandOutputContainer();
 *     output.getMessages().add(new CommandOutputMessage(false, "message.id", new String[]{"param1"}));
 *     output.incrementSuccessCount();
 * </pre>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * This class is not thread-safe. If used in a multi-threaded context, external synchronization is required.
 * </p>
 *
 * @author PowerNukkitX Team
 * @since 1.0
 */


public class CommandOutputContainer implements Cloneable {
    /**
     * An empty string array constant for convenience.
     */
    public static final String[] EMPTY_STRING = new String[]{};

    /**
     * The list of command output messages contained in this container.
     */
    private final List<CommandOutputMessage> messages;

    /**
     * The number of successful command executions associated with this output.
     */
    private int successCount;

    /**
     * Creates an empty CommandOutputContainer with no messages and zero success count.
     */
    public CommandOutputContainer() {
        this.messages = new ArrayList<>();
        this.successCount = 0;
    }

    /**
     * Creates a CommandOutputContainer with a single message and a specified success count.
     *
     * @param messageId    the message identifier
     * @param parameters   the parameters for the message
     * @param successCount the number of successful command executions
     */
    public CommandOutputContainer(String messageId, String[] parameters, int successCount) {
        this(List.of(new CommandOutputMessage(false, messageId, parameters)), successCount);
    }

    /**
     * Creates a CommandOutputContainer with a list of messages and a specified success count.
     *
     * @param messages     the list of command output messages
     * @param successCount the number of successful command executions
     */
    public CommandOutputContainer(List<CommandOutputMessage> messages, int successCount) {
        this.messages = messages;
        this.successCount = successCount;
    }

    /**
     * Returns the number of successful command executions associated with this output.
     *
     * @return the success count
     */
    public int getSuccessCount() {
        return successCount;
    }

    /**
     * Sets the number of successful command executions associated with this output.
     *
     * @param successCount the new success count
     */
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    /**
     * Increments the success count by one.
     */
    public void incrementSuccessCount() {
        this.successCount++;
    }

    /**
     * Returns the list of command output messages contained in this container.
     *
     * @return the list of command output messages
     */
    public List<CommandOutputMessage> getMessages() {
        return messages;
    }

    /**
     * Creates and returns a shallow copy of this CommandOutputContainer.
     *
     * @return a clone of this instance
     * @throws CloneNotSupportedException if the object's class does not support the Cloneable interface
     */
    @Override
    protected CommandOutputContainer clone() throws CloneNotSupportedException {
        return (CommandOutputContainer) super.clone();
    }
}

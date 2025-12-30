package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.CommandOutputContainer;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.permission.Permissible;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an entity capable of sending commands to the server, such as a player or the console.
 * <p>
 * The CommandSender interface provides methods for sending messages, retrieving sender information,
 * and determining the sender's type and location. Implementations may represent players, the console,
 * command blocks, or other entities capable of executing commands.
 * <p>
 * Usage:
 * <ul>
 *   <li>Use {@link #sendMessage(String)} or {@link #sendMessage(TextContainer)} to send messages to the sender.</li>
 *   <li>Use {@link #getName()} to get the sender's name ("CONSOLE" for the console).</li>
 *   <li>Use {@link #isPlayer()} and {@link #isEntity()} to check the sender's type.</li>
 *   <li>Use {@link #getPosition()} and {@link #getLocation()} to get the sender's coordinates.</li>
 *   <li>Use {@link #sendCommandOutput(CommandOutputContainer)} to send command output containers.</li>
 * </ul>
 * <p>
 * For entity-based senders, use {@link #asEntity()} and {@link #asPlayer()} to retrieve the underlying entity or player.
 * <p>
 * Default implementations for entity and player checks return false/null; override in concrete implementations as needed.
 * <p>
 * The default position and location are (0, 0, 0) in the server's default level if not overridden.
 *
 * @author MagicDroidX (code) @ Nukkit Project
 * @author 粉鞋大妈 (javadoc) @ Nukkit Project
 * @author smartcmd (code) @ PowerNukkitX Project
 * @see cn.nukkit.command.CommandExecutor#onCommand
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface CommandSender extends Permissible {
    /**
     * Sends a plain text message to the command sender.
     *
     * @param message the message to send
     * @see cn.nukkit.utils.TextFormat
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void sendMessage(String message);

    /**
     * Sends a formatted or translatable message to the command sender.
     *
     * @param message the TextContainer message to send
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void sendMessage(TextContainer message);

    /**
     * Sends a command output container to the sender, for advanced command feedback.
     *
     * @param container the command output container to send
     */
    void sendCommandOutput(CommandOutputContainer container);

    /**
     * Gets the server instance associated with this sender.
     *
     * @return the server instance
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Server getServer();

    /**
     * Gets the name of the command sender.
     * <p>
     * For players, returns the player name (not display name). For the console, returns "CONSOLE".
     * To check if the sender is the console, use {@code if(sender instanceof ConsoleCommandSender)}.
     *
     * @return the name of the sender
     * @see cn.nukkit.Player#getName()
     * @see cn.nukkit.command.ConsoleCommandSender#getName()
     * @see cn.nukkit.plugin.PluginDescription
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    @NotNull
    String getName();

    /**
     * Checks if the sender is a player.
     *
     * @return true if the sender is a player, false otherwise
     */
    boolean isPlayer();

    /**
     * Checks if the sender is an entity (not just a player).
     * <p>
     * Prefer this method over {@code instanceof Entity} checks, as some senders may act as entities
     * without being direct instances of Entity (e.g., ExecutorCommandSender).
     *
     * @return true if the sender is an entity, false otherwise
     */
    default boolean isEntity() {
        return false;
    }

    /**
     * Gets the underlying entity if the sender is an entity.
     *
     * @return the Entity instance, or null if not applicable
     */
    default Entity asEntity() {
        return null;
    }

    /**
     * Gets the underlying player if the sender is a player.
     *
     * @return the Player instance, or null if not applicable
     */
    default Player asPlayer() {
        return null;
    }

    /**
     * Gets the position of the sender in the world.
     * <p>
     * Default is (0, 0, 0) in the server's default level if not overridden.
     *
     * @return the sender's position
     */
    @NotNull
    default Position getPosition() {
        return new Position(0, 0, 0, Server.getInstance().getDefaultLevel());
    }

    /**
     * Gets the location of the sender in the world.
     * <p>
     * Default is (0, 0, 0) in the server's default level if not overridden.
     *
     * @return the sender's location
     */
    @NotNull
    default Location getLocation() {
        return new Location(0, 0, 0, Server.getInstance().getDefaultLevel());
    }
}

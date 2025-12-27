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
 * Who sends commands.<br>
 * That can be a player or a console.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @author smartcmd(code) @ PowerNukkitX Project
 * @see cn.nukkit.command.CommandExecutor#onCommand
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface CommandSender extends Permissible {

    /**
     * Sends a message to the command sender.
     *
     * @param message Message to send.
     * @see cn.nukkit.utils.TextFormat
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void sendMessage(String message);

    /**
     * Sends a message to the command sender.
     *
     * @param message Message to send.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void sendMessage(TextContainer message);

    /**
     * Send command output.
     *
     * @param container the container
     */
    void sendCommandOutput(CommandOutputContainer container);

    /**
     * Returns the server of the command sender.
     *
     * @return the server of the command sender.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Server getServer();

    /**
     * Returns the name of the command sender.<br>
     * If this command sender is a player, will return his/her player name(not display name).<br>
     * If it is a console, will return {@code "CONSOLE"}.<br>
     * When you need to determine if the sender is a console, use this:<br>
     * {@code if(sender instanceof ConsoleCommandSender) .....;}
     *
     * @return The name of the command sender.
     * @see cn.nukkit.Player#getName()
     * @see cn.nukkit.command.ConsoleCommandSender#getName()
     * @see cn.nukkit.plugin.PluginDescription
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    @NotNull
    String getName();

    /**
     * @return Whether the sender is a player
     */
    boolean isPlayer();

    /**
     * Please use this method to check whether the sender is an entity instead of using code {@code "xxx instanceof Entity"} <br>
     * Because the sender may not an instance of {@code "Entity"} but in fact it is executing commands identity as an entity(e.g.,: {@code "ExecutorCommandSender"})
     *
     * @return Whether the sender is an entity
     */
    default boolean isEntity() {
        return false;
    }

    /**
     * Return the entity who executes the command if the sender is a entity.
     *
     * @return Entity instance
     */
    default Entity asEntity() {
        return null;
    }

    /**
     * Return the player who executes the command if the sender is a player.
     *
     * @return Player instance
     */
    default Player asPlayer() {
        return null;
    }

    /**
     * @return Return the sender's position.
     */
    @NotNull
    default Position getPosition() {
        return new Position(0, 0, 0, Server.getInstance().getDefaultLevel());
    }

    /**
     * @return Return the sender's location.
     */
    @NotNull
    default Location getLocation() {
        return new Location(0, 0, 0, Server.getInstance().getDefaultLevel());
    }
}

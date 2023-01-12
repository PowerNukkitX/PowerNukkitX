package cn.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.permission.Permissible;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 能发送命令的对象.<br>
 * 可以是一个玩家或者一个控制台或者一个实体或者其他.
 * <p>
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
     * 给命令发送者返回信息.<p>
     * Sends a message to the command sender.
     *
     * @param message 要发送的信息.<br>Message to send.
     * @see cn.nukkit.utils.TextFormat
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void sendMessage(String message);

    /**
     * 给命令发送者返回信息.<p>
     * Sends a message to the command sender.
     *
     * @param message 要发送的信息.<br>Message to send.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void sendMessage(TextContainer message);

    /**
     * 返回命令发送者所在的服务器.<p>
     * Returns the server of the command sender.
     *
     * @return 命令发送者所在的服务器.<br>the server of the command sender.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Server getServer();

    /**
     * 返回命令发送者的名称.<br>
     * 如果命令发送者是一个玩家，将会返回他的玩家名字(name)不是显示名字(display name).<br>
     * 如果命令发送者是控制台，将会返回{@code "CONSOLE"}.<br>
     * 当你需要判断命令的执行者是不是控制台时，可以用这个：<br>
     * {@code if(sender instanceof ConsoleCommandSender) .....;}
     * <p>
     * Returns the name of the command sender.<br>
     * If this command sender is a player, will return his/her player name(not display name).<br>
     * If it is a console, will return {@code "CONSOLE"}.<br>
     * When you need to determine if the sender is a console, use this:<br>
     * {@code if(sender instanceof ConsoleCommandSender) .....;}
     *
     * @return 命令发送者的名称.<br>the name of the command sender.
     * @see cn.nukkit.Player#getName()
     * @see cn.nukkit.command.ConsoleCommandSender#getName()
     * @see cn.nukkit.plugin.PluginDescription
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    @Nonnull
    String getName();

    /**
     * @return 发送者是否为玩家<br>whether the sender is an player
     */
    boolean isPlayer();

    /**
     * 请使用这个方法来检查发送者是否是一个实体，而不是使用代码{@code "xxx instanceof Entity"}.<br>
     * 因为发送者可能不是{@code "Entity"}的一个实例，但实际上它是以一个实体的身份执行命令(例如：{@code "ExecutorCommandSender"})
     * <p>
     * please use this method to check whether the sender is an entity instead of using code {@code "xxx instanceof Entity"} <br>
     * because the sender may not an instance of {@code "Entity"} but in fact it is executing commands identity as an entity(eg: {@code "ExecutorCommandSender"})
     *
     * @return 发送者是否为实体<br>whether the sender is an entity
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    default boolean isEntity() {
        return false;
    }

    /**
     * 如果发送者是一个实体，返回执行该命令的实体.
     * <p>
     * return the entity who execute the command if the sender is a entity.
     *
     * @return 实体对象<br>Entity instance
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Nullable
    default Entity asEntity() {
        return null;
    }

    /**
     * 如果发送者是一个玩家，返回执行该命令的玩家.
     * <p>
     * return the player who execute the command if the sender is a player.
     *
     * @return 玩家对象<br>Player instance
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Nullable
    default Player asPlayer() {
        return null;
    }


    /**
     * @return 返回发送者的Position<br>return the sender's position.
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Nonnull
    default Position getPosition() {
        return new Position(0, 0, 0, Server.getInstance().getDefaultLevel());
    }


    /**
     * @return 返回发送者的Location<br>return the sender's location.
     */
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    @Nonnull
    default Location getLocation() {
        return new Location(0, 0, 0, Server.getInstance().getDefaultLevel());
    }
}

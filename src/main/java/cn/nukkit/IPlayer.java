package cn.nukkit;

import cn.nukkit.metadata.Metadatable;
import cn.nukkit.permission.ServerOperator;

import java.util.UUID;

/**
 * An interface to describe a player and get its information.
 * This player can be online or offline.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.Player
 * @see cn.nukkit.OfflinePlayer
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface IPlayer extends ServerOperator, Metadatable {

    /**
     * Returns if this player is online.
     *
     * @return If this player is online.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isOnline();

    /**
     * Returns the name of this player.
     * Notice that this will only return its login name. If you need its display name, turn to {@link cn.nukkit.Player#getDisplayName}</p>
     *
     * @return The name of this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    String getName();

    UUID getUniqueId();

    /**
     * Returns if this player is banned.
     *
     * @return The name of this player.
     * @see #setBanned
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isBanned();

    /**
     * Sets this player to be banned or to be pardoned.
     *
     * @param value {@code true} for ban and {@code false} for pardon.
     *
     * @see #isBanned
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setBanned(boolean value);

    /**
     * Returns if whitelist allows this player.
     *
     * @return 这个玩家是否已加入白名单。<br>If whitelist allows this player.
     * @see cn.nukkit.Server#isWhitelisted
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isWhitelisted();

    /**
     * Adds this player to the whitelist, or removes it from the whitelist.
     *
     * @param value {@code true} for add and {@code false} for remove.
     * @see #isWhitelisted
     * @see cn.nukkit.Server#addWhitelist
     * @see cn.nukkit.Server#removeWhitelist
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setWhitelisted(boolean value);

    /**
     * Returns a {@code Player} object for this interface.
     *
     * @return a {@code Player} object for this interface.
     * @see cn.nukkit.Server#getPlayerExact
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Player getPlayer();

    /**
     * Returns the server carrying this player.
     *
     * @return the server carrying this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Server getServer();

    /**
     * Returns the time this player first played in this server.
     *
     * @return Unix time in seconds.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Long getFirstPlayed();

    /**
     * Returns the time this player last joined in this server.
     *
     * @return Unix time in seconds.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Long getLastPlayed();

    /**
     * Returns if this player has played in this server before.
     * If you want to know if this player is the first time playing in this server, you can use:<br>
     * </p>
     * <pre>if(!player.hasPlayerBefore()) {...}</pre>
     *
     * @return If this player has played in this server before.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean hasPlayedBefore();

}

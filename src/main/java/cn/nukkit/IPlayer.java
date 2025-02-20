package cn.nukkit;

import cn.nukkit.metadata.Metadatable;
import cn.nukkit.permission.ServerOperator;

import java.util.UUID;

/**
 * An interface to describe a player and get its information.
 *
 * <p>This player can be online or offline.</p>
 *
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
     *
     * <p>Notice that this will only return its login name. If you need its display name, refer to
     * {@link cn.nukkit.Player#getDisplayName}</p>
     *
     * @return The name of this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    String getName();

    /**
     * Returns the unique identifier (UUID) of this player.
     *
     * @return The UUID of this player.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    UUID getUniqueId();

    /**
     * Returns if this player is banned.
     *
     * @return If this player is banned.
     * @see #setBanned
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isBanned();

    /**
     * Sets this player to be banned or to be pardoned.
     *
     * @param value {@code true} for ban and {@code false} for pardon.
     * @see #isBanned
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setBanned(boolean value);

    /**
     * Returns if this player is pardoned by whitelist.
     *
     * @return If this player is pardoned by whitelist.
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
     * @return A {@code Player} object for this interface.
     * @see cn.nukkit.Server#getPlayerExact
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    Player getPlayer();

    /**
     * Returns the server carrying this player.
     *
     * @return The server carrying this player.
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
     *
     * <p>If you want to know if this player is playing for the first time, you can use:</p>
     * <pre>if(!player.hasPlayedBefore()) {...}</pre>
     *
     * @return If this player has played in this server before.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean hasPlayedBefore();
}
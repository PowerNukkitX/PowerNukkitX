package cn.nukkit.player;

import cn.nukkit.Server;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.permission.ServerOperator;
import java.util.UUID;

/**
 * An interface to describe a player and get its information.
 *
 * This player can be online or offline.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see Player
 */
public interface IPlayer extends ServerOperator, Metadatable {

    /**
     * Returns if this player is online.
     *
     * @return If this player is online.
     */
    boolean isOnline();

    /**
     * Returns the name of this player.
     *
     * Notice that this will only return its login name. If you need its display name, turn to
     * {@link Player#getDisplayName}</p>
     */
    String getName();

    UUID getUniqueId();

    /**
     * Returns if this player is banned.
     *
     * @return The name of this player.
     * @see #setBanned
     */
    boolean isBanned();

    /**
     * Sets this player to be banned or to be pardoned.
     *
     * @param value {@code true} for ban and {@code false} for pardon.
     * @see #isBanned
     */
    void setBanned(boolean value);

    /**
     * Returns if this player is pardoned by whitelist.
     *
     * @return If this player is pardoned by whitelist.
     * @see cn.nukkit.Server#isWhitelisted
     */
    boolean isWhitelisted();

    /**
     * Adds this player to the white list, or removes it from the whitelist.
     *
     * @param value {@code true} for add and {@code false} for remove.
     * @see #isWhitelisted
     * @see cn.nukkit.Server#addWhitelist
     * @see cn.nukkit.Server#removeWhitelist
     */
    void setWhitelisted(boolean value);

    /**
     * Returns a {@code Player} object for this interface.
     *
     * @return a {@code Player} object for this interface.
     * @see cn.nukkit.player.PlayerManager#getPlayerExact
     */
    Player getPlayer();

    /**
     * Returns the server carrying this player.
     *
     * @return the server carrying this player.
     */
    Server getServer();

    /**
     * Returns the time this player first played in this server.
     *
     * @return Unix time in seconds.
     */
    Long getFirstPlayed();

    /**
     * Returns the time this player last joined in this server.
     *
     * @return Unix time in seconds.
     */
    Long getLastPlayed();

    /**
     * Returns if this player has played in this server before.
     *
     *
     * @return If this player has played in this server before.
     */
    boolean isPlayedBefore();
}

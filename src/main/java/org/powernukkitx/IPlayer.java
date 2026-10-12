package org.powernukkitx;

import com.google.common.base.Preconditions;
import org.powernukkitx.metadata.Metadatable;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.permission.ServerOperator;

import java.util.UUID;

/**
 * An interface to describe a player and get its information.
 * This player can be online or offline.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @see org.powernukkitx.Player
 * @see org.powernukkitx.OfflinePlayer
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
     * Notice that this will only return its login name. If you need its display name, turn to {@link org.powernukkitx.Player#getDisplayName}</p>
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
     * @return If whitelist allows this player.
     * @see org.powernukkitx.Server#isWhitelisted
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isWhitelisted();

    /**
     * Adds this player to the whitelist, or removes it from the whitelist.
     *
     * @param value {@code true} for add and {@code false} for remove.
     * @see #isWhitelisted
     * @see org.powernukkitx.Server#addWhitelist
     * @see org.powernukkitx.Server#removeWhitelist
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setWhitelisted(boolean value);

    /**
     * Returns a {@code Player} object for this interface.
     *
     * @return a {@code Player} object for this interface.
     * @see org.powernukkitx.Server#getPlayerExact
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

    /**
     * Returns the canonical BDS-compatible player NBT.
     */
    default CompoundTag getNbt() {
        return requireOnlinePlayerForNbt().getNbt();
    }

    /**
     * Replaces the canonical BDS-compatible player NBT.
     */
    default void setNbt(CompoundTag nbt) {
        requireOnlinePlayerForNbt().setNbt(Preconditions.checkNotNull(nbt, "nbt"));
    }

    /**
     * Returns the PNX-specific player NBT sidecar.
     */
    default CompoundTag getNbtExtra() {
        return requireOnlinePlayerForNbt().getNbtExtra();
    }

    /**
     * Replaces the PNX-specific player NBT sidecar.
     */
    default void setNbtExtra(CompoundTag nbtExtra) {
        requireOnlinePlayerForNbt().setNbtExtra(Preconditions.checkNotNull(nbtExtra, "nbtExtra"));
    }

    /**
     * Returns the custom player NBT sidecar.
     */
    default CompoundTag getNbtCustom() {
        return requireOnlinePlayerForNbt().getNbtCustom();
    }

    /**
     * Replaces the custom player NBT sidecar.
     */
    default void setNbtCustom(CompoundTag nbtCustom) {
        requireOnlinePlayerForNbt().setNbtCustom(Preconditions.checkNotNull(nbtCustom, "nbtCustom"));
    }

    /**
     * Returns all persistent player NBT layers.
     */
    default PlayerNBTData getNbtFullData() {
        return new PlayerNBTData(this.getNbt(), this.getNbtExtra(), this.getNbtCustom());
    }

    /**
     * Replaces all persistent player NBT layers.
     */
    default void setNbtFullData(PlayerNBTData data) {
        Preconditions.checkNotNull(data, "data");
        this.setNbt(data.getNbt());
        this.setNbtExtra(data.getNbtExtra());
        this.setNbtCustom(data.getNbtCustom());
    }

    /**
     * Saves this player's persistent data.
     */
    default void save() {
        requireOnlinePlayerForNbt().save();
    }

    private Player requireOnlinePlayerForNbt() {
        Player player = this.getPlayer();
        Preconditions.checkState(player != null, "NBT operation is unavailable for this offline player implementation");
        return player;
    }

}

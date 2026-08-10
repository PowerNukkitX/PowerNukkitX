package org.powernukkitx.event.player;

import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.Event;
import org.powernukkitx.event.HandlerList;
import lombok.Getter;
import lombok.Setter;
import org.cloudburstmc.protocol.bedrock.data.BuildPlatform;
import org.cloudburstmc.protocol.bedrock.data.PlatformType;
import org.cloudburstmc.protocol.bedrock.util.ChainValidationResult;
import org.jetbrains.annotations.Nullable;

/**
 * Called directly when the client sends a LoginPacket to the server. The player
 * object has not yet been created.
 * The client jwt data will be validated after this event is called.
 * Can be cancelled to disconnect the client with the specified kick message.
 * <p>
 * {@code deviceId}, {@code deviceModel}, {@code deviceOs}, {@code platformType}
 * and {@code clientRandomId} are read from the client JWT payload and are
 * <b>not</b> cryptographically
 * verified at this point - the client could have altered them. They are fine
 * for ban checks, but must not be trusted
 * for anything security critical.
 *
 * @author Kaooot
 */
@Getter
public class PlayerPreLoginEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ChainValidationResult.IdentityClaims identityClaims;

    /** The raw IP address the client connected from. */
    @Nullable
    private final String address;

    /**
     * The device id sent by the client, or {@code null} when it could not be read
     * from the client JWT.
     */
    @Nullable
    private final String deviceId;

    /**
     * The device model sent by the client, or {@code null} when it could not be
     * read from the client JWT.
     */
    @Nullable
    private final String deviceModel;

    /**
     * The device operating system the client reports (Android, iOS, Windows...),
     * or {@code null} when unknown.
     */
    @Nullable
    private final BuildPlatform deviceOs;

    /**
     * The platform type the client reports (desktop, console, mobile), or
     * {@code null} when unknown.
     */
    @Nullable
    private final PlatformType platformType;

    /**
     * The client's random install id, or {@code 0} when it could not be read from
     * the client JWT.
     */
    private final long clientRandomId;

    @Setter
    private String kickMessage = "";

    /**
     * @deprecated use
     *             {@link #PlayerPreLoginEvent(ChainValidationResult.IdentityClaims, String, String, String, BuildPlatform, PlatformType, long)}
     *             instead, which also carries the client address, device data and
     *             platform.
     */
    @Deprecated
    public PlayerPreLoginEvent(ChainValidationResult.IdentityClaims identityClaims) {
        this(identityClaims, null, null, null, null, null, 0L);
    }

    public PlayerPreLoginEvent(ChainValidationResult.IdentityClaims identityClaims, @Nullable String address,
            @Nullable String deviceId, @Nullable String deviceModel, @Nullable BuildPlatform deviceOs,
            @Nullable PlatformType platformType, long clientRandomId) {
        this.identityClaims = identityClaims;
        this.address = address;
        this.deviceId = deviceId;
        this.deviceModel = deviceModel;
        this.deviceOs = deviceOs;
        this.platformType = platformType;
        this.clientRandomId = clientRandomId;
    }

    public static HandlerList getHandlers() {
        return HANDLERS;
    }
}
package org.powernukkitx.network.process;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodecHelper;
import org.cloudburstmc.protocol.bedrock.data.BuildPlatform;
import org.cloudburstmc.protocol.bedrock.data.EncodingSettings;
import org.cloudburstmc.protocol.bedrock.data.payload.list.PlayerListAddEntry;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.PlayerListPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.cloudburstmc.protocol.common.util.VarInts;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.network.NetworkConstants;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Encodes each online player's entry of the player list once, and builds the player list a
 * joining player is sent from those encoded entries.
 * <p>
 * Every joining player is sent an entry for every online player, each with a whole skin, so
 * encoding the entries again for every join costs work that grows with the square of the player
 * count during a burst of joins. An entry is encoded again whenever the player's skin, name, XUID,
 * unique ID or locator bar colour changes.
 * <p>
 * The packets are byte for byte the ones the codec would write: before the first use the codec is
 * checked to write a player list as the entry count followed by the entries one after another,
 * and when it does not, every packet is sent the regular way.
 */
@Slf4j
@ApiStatus.Internal
public final class PlayerListEncoder {
    private static final int PACKET_ID = NetworkConstants.CODEC.getPacketDefinition(PlayerListPacket.class).getId();
    private static final int LAYOUT_UNKNOWN = 0;
    private static final int LAYOUT_CONCATENATED = 1;
    private static final int LAYOUT_OTHER = -1;

    private static final ConcurrentHashMap<UUID, EncodedEntry> ENTRIES = new ConcurrentHashMap<>();
    private static volatile int layout = LAYOUT_UNKNOWN;

    private record EncodedEntry(SerializedSkin skin, long uniqueId, String name, String xuid, int color, ByteBuf bytes) {
        boolean matches(SerializedSkin skin, long uniqueId, String name, String xuid, int color) {
            return this.skin == skin && this.uniqueId == uniqueId && this.color == color
                && this.name.equals(name) && Objects.equals(this.xuid, xuid);
        }
    }

    private PlayerListEncoder() {
    }

    /**
     * Creates the player list entry of a player.
     *
     * @param player the player the entry describes
     * @return a new entry
     */
    public static PlayerListAddEntry addEntry(Player player) {
        final PlayerListAddEntry entry = new PlayerListAddEntry();
        entry.setUuid(player.getUniqueId());
        entry.setActorUniqueID(player.uniqueIdLong());
        entry.setPlayerName(player.getName());
        entry.setXblXUID(player.getXUID());
        entry.setPlatformOnlineID("");
        entry.setBuildPlatform(BuildPlatform.UNKNOWN);
        entry.setSerializedSkin(player.getSkin().toSerializedSkin());
        entry.setPlayerColor(player.getLocatorBarColor().getRGB());
        return entry;
    }

    /**
     * Sends {@code recipient} the player list entries of {@code players} from their encoded form,
     * at most {@code entriesPerPacket} entries per packet. Sends nothing when the encoded form
     * cannot be used, see {@link PlayerSessionHolder#canSendEncoded(Player)}, or an entry could not
     * be encoded.
     *
     * @return whether the entries were sent, otherwise the caller sends them the regular way
     */
    public static boolean trySend(Player recipient, Collection<Player> players, int entriesPerPacket) {
        if (layout == LAYOUT_OTHER || !PlayerSessionHolder.canSendEncoded(recipient)) {
            return false;
        }
        final ByteBuf[] entries = encodedEntries(players.toArray(Player.EMPTY_ARRAY));
        if (entries == null) {
            return false;
        }
        if (recipient.getSession().isConnected()) {
            for (int from = 0; from < entries.length; from += entriesPerPacket) {
                recipient.getSession().sendPacketImmediately(packet(entries, from, Math.min(entries.length, from + entriesPerPacket)));
            }
        }
        return true;
    }

    /**
     * Drops the encoded entry of a player that left.
     */
    public static void forget(UUID uuid) {
        ENTRIES.remove(uuid);
    }

    /**
     * @return the encoded entry of each player, or {@code null} when one could not be encoded or
     * the codec does not write player lists as concatenated entries
     */
    static ByteBuf @Nullable [] encodedEntries(Player[] players) {
        final ByteBuf[] entries = new ByteBuf[players.length];
        for (int i = 0; i < players.length; i++) {
            entries[i] = encodedEntry(players[i]);
            if (entries[i] == null) {
                return null;
            }
        }
        return entries;
    }

    private static @Nullable ByteBuf encodedEntry(Player player) {
        final SerializedSkin skin = player.getSkin().toSerializedSkin();
        final long uniqueId = player.uniqueIdLong();
        final String name = player.getName();
        final String xuid = player.getXUID();
        final int color = player.getLocatorBarColor().getRGB();
        final EncodedEntry cached = ENTRIES.get(player.getUniqueId());
        if (cached != null && cached.matches(skin, uniqueId, name, xuid, color)) {
            return cached.bytes();
        }

        final PlayerListAddEntry entry = addEntry(player);
        final ByteBuf body = encode(entry, 1);
        if (body == null) {
            return null;
        }
        final ByteBuf bytes;
        try {
            if (body.readableBytes() < 2 || body.getByte(0) != 1) {
                layout = LAYOUT_OTHER;
                return null;
            }
            bytes = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(body.slice(1, body.readableBytes() - 1)).asReadOnly());
        } finally {
            body.release();
        }
        if (layout == LAYOUT_UNKNOWN && !checkLayout(entry, bytes)) {
            return null;
        }
        ENTRIES.put(player.getUniqueId(), new EncodedEntry(skin, uniqueId, name, xuid, color, bytes));
        return bytes;
    }

    private static boolean checkLayout(PlayerListAddEntry entry, ByteBuf bytes) {
        final ByteBuf twice = encode(entry, 2);
        if (twice == null) {
            return false;
        }
        try {
            final int length = bytes.readableBytes();
            final boolean concatenated = twice.readableBytes() == 1 + 2 * length && twice.getByte(0) == 2
                && ByteBufUtil.equals(twice, 1, bytes, 0, length) && ByteBufUtil.equals(twice, 1 + length, bytes, 0, length);
            layout = concatenated ? LAYOUT_CONCATENATED : LAYOUT_OTHER;
            if (!concatenated) {
                log.debug("The codec does not write player lists as concatenated entries, sending them the regular way");
            }
            return concatenated;
        } finally {
            twice.release();
        }
    }

    private static @Nullable ByteBuf encode(PlayerListAddEntry entry, int copies) {
        final PlayerListPacket packet = new PlayerListPacket();
        for (int i = 0; i < copies; i++) {
            packet.getEntries().add(entry);
        }
        final BedrockCodecHelper helper = NetworkConstants.CODEC.createHelper();
        if (Server.getInstance().getSettings().debugSettings().disableEncodingLimits()) {
            helper.setEncodingSettings(EncodingSettings.UNLIMITED);
        }
        final ByteBuf buffer = Unpooled.buffer();
        try {
            NetworkConstants.CODEC.tryEncode(helper, buffer, packet);
            return buffer;
        } catch (Exception e) {
            buffer.release();
            log.debug("Could not encode the player list entry of {}, sending it the regular way", entry.getPlayerName(), e);
            return null;
        }
    }

    static UnknownPacket packet(ByteBuf[] entries, int from, int to) {
        final ByteBuf[] parts = new ByteBuf[to - from + 1];
        final ByteBuf count = Unpooled.buffer(5);
        VarInts.writeUnsignedInt(count, to - from);
        parts[0] = count;
        for (int i = from; i < to; i++) {
            parts[i - from + 1] = entries[i].duplicate();
        }
        final UnknownPacket packet = new UnknownPacket();
        packet.setPacketId(PACKET_ID);
        packet.setPayload(Unpooled.wrappedBuffer(parts));
        return packet;
    }
}

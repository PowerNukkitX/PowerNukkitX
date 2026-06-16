package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author CreeperFace
 * @since 30. 10. 2016
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BossEventPacket extends DataPacket {
    /* S2C: Shows the bossbar to the player. */
    public static final int TYPE_SHOW = 0;
    /* C2S: Registers a player to a boss fight. */
    public static final int TYPE_REGISTER_PLAYER = 1;
    public static final int TYPE_UPDATE = 1;
    /* S2C: Removes the bossbar from the client. */
    public static final int TYPE_HIDE = 2;
    /* C2S: Unregisters a player from a boss fight. */
    public static final int TYPE_UNREGISTER_PLAYER = 3;
    /* S2C: Sets the bar percentage. */
    public static final int TYPE_HEALTH_PERCENT = 4;
    /* S2C: Sets title of the bar. */
    public static final int TYPE_TITLE = 5;
    /* S2C: Update a player's bossbar properties. */
    public static final int TYPE_UPDATE_PROPERTIES = 6;
    public static final int TYPE_UNKNOWN_6 = TYPE_UPDATE_PROPERTIES;
    /* S2C: Sets color and overlay of the bar. */
    public static final int TYPE_TEXTURE = 7;
    /* S2C: Get a player's bossbar information. TODO: 2022/2/9 implement query packet. */
    public static final int TYPE_QUERY = 8;

    public long bossEid;
    public int type;
    public long playerEid;
    public float healthPercent;
    public String title = "";
    public String filteredName = "";
    public short darkenSky;
    public int color;
    public int overlay;
    
    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.bossEid = byteBuf.readEntityUniqueId();
        this.playerEid = byteBuf.readEntityUniqueId();
        this.type = byteBuf.readUnsignedByte();
        this.title = byteBuf.readString();
        this.filteredName = byteBuf.readString();
        this.healthPercent = byteBuf.readFloatLE();
        this.color = byteBuf.readUnsignedByte();
        this.overlay = byteBuf.readUnsignedByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeEntityUniqueId(this.bossEid);
        byteBuf.writeEntityUniqueId(this.playerEid);
        byteBuf.writeByte(this.type);
        byteBuf.writeString(this.title);
        byteBuf.writeString(this.filteredName);
        byteBuf.writeFloatLE(this.healthPercent);
        byteBuf.writeByte(this.color);
        byteBuf.writeByte(this.overlay);
    }

    @Override
    public int pid() {
        return ProtocolInfo.BOSS_EVENT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

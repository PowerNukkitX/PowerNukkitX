package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

/**
 * @author CreeperFace
 * @since 30. 10. 2016
 */
@ToString
public class BossEventPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.BOSS_EVENT_PACKET;

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
    public short unknown;
    public int color;
    public int overlay;
    
    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.bossEid = byteBuf.readEntityUniqueId();
        this.type = (int) byteBuf.readUnsignedVarInt();
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
            case TYPE_QUERY:
                this.playerEid = byteBuf.readEntityUniqueId();
                break;
            case TYPE_SHOW:
                this.title = byteBuf.readString();
                this.healthPercent = byteBuf.readFloatLE();
            case TYPE_UPDATE_PROPERTIES:
                this.unknown = (short) byteBuf.readShort();
            case TYPE_TEXTURE:
                this.color = (int) byteBuf.readUnsignedVarInt();
                this.overlay = (int) byteBuf.readUnsignedVarInt();
                break;
            case TYPE_HEALTH_PERCENT:
                this.healthPercent = byteBuf.readFloatLE();
                break;
            case TYPE_TITLE:
                this.title = byteBuf.readString();
                break;
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeEntityUniqueId(this.bossEid);
        byteBuf.writeUnsignedVarInt(this.type);
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
            case TYPE_QUERY:
                byteBuf.writeEntityUniqueId(this.playerEid);
                break;
            case TYPE_SHOW:
                byteBuf.writeString(this.title);
                byteBuf.writeFloatLE(this.healthPercent);
            case TYPE_UNKNOWN_6:
                byteBuf.writeShort(this.unknown);
            case TYPE_TEXTURE:
                byteBuf.writeUnsignedVarInt(this.color);
                byteBuf.writeUnsignedVarInt(this.overlay);
                break;
            case TYPE_HEALTH_PERCENT:
                byteBuf.writeFloatLE(this.healthPercent);
                break;
            case TYPE_TITLE:
                byteBuf.writeString(this.title);
                break;
        }
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

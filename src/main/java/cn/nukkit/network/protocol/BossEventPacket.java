package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * @author CreeperFace
 * @since 30. 10. 2016
 */
@ToString
public class BossEventPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BOSS_EVENT_PACKET;

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
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.bossEid = this.getEntityUniqueId();
        this.type = (int) this.getUnsignedVarInt();
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
            case TYPE_QUERY:
                this.playerEid = this.getEntityUniqueId();
                break;
            case TYPE_SHOW:
                this.title = this.getString();
                this.healthPercent = this.getLFloat();
            case TYPE_UPDATE_PROPERTIES:
                this.unknown = (short) this.getShort();
            case TYPE_TEXTURE:
                this.color = (int) this.getUnsignedVarInt();
                this.overlay = (int) this.getUnsignedVarInt();
                break;
            case TYPE_HEALTH_PERCENT:
                this.healthPercent = this.getLFloat();
                break;
            case TYPE_TITLE:
                this.title = this.getString();
                break;
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.bossEid);
        this.putUnsignedVarInt(this.type);
        switch (this.type) {
            case TYPE_REGISTER_PLAYER:
            case TYPE_UNREGISTER_PLAYER:
            case TYPE_QUERY:
                this.putEntityUniqueId(this.playerEid);
                break;
            case TYPE_SHOW:
                this.putString(this.title);
                this.putLFloat(this.healthPercent);
            case TYPE_UNKNOWN_6:
                this.putShort(this.unknown);
            case TYPE_TEXTURE:
                this.putUnsignedVarInt(this.color);
                this.putUnsignedVarInt(this.overlay);
                break;
            case TYPE_HEALTH_PERCENT:
                this.putLFloat(this.healthPercent);
                break;
            case TYPE_TITLE:
                this.putString(this.title);
                break;
        }
    }
}

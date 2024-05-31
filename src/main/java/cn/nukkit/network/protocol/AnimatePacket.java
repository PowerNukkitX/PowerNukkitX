package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnimatePacket extends DataPacket {

    public static final int $1 = ProtocolInfo.ANIMATE_PACKET;


    public long eid;
    public Action action;
    public float rowingTime;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.action = Action.fromId(byteBuf.readVarInt());
        this.eid = byteBuf.readEntityRuntimeId();
        if (this.action == Action.ROW_RIGHT || this.action == Action.ROW_LEFT) {
            this.rowingTime = byteBuf.readFloatLE();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeVarInt(this.action.getId());
        byteBuf.writeEntityRuntimeId(this.eid);
        if (this.action == Action.ROW_RIGHT || this.action == Action.ROW_LEFT) {
            byteBuf.writeFloatLE(this.rowingTime);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public enum Action {
        NO_ACTION(0),
        SWING_ARM(1),
        WAKE_UP(3),
        CRITICAL_HIT(4),
        MAGIC_CRITICAL_HIT(5),
        ROW_RIGHT(128),
        ROW_LEFT(129);

        private static final Int2ObjectMap<Action> ID_LOOKUP = new Int2ObjectOpenHashMap<>();

        static {
            for (Action value : values()) {
                ID_LOOKUP.put(value.id, value);
            }
        }

        private final int id;

        Action(int id) {
            this.id = id;
        }
    /**
     * @deprecated 
     */
    

        public int getId() {
            return id;
        }

        public static Action fromId(int id) {
            return ID_LOOKUP.get(id);
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

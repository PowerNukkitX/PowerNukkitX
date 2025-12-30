package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.SwingSource;
import cn.nukkit.utils.OptionalValue;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnimatePacket extends DataPacket {
    public long eid;
    public Action action;
    public float data;
    public SwingSource swingSource = SwingSource.NONE;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.action = Action.fromId(byteBuf.readByte());
        this.eid = byteBuf.readEntityRuntimeId();
        this.data = byteBuf.readFloatLE();
        this.swingSource = byteBuf.readOptional(SwingSource.NONE, () -> SwingSource.from(byteBuf.readString()));
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(this.action.getId());
        byteBuf.writeEntityRuntimeId(this.eid);
        byteBuf.writeFloatLE(this.data);
        byteBuf.writeOptional(OptionalValue.of(swingSource.getName()), byteBuf::writeString);
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

        public int getId() {
            return id;
        }

        public static Action fromId(int id) {
            return ID_LOOKUP.get(id);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.ANIMATE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

}

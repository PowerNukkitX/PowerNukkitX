package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.LessonAction;
import lombok.ToString;


@ToString
public class LessonProgressPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.LESSON_PROGRESS_PACKET;

    public LessonAction action;
    public int score;
    public String activityId;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.action = LessonAction.values()[byteBuf.readVarInt()];
        this.score = byteBuf.readVarInt();
        this.activityId = byteBuf.readString();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(action.ordinal());
        byteBuf.writeVarInt(score);
        byteBuf.writeString(activityId);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

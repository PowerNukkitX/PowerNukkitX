package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.LessonAction;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.LESSON_PROGRESS_PACKET;

    public LessonAction action;
    public int score;
    public String activityId;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.action = LessonAction.values()[byteBuf.readVarInt()];
        this.score = byteBuf.readVarInt();
        this.activityId = byteBuf.readString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeVarInt(action.ordinal());
        byteBuf.writeVarInt(score);
        byteBuf.writeString(activityId);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

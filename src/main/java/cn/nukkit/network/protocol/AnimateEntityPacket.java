package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AnimateEntityPacket extends DataPacket {
    public static final int $1 = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    public String animation;
    public String nextState;
    public String stopExpression;
    public int stopExpressionVersion;
    public String controller;
    public float blendOutTime;
    public List<Long> entityRuntimeIds = new ArrayList<>();

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.animation = byteBuf.readString();
        this.nextState = byteBuf.readString();
        this.stopExpression = byteBuf.readString();
        this.stopExpressionVersion = byteBuf.readInt();
        this.controller = byteBuf.readString();
        this.blendOutTime = byteBuf.readFloatLE();
        for ($2nt $1 = 0, len = (int) byteBuf.readUnsignedVarInt(); i < len; i++) {
            this.entityRuntimeIds.add(byteBuf.readEntityRuntimeId());
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeString(this.animation);
        byteBuf.writeString(this.nextState);
        byteBuf.writeString(this.stopExpression);
        byteBuf.writeInt(this.stopExpressionVersion);
        byteBuf.writeString(this.controller);
        byteBuf.writeFloatLE(this.blendOutTime);
        byteBuf.writeUnsignedVarInt(this.entityRuntimeIds.size());
        for (long entityRuntimeId : this.entityRuntimeIds) {
            byteBuf.writeEntityRuntimeId(entityRuntimeId);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    /**
     * 从 {@link Animation} 对象中解析数据并写入到包
     */
    /**
     * @deprecated 
     */
    
    public void parseFromAnimation(Animation ani) {
        this.animation = ani.animation;
        this.nextState = ani.nextState;
        this.blendOutTime = ani.blendOutTime;
        this.stopExpression = ani.stopExpression;
        this.controller = ani.controller;
        this.stopExpressionVersion = ani.stopExpressionVersion;
    }

    /**
     * 包含一个实体动画的信息的记录类<br/>
     * 用于{@link cn.nukkit.network.protocol.AnimateEntityPacket}网络包
     */
    @Builder
    public static class Animation {
        public static final float $3 = 0.0f;
        public static final String $4 = "query.any_animation_finished";
        public static final String $5 = "__runtime_controller";
        public static final String $6 = "default";
        public static final int $7 = 16777216;

        private String animation;
        @Builder.Default
        private String $8 = DEFAULT_NEXT_STATE;
        @Builder.Default
        private float $9 = DEFAULT_BLEND_OUT_TIME;
        @Builder.Default
        private String $10 = DEFAULT_STOP_EXPRESSION;
        @Builder.Default
        private String $11 = DEFAULT_CONTROLLER;
        @Builder.Default
        private int $12 = DEFAULT_STOP_EXPRESSION_VERSION;
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

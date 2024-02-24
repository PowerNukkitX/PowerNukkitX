package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */


@NoArgsConstructor(onConstructor = @__())
public class AnimateEntityPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    private String animation;
    private String nextState;
    private String stopExpression;
    private int stopExpressionVersion;
    private String controller;
    private float blendOutTime;
    private List<Long> entityRuntimeIds = new ArrayList<>();

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.animation = byteBuf.readString();
        this.nextState = byteBuf.readString();
        this.stopExpression = byteBuf.readString();
        this.stopExpressionVersion = byteBuf.readInt();
        this.controller = byteBuf.readString();
		this.blendOutTime = byteBuf.readFloatLE();
		for (int i = 0, len = (int) byteBuf.readUnsignedVarInt(); i < len; i++) {
			this.entityRuntimeIds.add(byteBuf.readEntityRuntimeId());
		}
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeString(this.animation);
		byteBuf.writeString(this.nextState);
        byteBuf.writeString(this.stopExpression);
        byteBuf.writeInt(this.stopExpressionVersion);
        byteBuf.writeString(this.controller);
		byteBuf.writeFloatLE(this.blendOutTime);
		byteBuf.writeUnsignedVarInt(this.entityRuntimeIds.size());
		for (long entityRuntimeId : this.entityRuntimeIds){
			byteBuf.writeEntityRuntimeId(entityRuntimeId);
		}
    }
    
    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public String getAnimation() {
        return this.animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getNextState() {
        return this.nextState;
    }

    public void setNextState(String nextState) {
        this.nextState = nextState;
    }

    public String getStopExpression() {
        return this.stopExpression;
    }

    public void setStopExpression(String stopExpression) {
        this.stopExpression = stopExpression;
    }

    public String getController() {
        return this.controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public float getBlendOutTime() {
        return this.blendOutTime;
    }

    public void setBlendOutTime(float blendOutTime) {
        this.blendOutTime = blendOutTime;
    }

    public List<Long> getEntityRuntimeIds() {
        return this.entityRuntimeIds;
    }

    public void setEntityRuntimeIds(List<Long> entityRuntimeIds) {
        this.entityRuntimeIds = entityRuntimeIds;
    }

    public int getStopExpressionVersion() {
        return stopExpressionVersion;
    }

    public void setStopExpressionVersion(int stopExpressionVersion) {
        this.stopExpressionVersion = stopExpressionVersion;
    }

    /**
     * 从 {@link Animation} 对象中解析数据并写入到包
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
        public static final float DEFAULT_BLEND_OUT_TIME = 0.0f;
        public static final String DEFAULT_STOP_EXPRESSION = "query.any_animation_finished";
        public static final String DEFAULT_CONTROLLER = "__runtime_controller";
        public static final String DEFAULT_NEXT_STATE = "default";
        public static final int DEFAULT_STOP_EXPRESSION_VERSION = 16777216;

        private String animation;
        @Builder.Default
        private String nextState = DEFAULT_NEXT_STATE;
        @Builder.Default
        private float blendOutTime = DEFAULT_BLEND_OUT_TIME;
        @Builder.Default
        private String stopExpression = DEFAULT_STOP_EXPRESSION;
        @Builder.Default
        private String controller = DEFAULT_CONTROLLER;
        @Builder.Default
        private int stopExpressionVersion = DEFAULT_STOP_EXPRESSION_VERSION;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}

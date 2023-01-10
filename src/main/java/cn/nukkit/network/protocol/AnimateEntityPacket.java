package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IWareQ
 */
@PowerNukkitOnly
@Since("1.5.1.0-PN")
@NoArgsConstructor(onConstructor = @__(@PowerNukkitOnly))
public class AnimateEntityPacket extends DataPacket {

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public static final byte NETWORK_ID = ProtocolInfo.ANIMATE_ENTITY_PACKET;

    private String animation;
    private String nextState;
    private String stopExpression;
    @PowerNukkitXOnly
    @Since("1.19.40-r1")
    private int stopExpressionVersion;
    private String controller;
    private float blendOutTime;
    private List<Long> entityRuntimeIds = new ArrayList<>();

    @Override
    public void decode() {
        this.animation = this.getString();
        this.nextState = this.getString();
        this.stopExpression = this.getString();
        this.stopExpressionVersion = this.getInt();
        this.controller = this.getString();
		this.blendOutTime = this.getLFloat();
		for (int i = 0, len = (int) this.getUnsignedVarInt(); i < len; i++) {
			this.entityRuntimeIds.add(this.getEntityRuntimeId());
		}
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.animation);
		this.putString(this.nextState);
        this.putString(this.stopExpression);
        this.putInt(this.stopExpressionVersion);
        this.putString(this.controller);
		this.putLFloat(this.blendOutTime);
		this.putUnsignedVarInt(this.entityRuntimeIds.size());
		for (long entityRuntimeId : this.entityRuntimeIds){
			this.putEntityRuntimeId(entityRuntimeId);
		}
    }
    
    @Override
    public byte pid() {
        return NETWORK_ID;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getAnimation() {
        return this.animation;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setAnimation(String animation) {
        this.animation = animation;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getNextState() {
        return this.nextState;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setNextState(String nextState) {
        this.nextState = nextState;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getStopExpression() {
        return this.stopExpression;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setStopExpression(String stopExpression) {
        this.stopExpression = stopExpression;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public String getController() {
        return this.controller;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setController(String controller) {
        this.controller = controller;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public float getBlendOutTime() {
        return this.blendOutTime;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setBlendOutTime(float blendOutTime) {
        this.blendOutTime = blendOutTime;
    }
    
    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public List<Long> getEntityRuntimeIds() {
        return this.entityRuntimeIds;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    public void setEntityRuntimeIds(List<Long> entityRuntimeIds) {
        this.entityRuntimeIds = entityRuntimeIds;
    }

    @PowerNukkitXOnly
    @Since("1.19.40-r1")
    public int getStopExpressionVersion() {
        return stopExpressionVersion;
    }

    @PowerNukkitXOnly
    @Since("1.19.40-r1")
    public void setStopExpressionVersion(int stopExpressionVersion) {
        this.stopExpressionVersion = stopExpressionVersion;
    }

    /**
     * 从 {@link Animation} 对象中解析数据并写入到包
     */
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
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
    @PowerNukkitXOnly
    @Since("1.19.50-r3")
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
}

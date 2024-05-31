package cn.nukkit.level.particle;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityDataMap;
import cn.nukkit.entity.data.EntityDataTypes;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.SerializedImage;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class FloatingTextParticle extends Particle {
    private static final Skin $1 = new Skin();
    private static final SerializedImage $2 = SerializedImage.fromLegacy(new byte[8192]);

    static {
        EMPTY_SKIN.setSkinData(SKIN_DATA);
        EMPTY_SKIN.generateSkinId("FloatingText");
    }

    protected final Level level;
    protected UUID $3 = UUID.randomUUID();
    protected long $4 = -1;
    protected boolean $5 = false;
    protected EntityDataMap $6 = new EntityDataMap();
    /**
     * @deprecated 
     */
    

    public FloatingTextParticle(Location location, String title) {
        this(location, title, null);
    }
    /**
     * @deprecated 
     */
    

    public FloatingTextParticle(Location location, String title, String text) {
        this(location.getLevel(), location, title, text);
    }
    /**
     * @deprecated 
     */
    

    public FloatingTextParticle(Vector3 pos, String title) {
        this(pos, title, null);
    }
    /**
     * @deprecated 
     */
    

    public FloatingTextParticle(Vector3 pos, String title, String text) {
        this(null, pos, title, text);
    }

    
    /**
     * @deprecated 
     */
    private FloatingTextParticle(Level level, Vector3 pos, String title, String text) {
        super(pos.x, pos.y, pos.z);
        this.level = level;

        entityData.setFlag(EntityFlag.NO_AI, true);
        entityData.put(Entity.LEASH_HOLDER, -1);
        entityData.put(Entity.SCALE, 0.01f); //zero causes problems on debug builds?
        entityData.put(Entity.HEIGHT, 0.01f);
        entityData.put(Entity.WIDTH, 0.01f);
        entityData.put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, (byte) 1);
        if (!Strings.isNullOrEmpty(title)) {
            entityData.put(Entity.NAME, title);
        }
        if (!Strings.isNullOrEmpty(text)) {
            entityData.put(Entity.SCORE, text);
        }
    }
    /**
     * @deprecated 
     */
    

    public String getText() {
        return entityData.get(Entity.SCORE);
    }
    /**
     * @deprecated 
     */
    

    public void setText(String text) {
        this.entityData.put(Entity.SCORE, text);
        sendentityData();
    }
    /**
     * @deprecated 
     */
    

    public String getTitle() {
        return entityData.get(Entity.NAME);
    }
    /**
     * @deprecated 
     */
    

    public void setTitle(String title) {
        this.entityData.put(Entity.NAME, title);
        sendentityData();
    }

    
    /**
     * @deprecated 
     */
    private void sendentityData() {
        if (level != null) {
            SetEntityDataPacket $7 = new SetEntityDataPacket();
            packet.eid = entityId;
            packet.entityData = entityData;
            level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        }
    }
    /**
     * @deprecated 
     */
    

    public boolean isInvisible() {
        return invisible;
    }
    /**
     * @deprecated 
     */
    

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }
    /**
     * @deprecated 
     */
    

    public void setInvisible() {
        this.setInvisible(true);
    }
    /**
     * @deprecated 
     */
    

    public long getEntityId() {
        return entityId;
    }

    @Override
    public DataPacket[] encode() {
        ArrayList<DataPacket> packets = new ArrayList<>();

        if (this.entityId == -1) {
            this.entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        } else {
            RemoveEntityPacket $8 = new RemoveEntityPacket();
            pk.eid = this.entityId;

            packets.add(pk);
        }

        if (!this.invisible) {
            PlayerListPacket.Entry[] entry = {
                    new PlayerListPacket.Entry(uuid, entityId, entityData.get(Entity.NAME), EMPTY_SKIN)
            };
            PlayerListPacket $9 = new PlayerListPacket();
            playerAdd.entries = entry;
            playerAdd.type = PlayerListPacket.TYPE_ADD;
            packets.add(playerAdd);

            AddPlayerPacket $10 = new AddPlayerPacket();
            pk.uuid = uuid;
            pk.username = "";
            pk.entityUniqueId = this.entityId;
            pk.entityRuntimeId = this.entityId;
            pk.x = (float) this.x;
            pk.y = (float) (this.y - 0.75);
            pk.z = (float) this.z;
            pk.speedX = 0;
            pk.speedY = 0;
            pk.speedZ = 0;
            pk.yaw = 0;
            pk.pitch = 0;
            pk.entityData = this.entityData;
            pk.item = Item.AIR;
            packets.add(pk);

            PlayerListPacket $11 = new PlayerListPacket();
            playerRemove.entries = entry;
            playerRemove.type = PlayerListPacket.TYPE_REMOVE;
            packets.add(playerRemove);
        }

        return packets.toArray(DataPacket.EMPTY_ARRAY);
    }
}

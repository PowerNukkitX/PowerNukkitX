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
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.PlayerListPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityDataPacket;
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
    private static final Skin EMPTY_SKIN = new Skin();
    private static final SerializedImage SKIN_DATA = SerializedImage.fromLegacy(new byte[4096]);

    static {
        EMPTY_SKIN.setSkinData(SKIN_DATA);
        EMPTY_SKIN.generateSkinId("FloatingText");
    }

    protected final Level level;
    protected UUID uuid = UUID.randomUUID();
    protected long entityId = -1;
    protected boolean invisible = false;
    protected EntityDataMap entityData = new EntityDataMap();

    public FloatingTextParticle(Location location, String title) {
        this(location, title, null);
    }

    public FloatingTextParticle(Location location, String title, String text) {
        this(location.getLevel(), location, title, text);
    }

    public FloatingTextParticle(Vector3 pos, String title) {
        this(pos, title, null);
    }

    public FloatingTextParticle(Vector3 pos, String title, String text) {
        this(null, pos, title, text);
    }

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

    public String getText() {
        return entityData.get(Entity.SCORE);
    }

    public void setText(String text) {
        this.entityData.put(Entity.SCORE, text);
        sendentityData();
    }

    public String getTitle() {
        return entityData.get(Entity.NAME);
    }

    public void setTitle(String title) {
        this.entityData.put(Entity.NAME, title);
        sendentityData();
    }

    private void sendentityData() {
        if (level != null) {
            SetEntityDataPacket packet = new SetEntityDataPacket();
            packet.eid = entityId;
            packet.entityData = entityData;
            level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        }
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public void setInvisible() {
        this.setInvisible(true);
    }

    public long getEntityId() {
        return entityId;
    }

    @Override
    public DataPacket[] encode() {
        ArrayList<DataPacket> packets = new ArrayList<>();

        if (this.entityId == -1) {
            this.entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        } else {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.entityId;

            packets.add(pk);
        }

        if (!this.invisible) {
            PlayerListPacket.Entry[] entry = {
                    new PlayerListPacket.Entry(uuid, entityId, entityData.get(Entity.NAME), EMPTY_SKIN)
            };
            PlayerListPacket playerAdd = new PlayerListPacket();
            playerAdd.entries = entry;
            playerAdd.type = PlayerListPacket.TYPE_ADD;
            packets.add(playerAdd);

            AddPlayerPacket pk = new AddPlayerPacket();
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

            PlayerListPacket playerRemove = new PlayerListPacket();
            playerRemove.entries = entry;
            playerRemove.type = PlayerListPacket.TYPE_REMOVE;
            packets.add(playerRemove);
        }

        return packets.toArray(DataPacket.EMPTY_ARRAY);
    }
}

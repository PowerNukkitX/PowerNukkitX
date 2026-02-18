package cn.nukkit.level.particle;

import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import com.google.common.base.Strings;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataMap;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes;
import org.cloudburstmc.protocol.bedrock.data.entity.EntityFlag;
import org.cloudburstmc.protocol.bedrock.packet.AddEntityPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveEntityPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetEntityDataPacket;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 * @since 2015/11/21
 */
public class FloatingTextParticle extends Particle {

    protected final Level level;
    protected long entityId = -1;
    protected boolean invisible = false;
    protected String title;
    protected String text;
    protected EntityDataMap metadata = new EntityDataMap();

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
        this.title = title;
        this.text = text;

        EnumMap<EntityFlag, Boolean> flags = EntityDataMap.flagsOf(EntityFlag.NO_AI, EntityFlag.CAN_SHOW_NAME);
        this.metadata.putFlags(flags);
        this.metadata.put(EntityDataTypes.LEASH_HOLDER, -1L);
        this.metadata.put(EntityDataTypes.NAMETAG_ALWAYS_SHOW, (byte) 1);
        this.metadata.put(EntityDataTypes.SCALE, 0.0001f); // Zero causes problems on debug builds?
        this.metadata.put(EntityDataTypes.HEIGHT, 0.01f);
        this.metadata.put(EntityDataTypes.WIDTH, 0.01f);

        updateNameTag();
    }

    public String getText() {
        return this.text == null ? "" : this.text;
    }

    public void setText(String text) {
        this.text = text;
        updateNameTag();
        sendMetadata();
    }

    public String getTitle() {
        return this.title == null ? "" : this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        updateNameTag();
        sendMetadata();
    }

    private void updateNameTag() {
        // Score tag only works on player
        boolean hasTitle = !Strings.isNullOrEmpty(this.title);
        boolean hasText = !Strings.isNullOrEmpty(this.text);
        String tag = "";
        if (hasTitle) {
            tag += this.title;
            if (hasText) {
                tag += "\n";
            }
        }
        if (hasText) {
            tag += this.text;
        }
        this.metadata.put(EntityDataTypes.NAME, tag);
    }

    private void sendMetadata() {
        if (this.level != null) {
            SetEntityDataPacket packet = new SetEntityDataPacket();
            packet.setRuntimeEntityId(entityId);
            packet.setMetadata(this.metadata);

            this.level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        }
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;

        if (this.level != null) {
            if (invisible) {
                this.level.addChunkPacket(getChunkX(), getChunkZ(), getRemovePacket());
            } else {
                this.level.addChunkPacket(getChunkX(), getChunkZ(), getAddPacket());
            }
        }
    }

    public void setInvisible() {
        this.setInvisible(true);
    }

    public long getEntityId() {
        return this.entityId;
    }

    @Override
    public BedrockPacket[] encode() {
        ArrayList<BedrockPacket> packets = new ArrayList<>();

        if (this.entityId == -1) {
            this.entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL);
        } else {
            packets.add(getRemovePacket());
        }

        if (!this.invisible) {
            packets.add(getAddPacket());
        }

        return packets.toArray(new BedrockPacket[0]);
    }

    private AddEntityPacket getAddPacket() {
        AddEntityPacket pk = new AddEntityPacket();
        pk.setIdentifier("minecraft:armor_stand");
        pk.setUniqueEntityId(this.entityId);
        pk.setRuntimeEntityId(this.entityId);
        pk.setPosition(Vector3f.from((float) this.x, (float) this.y - 0.75f, (float) this.z));
        pk.setMotion(Vector3f.from(0, 0, 0));
        pk.setRotation(Vector2f.from(0, 0));
        pk.setMetadata(this.metadata);
        return pk;
    }

    private RemoveEntityPacket getRemovePacket() {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.setUniqueEntityId(this.entityId);
        return pk;
    }
}

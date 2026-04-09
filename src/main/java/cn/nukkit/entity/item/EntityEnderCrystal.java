package cn.nukkit.entity.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.components.NameableComponent;
import cn.nukkit.entity.mob.EntityEnderDragon;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockVector3;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;

/**
 * @author PetteriM1
 */
public class EntityEnderCrystal extends Entity implements EntityExplosive {
    @Override
    @NotNull
    public String getIdentifier() {
        return ENDER_CRYSTAL;
    }


    /**
     * @since 1.2.1.0-PN
     */
    protected boolean detonated = false;

    public EntityEnderCrystal(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }


    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.containsKey("ShowBottom")) {
            this.setShowBase(this.namedTag.getBoolean("ShowBottom"));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag = this.namedTag.toBuilder().putBoolean("ShowBottom", this.showBase()).build();
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public NameableComponent getComponentNameable() {
        return DEFAULT_NOT_NAMEABLE;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (isClosed()) {
            return false;
        }

        if (source.getCause() == EntityDamageEvent.DamageCause.FIRE || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || source.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            return false;
        }

        if (!super.attack(source)) {
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) source).getDamager() instanceof EntityEnderDragon) {
                return false;
            }
        }
        explode();

        return true;
    }

    @Override
    public void explode() {
        if (!this.detonated) {
            this.detonated = true;

            Position pos = this.getPosition();
            Explosion explode = new Explosion(pos, 6, this);

            this.close();

            if (this.level.getGameRules().getBoolean(GameRule.MOB_GRIEFING)) {
                explode.explodeA();
                explode.explodeB();
            } else {
                explode.explodeB();
            }
        }
    }

    @Override
    public void close() {
        super.close();
        for (Entity entity : this.getLevel().getEntities()) {
            if (entity instanceof EntityEnderDragon dragon) {
                if (entity.distance(this) <= 28) {
                    entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 10));
                }
                dragon.getMemoryStorage().put(CoreMemoryTypes.LAST_ENDER_CRYSTAL_DESTROY, this.asBlockVector3());
            }
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public boolean showBase() {
        return this.getDataFlag(ActorFlags.SHOW_BOTTOM);
    }

    public void setShowBase(boolean value) {
        this.setDataFlag(ActorFlags.SHOW_BOTTOM, value);
    }

    public BlockVector3 getBeamTarget() {
        return BlockVector3.fromNetwork(this.getDataProperty(ActorDataTypes.BLOCK_TARGET));
    }

    public void setBeamTarget(BlockVector3 beamTarget) {
        this.setDataProperty(ActorDataTypes.BLOCK_TARGET, beamTarget.toNetwork());
    }

    @Override
    public String getOriginalName() {
        return "Ender Crystal";
    }
}

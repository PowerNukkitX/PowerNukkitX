package cn.nukkit.entity.projectile;

import cn.nukkit.entity.ClimateVariant;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.item.ItemEgg;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityEgg extends EntityProjectile implements ClimateVariant {
    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
        new EnumEntityProperty("minecraft:climate_variant", new String[]{
            "temperate",
            "warm",
            "cold"
        }, "temperate", true)
    };
    private final static String PROPERTY_STATE = "minecraft:climate_variant";

    @Override
    @NotNull public String getIdentifier() {
        return EGG;
    }

    public EntityEgg(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityEgg(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if(namedTag.containsString("variant")) {
            setVariant(Variant.get(namedTag.getString("variant")));
        } else setVariant(Variant.TEMPERATE);
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.03f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200 || this.isCollided) {
            this.kill();
            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    protected void addHitEffect() {
        int particles = ThreadLocalRandom.current().nextInt(10) + 5;
        ItemEgg egg = new ItemEgg();
        for (int i = 0; i < particles; i++) {
            level.addParticle(new ItemBreakParticle(this, egg));
        }
    }

    @Override
    public String getOriginalName() {
        return "Egg";
    }

    @Override
    protected void onCollideWithBlock(Position position, Vector3 motion) {
        super.onCollideWithBlock(position, motion);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int chicks = 0;
        if (random.nextInt(8) == 0) {
            chicks = 1;
            if (random.nextInt(32) == 0) {
                chicks = 4;
            }
        }
        if (chicks > 0) {
            for (int i = 0; i < chicks; i++) {
                CompoundTag nbt = Entity.getDefaultNBT(
                        this.add(0, 0.5, 0),
                        null,
                        0,
                        0
                );
                String variant = this.getVariant().getName();
                nbt.putString("variant", variant);
                Entity entity = Entity.createEntity(Entity.CHICKEN, this.level.getChunk((int)this.x >> 4, (int)this.z >> 4), nbt);
                if (entity != null) {
                    entity.setDataFlag(EntityFlag.BABY, true);
                    entity.setScale(0.5f);
                    entity.spawnToAll();
                }
            }
        }
    }
}

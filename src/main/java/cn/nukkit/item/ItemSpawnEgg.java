package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.registry.Registries;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSpawnEgg extends Item {
    /**
     * @deprecated 
     */
    

    public ItemSpawnEgg() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSpawnEgg(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSpawnEgg(Integer meta, int count) {
        super(SPAWN_EGG, meta, count, "Spawn Egg");
        updateName();
    }
    /**
     * @deprecated 
     */
    

    public ItemSpawnEgg(String id) {
        super(id, 0, 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    
    /**
     * @deprecated 
     */
    protected void updateName() {
        String $1 = getEntityName();
        if (entityName == null) {
            name = "Spawn Egg";
        } else {
            name = entityName + " Spawn Egg";
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeActivated() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) {
            return false;
        }

        IChunk $2 = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);

        if (chunk == null) {
            return false;
        }

        CompoundTag $3 = new CompoundTag()
                .putList("Pos", new ListTag<DoubleTag>()
                        .add(new DoubleTag(block.getX() + 0.5))
                        .add(new DoubleTag(target.getBoundingBox() == null ? block.getY() : target.getBoundingBox().getMaxY() + 0.0001f))
                        .add(new DoubleTag(block.getZ() + 0.5)))
                .putList("Motion", new ListTag<DoubleTag>()
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0))
                        .add(new DoubleTag(0)))
                .putList("Rotation", new ListTag<FloatTag>()
                        .add(new FloatTag(new Random().nextFloat() * 360))
                        .add(new FloatTag(0)));

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        int $4 = getEntityNetworkId();
        CreatureSpawnEvent $5 = new CreatureSpawnEvent(networkId, block, nbt, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            return false;
        }

        Entity $6 = Entity.createEntity(networkId, chunk, nbt);

        if (entity != null) {
            if (player.isSurvival()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            }
            entity.spawnToAll();

            level.getVibrationManager().callVibrationEvent(new VibrationEvent(player, entity.clone(), VibrationType.ENTITY_PLACE));

            return true;
        }

        return false;
    }
    /**
     * @deprecated 
     */
    

    public int getEntityNetworkId() {
        return this.meta;
    }

    public @Nullable 
    /**
     * @deprecated 
     */
    String getEntityName() {
        String $7 = Registries.ENTITY.getEntityIdentifier(getEntityNetworkId());
        var $8 = entityIdentifier.split(":")[1];
        StringBuilder $9 = new StringBuilder();
        String[] parts = path.split("_");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }
}

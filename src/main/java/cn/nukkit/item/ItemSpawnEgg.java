package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;

import javax.annotation.Nullable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSpawnEgg extends Item {

    public ItemSpawnEgg() {
        this(0, 1);
    }

    public ItemSpawnEgg(Integer meta) {
        this(meta, 1);
    }

    public ItemSpawnEgg(Integer meta, int count) {
        super(SPAWN_EGG, meta, count, "Spawn Egg");
        updateName();
    }

    public ItemSpawnEgg(String id) {
        super(id, 0, 1);
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta);
        updateName();
    }

    protected void updateName() {
        String entityName = getEntityName();
        if (entityName == null) {
            name = "Spawn Egg";
        } else {
            name = entityName + " Spawn Egg";
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player.isAdventure()) return false;

        IChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);
        if (chunk == null) return false;

        double spawnY = (target.getBoundingBox() == null) ? block.getY() : target.getBoundingBox().getMaxY() + 0.0001d;
        float yaw = java.util.concurrent.ThreadLocalRandom.current().nextFloat() * 360f;
        Location loc = new Location(block.getX() + 0.5, spawnY, block.getZ() + 0.5, yaw, 0f, level);

        CompoundTag nbt = Entity.getDefaultNBT(loc);
        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        int networkId = getEntityNetworkId();
        CreatureSpawnEvent ev = new CreatureSpawnEvent(networkId, block, nbt, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) return false;

        Entity entity = Entity.createEntity(networkId, chunk, nbt);
        if (entity == null) return false;

        if (player.isSurvival()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        entity.spawnToAll();
        level.getVibrationManager().callVibrationEvent(
                new VibrationEvent(player, entity.clone(), VibrationType.ENTITY_PLACE)
        );
        return true;
    }

    public int getEntityNetworkId() {
        return this.meta;
    }

    public @Nullable String getEntityName() {
        String entityIdentifier = Registries.ENTITY.getEntityIdentifier(getEntityNetworkId());
        var path = entityIdentifier.split(":")[1];
        StringBuilder result = new StringBuilder();
        String[] parts = path.split("_");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }
}

package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.entity.CreatureSpawnEvent;
import org.powernukkitx.event.entity.CreatureSpawnEvent.SpawnReason;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Location;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.registry.Registries;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSpawnEgg extends Item implements SpawnEggPickable {

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

    protected CompoundTag entityNBT;

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

        double spawnY = (target.getBoundingBox() == null) ? block.getY() : target.getBoundingBox().getMaxY() + 0.0001d;
        double spawnX = target.getX() + fx;
        double spawnZ = target.getZ() + fz;
        Location loc = new Location(spawnX, spawnY, spawnZ, 0f, 0f, level).setYawFacing(player);

        IChunk chunk = level.getChunk((int) Math.floor(loc.getX()) >> 4, (int) Math.floor(loc.getZ()) >> 4);
        if (chunk == null) return false;

        CompoundTag nbt = Entity.getDefaultNBT(loc);
        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        if (this.entityNBT != null) {
            this.entityNBT.putList("Pos", nbt.getList("Pos", DoubleTag.class));
            this.entityNBT.putList("Motion", nbt.getList("Motion", DoubleTag.class));
            this.entityNBT.putList("Rotation", nbt.getList("Rotation", FloatTag.class));
            nbt = this.entityNBT;
        }

        int networkId = getEntityNetworkId();
        CreatureSpawnEvent ev = new CreatureSpawnEvent(networkId, block, nbt, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) return false;

        Entity entity = Entity.createEntity(networkId, chunk, nbt);
        if (entity == null) return false;

        if (entity.isAgeable() && ThreadLocalRandom.current().nextInt(6) == 0) {
            entity.setBaby(true);
        }

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
        if (entityIdentifier == null) {
            return null;
        }
        String[] split = entityIdentifier.split(":");
        var path = split.length > 1 ? split[1] : split[0];
        StringBuilder result = new StringBuilder();
        String[] parts = path.split("_");
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }

    @Override
    public void setEntityNBT(CompoundTag entityNBT) {
    }
}
package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityFakeInventory;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EntityFakeBlock implements FakeBlock {
    private final FakeInventory inventory;
    private final Map<Player, EntityFakeInventory> entities = new HashMap<>();

    public EntityFakeBlock(FakeInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void create(Player player) {
        create(player, inventory.getTitle());
    }

    @Override
    public void create(Player player, String titleName) {
        EntityFakeInventory existing = entities.get(player);
        if (existing != null && !existing.isClosed()) {
            return;
        }

        Location loc = player.getLocation();

        CompoundTag nbt = Entity.getDefaultNBT(loc)
                .putInt("ContainerSize", inventory.getSize())
                .putString("displayName", titleName);

        Entity raw = Entity.createEntity(EntityID.FAKE_INVENTORY, player.getChunk(), nbt);
        if (!(raw instanceof EntityFakeInventory fake)) {
            Server.getInstance().getLogger().error("[FakeInventory] Failed to create EntityFakeInventory");
            return;
        }

        entities.put(player, fake);
        fake.spawnTo(player);
    }

    @Override
    public long getEntityId(Player player) {
        EntityFakeInventory fake = entities.get(player);
        return (fake != null && !fake.isClosed()) ? fake.getId() : 0L;
    }

    @Override
    public void remove(Player player) {
        EntityFakeInventory fake = entities.remove(player);
        if (fake != null && !fake.isClosed()) {
            fake.close();
        }
    }

    @Override
    public HashSet<Vector3> getLastPositions(Player player) {
        HashSet<Vector3> set = new HashSet<>();
        EntityFakeInventory fake = entities.get(player);
        if (fake != null && !fake.isClosed()) {
            set.add(new Vector3(fake.getFloorX(), fake.getFloorY(), fake.getFloorZ()));
        }
        return set;
    }
}

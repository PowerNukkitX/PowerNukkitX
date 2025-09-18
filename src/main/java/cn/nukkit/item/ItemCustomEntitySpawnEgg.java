package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.CreatureSpawnEvent;
import cn.nukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

public class ItemCustomEntitySpawnEgg extends Item {
    private static final String SUFFIX = "_spawn_egg";
    private static final String PLACEHOLDER_ID = "pnx:auto_spawn_egg";

    private static final String ROOT_PNX_EXTRA  = "pnx_extra_data";
    private static final String COMP_CUSTOM_EGG = "custom_entity_spawn_egg";
    private static final String KEY_EGG_ID      = "egg_identifier";
    private static final String KEY_ENTITY_ID   = "entity_identifier";

    public ItemCustomEntitySpawnEgg() {
        super(PLACEHOLDER_ID, 0, 1);
        selfHealIdentifierFromNamedTag();
    }

    /** Build a full client definition for a spawn egg id */
    public static CustomItemDefinition buildEggDefinition(String eggId) {
        final String entityId = entityIdFromEggId(eggId);

        CompoundTag nbt = buildEggDefinitionNbtStatic(eggId, entityId);
        return new CustomItemDefinition(eggId, nbt);
    }

    /** Build definition from this instance’s resolved id. */
    public CustomItemDefinition buildDefinition() {
        String eggId = getEggId();
        if (eggId == null || eggId.isEmpty()) eggId = PLACEHOLDER_ID;

        final String entityId = entityIdFromEggId(eggId);

        CompoundTag nbt = buildEggDefinitionNbtStatic(eggId, entityId);
        return new CustomItemDefinition(eggId, nbt);
    }

    /** Static NBT builder. */
    private static CompoundTag buildEggDefinitionNbtStatic(String eggId, @Nullable String entityId) {
        CompoundTag root = new CompoundTag();
        CompoundTag pnxExtra = new CompoundTag();
        CompoundTag eggComp  = new CompoundTag();
        eggComp.putString(KEY_EGG_ID, eggId);
        if (entityId != null && !entityId.isEmpty()) {
            eggComp.putString(KEY_ENTITY_ID, entityId);
        }
        pnxExtra.putCompound(COMP_CUSTOM_EGG, eggComp);
        root.putCompound(ROOT_PNX_EXTRA, pnxExtra);

        return root;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (player != null && player.isAdventure()) return false;
        selfHealIdentifierFromNamedTag();

        String entityId = entityIdFromEggId(this.getId());
        if (entityId == null || entityId.isEmpty()) {
            if (player != null) player.sendMessage("§cInvalid spawn egg.");
            return false;
        }

        IChunk chunk = level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4);
        if (chunk == null) return false;

        double spawnY = (target.getBoundingBox() == null) ? block.getY() : target.getBoundingBox().getMaxY() + 0.0001d;
        float yaw = ThreadLocalRandom.current().nextFloat() * 360f;
        Location loc = new Location(block.getX() + 0.5, spawnY, block.getZ() + 0.5, yaw, 0f, level);
        CompoundTag nbt = Entity.getDefaultNBT(loc);

        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName());
        }

        int networkId = Registries.ENTITY.getEntityNetworkId(entityId);
        var ev = new CreatureSpawnEvent(networkId, block, nbt, SpawnReason.SPAWN_EGG);
        level.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) return false;

        Entity entity = Registries.ENTITY.provideEntity(entityId, chunk, nbt);
        if (entity == null) {
            if (player != null) player.sendMessage("§cCould not create entity: §e" + entityId);
            return false;
        }

        if (player != null && player.isSurvival()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
        }

        entity.spawnToAll();

        if (player != null) {
            level.getVibrationManager().callVibrationEvent(
                    new VibrationEvent(player, entity.clone(), VibrationType.ENTITY_PLACE)
            );
        }
        return true;
    }

    @Override
    public Item setNamedTag(CompoundTag tag) {
        Item out = super.setNamedTag(tag);
        selfHealIdentifierFromNamedTag();
        return out;
    }

    @Override
    public Item clone() {
        Item c = super.clone();
        if (c instanceof ItemCustomEntitySpawnEgg egg) {
            egg.selfHealIdentifierFromNamedTag();
        }
        return c;
    }


    // ---------- helpers ----------
    private void selfHealIdentifierFromNamedTag() {
        CompoundTag tag = this.getNamedTag();
        if (tag == null) return;
        CompoundTag pnxExtra = tag.getCompound(ROOT_PNX_EXTRA);
        if (pnxExtra == null) return;
        CompoundTag customEgg = pnxExtra.getCompound(COMP_CUSTOM_EGG);
        if (customEgg == null) return;
        String eggId = customEgg.getString(KEY_EGG_ID);
        if (eggId != null && !eggId.isEmpty() && !eggId.equals(super.getId())) {
            setItemIdReflect(this, eggId);
        }
    }

    private static void setItemIdReflect(Item item, String id) {
        try {
            Field f = Item.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(item, id);
        } catch (Throwable ignored) { }
    }

    public static @Nullable String entityIdFromEggId(String eggId) {
        if (eggId == null) return null;
        int colon = eggId.indexOf(':');
        if (colon < 0) return null;

        String ns = eggId.substring(0, colon);
        String path = eggId.substring(colon + 1);
        if (!path.endsWith(SUFFIX)) return null;

        String entityPath = path.substring(0, path.length() - SUFFIX.length());
        if (entityPath.isEmpty()) return null;

        return ns + ":" + entityPath;
    }

    /** Returns the real item id for this egg (e.g. "namespace:custom_zombie_spawn_egg"), or null if unavailable. */
    public String getEggId() {
        String raw = getRawItemIdNoHook();
        return (raw != null && !raw.isEmpty() && !raw.equals(PLACEHOLDER_ID) && entityIdFromEggId(raw) != null) ? raw : null;
    }

    private String getRawItemIdNoHook() {
        try {
            Field f = Item.class.getDeclaredField("id");
            f.setAccessible(true);
            Object v = f.get(this);
            return v instanceof String ? (String) v : null;
        } catch (Throwable ignore) {
            return null;
        }
    }

    /** Bind the resolved item id to this egg and seed PNX extra data. */
    public void resolveSpawnEgg(String resolvedId) {
        setItemIdReflect(this, resolvedId);

        CompoundTag root = this.getOrCreateNamedTag();
        CompoundTag pnx  = root.getCompound(ROOT_PNX_EXTRA);
        if (pnx == null) {
            pnx = new CompoundTag();
            root.putCompound(ROOT_PNX_EXTRA, pnx);
        }
        CompoundTag ce = pnx.getCompound(COMP_CUSTOM_EGG);
        if (ce == null) {
            ce = new CompoundTag();
            pnx.putCompound(COMP_CUSTOM_EGG, ce);
        }
        ce.putString(KEY_EGG_ID, resolvedId);

        String ent = entityIdFromEggId(resolvedId);
        if (ent != null) {
            ce.putString(KEY_ENTITY_ID, ent);
        }
    }
}

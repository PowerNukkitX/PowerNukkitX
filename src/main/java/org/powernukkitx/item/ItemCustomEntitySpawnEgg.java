package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.entity.CreatureSpawnEvent;
import org.powernukkitx.event.entity.CreatureSpawnEvent.SpawnReason;
import org.powernukkitx.item.customitem.CustomItemDefinition;
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
import org.powernukkitx.utils.Identifier;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
public class ItemCustomEntitySpawnEgg extends Item implements SpawnEggPickable {
    private static final String SUFFIX = "_spawn_egg";
    private static final String PLACEHOLDER_ID = "pnx:auto_spawn_egg";

    private static final String ROOT_PNX_EXTRA  = "pnx_extra_data";
    private static final String COMP_CUSTOM_EGG = "custom_entity_spawn_egg";
    private static final String KEY_EGG_ID      = "egg_identifier";
    private static final String KEY_ENTITY_ID   = "entity_identifier";

    private CompoundTag entityNBT;

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

        double spawnY = (target.getBoundingBox() == null) ? block.getY() : target.getBoundingBox().getMaxY() + 0.0001d;
        double spawnX = target.getX() + fx;
        double spawnZ = target.getZ() + fz;
        Location loc = new Location(spawnX, spawnY, spawnZ, 0f, 0f, level);

        if (player != null) {
            loc.setYawFacing(player);
        } else {
            loc.setYaw(ThreadLocalRandom.current().nextFloat() * 360f);
        }

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
    public Item setNbt(CompoundTag tag) {
        Item out = super.setNbt(tag);
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

    @Override
    protected boolean isCreativeTagEmpty() {
        return true;
    }

    @Override
    protected boolean isCreativeBlockDefinitionEmpty() {
        return true;
    }


    // ---------- helpers ----------
    private void selfHealIdentifierFromNamedTag() {
        CompoundTag tag = this.getNbt();
        if (tag == null) return;

        CompoundTag pnxExtra = tag.getCompound(ROOT_PNX_EXTRA);
        if (pnxExtra == null) return;

        CompoundTag customEgg = pnxExtra.getCompound(COMP_CUSTOM_EGG);
        if (customEgg == null) return;

        String eggId = customEgg.getString(KEY_EGG_ID);
        if (eggId != null && !eggId.isEmpty() && !eggId.equals(super.getId())) {
            this.id = eggId.intern();
            this.identifier = new Identifier(eggId);
            this.name = null;
        }
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
        if (resolvedId == null || resolvedId.isBlank()) {
            return;
        }

        String entityId = entityIdFromEggId(resolvedId);

        this.id = resolvedId.intern();
        this.identifier = new Identifier(resolvedId);
        this.name = null;

        CompoundTag tag = this.getNbt();
        if (tag == null) {
            tag = new CompoundTag();
        }

        CompoundTag pnxExtra = tag.getCompound(ROOT_PNX_EXTRA);
        if (pnxExtra == null) {
            pnxExtra = new CompoundTag();
        }

        CompoundTag customEgg = pnxExtra.getCompound(COMP_CUSTOM_EGG);
        if (customEgg == null) {
            customEgg = new CompoundTag();
        }

        customEgg.putString(KEY_EGG_ID, resolvedId);
        if (entityId != null && !entityId.isEmpty()) {
            customEgg.putString(KEY_ENTITY_ID, entityId);
        }

        pnxExtra.putCompound(COMP_CUSTOM_EGG, customEgg);
        tag.putCompound(ROOT_PNX_EXTRA, pnxExtra);

        super.setNbt(tag);
    }

    @Override
    public void setEntityNBT(CompoundTag entityNBT) {
        this.entityNBT = entityNBT;
    }
}
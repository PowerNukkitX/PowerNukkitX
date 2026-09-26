package org.powernukkitx.entity.item;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBarrier;
import org.powernukkitx.block.BlockCactus;
import org.powernukkitx.block.BlockCampfire;
import org.powernukkitx.block.BlockCopperGrate;
import org.powernukkitx.block.BlockFarmland;
import org.powernukkitx.block.BlockGlowstone;
import org.powernukkitx.block.BlockGrassPath;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockIce;
import org.powernukkitx.block.BlockLava;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockSlime;
import org.powernukkitx.block.BlockTnt;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.entity.projectile.EntityEnderPearl;
import org.powernukkitx.event.entity.EntityDamageByChildEntityEvent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityDamageEvent.DamageCause;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemCushion;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.DestroyBlockParticle;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A dyed seat that a player can sit on. The client only knows it as an entity, there is no cushion block.
 */
public class EntityCushion extends EntityVehicle {
    private static final String TAG_VARIANT = "Variant";
    private static final String TAG_LEGACY_COLOR = "Color";

    private static final float WIDTH = 0.999f;
    private static final float HEIGHT = 0.249f;

    /**
     * How far below a cushion its supporting block is looked for.
     */
    private static final double SUPPORT_PROBE = 1.0 / 64;

    /**
     * Vanilla revalidates a cushion every 80 to 120 ticks instead of every tick.
     */
    private static final int SURVIVAL_CHECK_MIN_DELAY = 80;
    private static final int SURVIVAL_CHECK_DELAY_SPREAD = 41;

    private static final float LAVA_DAMAGE = 4f;

    /**
     * Vanilla offset of a player rider, added to the 0.1875 seat height of the cushion.
     */
    private static final Vector3f MOUNTED_OFFSET = new Vector3f(0f, 1.22001f, 0f);

    private static final RideableComponent RIDEABLE = new RideableComponent(
        0,
        true,
        RideableComponent.DismountMode.ON_TOP_CENTER,
        Set.of(),
        "action.interact.ride.cushion",
        1.375f,
        false,
        true,
        1,
        List.of(new RideableComponent.Seat(0, 1, new Vector3f(0f, 0.1875f, 0f), null, -90f, null, null))
    );

    // No initializer: initEntity() runs from the super constructor, an initializer would reset the loaded color afterwards
    private DyeColor color;
    private int survivalCheckDelay;

    public EntityCushion(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

        this.setHealthMax(1);
        this.setHealthCurrent(1);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return CUSHION;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        final CompoundTag nbtMap = this.getNbt();
        if (nbtMap.contains(TAG_VARIANT)) {
            this.color = DyeColor.getByDyeData(nbtMap.getInt(TAG_VARIANT) & 0xf);
        } else if (nbtMap.contains(TAG_LEGACY_COLOR)) {
            this.color = DyeColor.getByWoolData(nbtMap.getByte(TAG_LEGACY_COLOR));
        } else {
            this.color = DyeColor.WHITE;
        }

        this.setImmobile(true);
        this.setDataFlag(ActorFlags.COLLIDABLE);
        this.actorDataMap.put(ActorDataTypes.VARIANT, this.color.getDyeData());
    }

    @Override
    public Vector3f getMountedOffset(Entity passenger) {
        return MOUNTED_OFFSET;
    }

    @Override
    public float getHeight() {
        return HEIGHT;
    }

    @Override
    public float getWidth() {
        return WIDTH;
    }

    @Override
    protected float getDefaultGravity() {
        return 0f;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
        this.setDataProperty(ActorDataTypes.VARIANT, color.getDyeData());
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        return RIDEABLE;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.isSneaking() || !this.passengers.isEmpty()) {
            return false;
        }

        if (!mountEntity(player, true)) {
            return false;
        }

        this.level.addLevelSoundEvent(this, SoundEvent.MOUNT, -1, CUSHION, false, false);
        return true;
    }

    @Override
    public boolean canPassThrough() {
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {
            this.close();
            return false;
        }

        if (this.level != null) {
            if (hurtByBlocks()) {
                return true;
            }

            if (--this.survivalCheckDelay <= 0) {
                this.survivalCheckDelay = SURVIVAL_CHECK_MIN_DELAY + ThreadLocalRandom.current().nextInt(SURVIVAL_CHECK_DELAY_SPREAD);

                if (!canSurvive()) {
                    this.kill();
                    return true;
                }
            }
        }

        return super.onUpdate(currentTick);
    }

    /**
     * Hurts the cushion in lava or a lit campfire. Fire and magma blocks do not hurt it.
     */
    private boolean hurtByBlocks() {
        AxisAlignedBB box = this.getBoundingBox().shrink(1e-4, 1e-4, 1e-4);
        for (int x = (int) Math.floor(box.getMinX()); x <= (int) Math.floor(box.getMaxX()); x++) {
            for (int y = (int) Math.floor(box.getMinY()); y <= (int) Math.floor(box.getMaxY()); y++) {
                for (int z = (int) Math.floor(box.getMinZ()); z <= (int) Math.floor(box.getMaxZ()); z++) {
                    Block block = this.level.getBlock(x, y, z);
                    if (block instanceof BlockLava) {
                        this.attack(new EntityDamageEvent(this, DamageCause.LAVA, LAVA_DAMAGE));
                        return true;
                    }

                    if (block instanceof BlockCampfire campfire && !campfire.isExtinguished()) {
                        this.attack(new EntityDamageEvent(this, DamageCause.FIRE, 1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * A cushion survives while the block under it reaches it and it is not fully covered by suffocating blocks.
     */
    private boolean canSurvive() {
        if (!isSupported()) {
            return false;
        }

        Block low = this.level.getBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ());
        Block high = this.level.getBlock(this.getFloorX(), (int) Math.floor(this.y + HEIGHT), this.getFloorZ());
        return !covers(low) || !covers(high);
    }

    private boolean isSupported() {
        double probeY = this.y - SUPPORT_PROBE;
        Block below = this.level.getBlock(this.getFloorX(), (int) Math.floor(probeY), this.getFloorZ());
        if (below instanceof BlockLiquid) {
            return false;
        }

        AxisAlignedBB collision = below.getCollisionBoundingBox();
        if (collision == null) {
            return false;
        }

        double halfWidth = WIDTH / 2;
        double halfHeight = HEIGHT / 2;
        AxisAlignedBB probe = new SimpleAxisAlignedBB(
            this.x - halfWidth, probeY - halfHeight, this.z - halfWidth,
            this.x + halfWidth, probeY + halfHeight, this.z + halfWidth
        );
        return collision.intersectsWith(probe);
    }

    /**
     * Whether a block covers a cushion inside it: suffocating blocks, with the vanilla exceptions.
     */
    private static boolean covers(Block block) {
        if (block instanceof BlockGlowstone || block instanceof BlockIce || block instanceof BlockTnt) {
            return false;
        }

        if (block instanceof BlockSlime || block instanceof BlockBarrier || block instanceof BlockCopperGrate
            || block instanceof BlockCactus || block instanceof BlockFarmland || block instanceof BlockGrassPath) {
            return true;
        }

        return block.isSolid() && !block.isTransparent();
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == DamageCause.VOID) {
            this.close();
            return true;
        }

        if (source instanceof EntityDamageByChildEntityEvent byChild && byChild.getChild() instanceof EntityEnderPearl) {
            return false;
        }

        if (source instanceof EntityDamageByEntityEvent byEntity && byEntity.getDamager() instanceof Player player && player.isAdventure()) {
            return false;
        }

        if (!super.attack(source)) {
            return false;
        }

        // any damage breaks a cushion
        if (this.isAlive()) {
            this.kill();
        }
        return true;
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }

        super.kill();

        if (this.level == null) {
            return;
        }

        Vector3 centre = this.add(0, HEIGHT / 2, 0);
        this.level.addParticle(new DestroyBlockParticle(centre, Block.get(getWoolId())));
        this.level.addLevelSoundEvent(centre, SoundEvent.DEATH, -1, CUSHION, false, false);

        if (this.lastDamageCause instanceof EntityDamageByEntityEvent damageByEntity) {
            Entity damager = damageByEntity.getDamager();

            if (damager instanceof Player player && player.isCreative()) {
                return;
            }
        }

        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            Item drop = ItemCushion.of(this.color);
            if (this.hasCustomName()) {
                drop.setCustomName(this.getNameTag());
            }
            this.level.dropItem(this, drop);
        }
    }

    private String getWoolId() {
        return switch (this.color) {
            case WHITE -> BlockID.WHITE_WOOL;
            case ORANGE -> BlockID.ORANGE_WOOL;
            case MAGENTA -> BlockID.MAGENTA_WOOL;
            case LIGHT_BLUE -> BlockID.LIGHT_BLUE_WOOL;
            case YELLOW -> BlockID.YELLOW_WOOL;
            case LIME -> BlockID.LIME_WOOL;
            case PINK -> BlockID.PINK_WOOL;
            case GRAY -> BlockID.GRAY_WOOL;
            case LIGHT_GRAY -> BlockID.LIGHT_GRAY_WOOL;
            case CYAN -> BlockID.CYAN_WOOL;
            case PURPLE -> BlockID.PURPLE_WOOL;
            case BLUE -> BlockID.BLUE_WOOL;
            case BROWN -> BlockID.BROWN_WOOL;
            case GREEN -> BlockID.GREEN_WOOL;
            case RED -> BlockID.RED_WOOL;
            case BLACK -> BlockID.BLACK_WOOL;
        };
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.remove(TAG_LEGACY_COLOR);
        this.nbt.putInt(TAG_VARIANT, this.color.getDyeData());
    }

    @Override
    public String getOriginalName() {
        return "Cushion";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("cushion", "inanimate", "actor");
    }
}

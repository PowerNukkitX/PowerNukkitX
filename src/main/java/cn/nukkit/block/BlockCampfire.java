package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.item.EntitySplashPotion;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntitySmallFireball;
import cn.nukkit.event.entity.EntityCombustByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.CampfireInventory;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.recipe.CampfireRecipe;
import cn.nukkit.utils.Faceable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.EXTINGUISHED;


@Slf4j
public class BlockCampfire extends BlockTransparent implements Faceable, BlockEntityHolder<BlockEntityCampfire> {
    public static final BlockProperties $1 = new BlockProperties(CAMPFIRE, EXTINGUISHED, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);
    /**
     * @deprecated 
     */
    

    public BlockCampfire() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCampfire(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
     public String getBlockEntityType() {
        return BlockEntity.CAMPFIRE;
    }

    @Override
    @NotNull public Class<? extends BlockEntityCampfire> getBlockEntityClass() {
        return BlockEntityCampfire.class;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return isExtinguished() ? 0 : 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(ItemID.CHARCOAL, 0, 2)};
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (down().getProperties() == PROPERTIES) {
            return false;
        }

        final Block $2 = level.getBlock(this, 0);
        final Block $3 = level.getBlock(this, 1);

        setBlockFace(player != null ? player.getDirection().getOpposite() : null);
        boolean $4 = (block instanceof BlockFlowingWater w && w.isSourceOrFlowingDown()) || block instanceof BlockFrostedIce;
        boolean $5 = (layer1 instanceof BlockFlowingWater w && w.isSourceOrFlowingDown()) || layer1 instanceof BlockFrostedIce;
        if (defaultLayerCheck || layer1Check) {
            setExtinguished(true);
            this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f);
            this.level.setBlock(this, 1, defaultLayerCheck ? block : layer1, false, false);
        } else {
            this.level.setBlock(this, 1, Block.get(BlockID.AIR), false, false);
        }

        this.level.setBlock(block, this, true, true);
        try {
            CompoundTag $6 = new CompoundTag();

            if (item.hasCustomBlockData()) {
                Map<String, Tag> customData = item.getCustomBlockData().getTags();
                for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                    nbt.put(tag.getKey(), tag.getValue());
                }
            }

            createBlockEntity(nbt);
        } catch (Exception e) {
            log.warn("Failed to create the block entity {} at {}", getBlockEntityType(), getLocation(), e);
            level.setBlock(layer0, 0, layer0, true);
            level.setBlock(layer1, 0, layer1, true);
            return false;
        }

        this.level.updateAround(this);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        if (isExtinguished()) {
            if (entity.isOnFire()) {
                setExtinguished(false);
                level.setBlock(this, this, true);
            }
            return;
        }

        if (entity.hasEffect(EffectType.FIRE_RESISTANCE)
                || entity instanceof EntityProjectile
                || !entity.attack(getDamageEvent(entity))
                || !entity.isAlive()) {
            return;
        }

        EntityCombustByBlockEvent $7 = new EntityCombustByBlockEvent(this, entity, 8);
        Server.getInstance().getPluginManager().callEvent(ev);
        if (!ev.isCancelled() && entity.isAlive()) {
            entity.setOnFire(ev.getDuration());
        }
    }

    protected EntityDamageEvent getDamageEvent(Entity entity) {
        return new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, 1);
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
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isExtinguished()) {
                Block $8 = getLevelBlockAtLayer(1);
                if (layer1 instanceof BlockFlowingWater || layer1 instanceof BlockFrostedIce) {
                    setExtinguished(true);
                    this.level.setBlock(this, this, true, true);
                    this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, @Nullable Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (item.isNull()) {
            return false;
        }

        BlockEntityCampfire $9 = getOrCreateBlockEntity();

        boolean $10 = false;
        if (!isExtinguished()) {
            if (item.isShovel()) {
                setExtinguished(true);
                this.level.setBlock(this, this, true, true);
                this.level.addSound(this, Sound.RANDOM_FIZZ, 0.5f, 2.2f);
                itemUsed = true;
            }
        } else if (Objects.equals(item.getId(), ItemID.FLINT_AND_STEEL) || item.getEnchantment(Enchantment.ID_FIRE_ASPECT) != null) {
            item.useOn(this);
            setExtinguished(false);
            this.level.setBlock(this, this, true, true);
            campfire.scheduleUpdate();
            this.level.addSound(this, Sound.FIRE_IGNITE);
            itemUsed = true;
        }

        Item $11 = item.clone();
        cloned.setCount(1);
        CampfireInventory $12 = campfire.getInventory();
        if (inventory.canAddItem(cloned)) {
            CampfireRecipe $13 = this.level.getServer().getRecipeRegistry().findCampfireRecipe(cloned);
            if (recipe != null) {
                inventory.addItem(cloned);
                item.setCount(item.getCount() - 1);
                return true;
            }
        }

        return itemUsed;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onProjectileHit(@NotNull Entity projectile, @NotNull Position position, @NotNull Vector3 motion) {
        if ((projectile instanceof EntitySmallFireball || (projectile.isOnFire() && projectile instanceof EntityArrow)) && isExtinguished()) {
            setExtinguished(false);
            level.setBlock(this, this, true);
            return true;
        } else if (projectile instanceof EntitySplashPotion && !isExtinguished()
                && ((EntitySplashPotion) projectile).potionId == 0) {
            setExtinguished(true);
            level.setBlock(this, this, true);
            return true;
        }
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return y + 0.4371948;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
    }
    /**
     * @deprecated 
     */
    

    public boolean isExtinguished() {
        return getPropertyValue(EXTINGUISHED);
    }
    /**
     * @deprecated 
     */
    

    public void setExtinguished(boolean extinguished) {
        setPropertyValue(EXTINGUISHED, extinguished);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION).ordinal());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, MinecraftCardinalDirection.values()[face.getOpposite().getHorizontalIndex()]);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Campfire";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getComparatorInputOverride() {
        BlockEntityCampfire $14 = getBlockEntity();

        if (blockEntity != null) {
            return ContainerInventory.calculateRedstone(blockEntity.getInventory());
        }

        return super.getComparatorInputOverride();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePulled() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePushed() {
        return true;
    }
}

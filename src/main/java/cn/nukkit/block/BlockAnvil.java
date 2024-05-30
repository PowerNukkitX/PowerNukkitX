package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.CommonPropertyMap;
import cn.nukkit.block.property.enums.Damage;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.BlockInventoryHolder;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

import static cn.nukkit.block.property.CommonBlockProperties.DAMAGE;
import static cn.nukkit.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
public class BlockAnvil extends BlockFallable implements Faceable, BlockInventoryHolder {
    public static final BlockProperties PROPERTIES = new BlockProperties(ANVIL, DAMAGE, MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAnvil() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAnvil(BlockState blockstate) {
        super(blockstate);
    }

    public Damage getAnvilDamage() {
        return getPropertyValue(DAMAGE);
    }

    public void setAnvilDamage(Damage anvilDamage) {
        setPropertyValue(DAMAGE, anvilDamage);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return getAnvilDamage().name();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? player.getDirection().rotateYCCW() : BlockFace.SOUTH);
        this.getLevel().setBlock(this, this, true);
        if (player == null) {
            this.getLevel().addSound(this, Sound.RANDOM_ANVIL_LAND, 1, 0.8F);
        } else {
            Collection<Player> players = getLevel().getChunkPlayers(getChunkX(), getChunkZ()).values();
            players.remove(player);
            if (!players.isEmpty()) {
                getLevel().addSound(this, Sound.RANDOM_ANVIL_LAND, 1, 0.8F, players);
            }
        }
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;
        player.addWindow(getOrCreateInventory());
        return true;
    }

    @Override
    public Supplier<Inventory> blockInventorySupplier() {
        return () -> new AnvilInventory(this);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public void setBlockFace(BlockFace face) {
        this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(face));
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        BlockFace face = getBlockFace().rotateY();
        double xOffset = Math.abs(face.getXOffset()) * (2 / 16.0);
        double zOffset = Math.abs(face.getZOffset()) * (2 / 16.0);
        return new SimpleAxisAlignedBB(x + xOffset, y, z + zOffset, x + 1 - xOffset, y + 1, z + 1 - zOffset);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.clone().setPropertyValue(MINECRAFT_CARDINAL_DIRECTION.createDefaultValue()));
    }
}

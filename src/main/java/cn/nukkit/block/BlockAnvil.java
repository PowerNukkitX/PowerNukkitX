package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockproperty.value.AnvilDamage;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Pub4Game
 * @since 27.12.2015
 */

public class BlockAnvil extends BlockFallable implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:anvil", CommonBlockProperties.DAMAGE, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAnvil() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAnvil(BlockState blockstate) {
        super(blockstate);
    }

    public AnvilDamage getAnvilDamage() {
        return getPropertyValue(DAMAGE);
    }


    public void setAnvilDamage(AnvilDamage anvilDamage) {
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
        return getAnvilDamage().getEnglishName();
    }


    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? player.getDirection().rotateY() : BlockFace.SOUTH);
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
    public boolean onActivate(@NotNull Item item, Player player) {
        if (player != null) {
            player.addWindow(new AnvilInventory(player.getUIInventory(), this), Player.ANVIL_WINDOW_ID);
        }
        return true;
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
        setPropertyValue(CommonBlockProperties.CARDINAL_DIRECTION, face);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(CommonBlockProperties.CARDINAL_DIRECTION);
    }


    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        BlockFace face = getBlockFace().rotateY();
        double xOffset = Math.abs(face.getXOffset()) * (2/16.0);
        double zOffset = Math.abs(face.getZOffset()) * (2/16.0);
        return new SimpleAxisAlignedBB(x + xOffset, y, z + zOffset, x + 1 - xOffset, y + 1, z + 1 - zOffset);
    }
}

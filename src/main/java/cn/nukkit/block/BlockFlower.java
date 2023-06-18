package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyValueException;
import cn.nukkit.blockproperty.value.SmallFlowerType;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 * @since 2015/11/23
 */
public class BlockFlower extends BlockFlowable implements BlockFlowerPot.FlowerPotBlock {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<SmallFlowerType> RED_FLOWER_TYPE = new ArrayBlockProperty<>("flower_type", true, new SmallFlowerType[]{
            SmallFlowerType.POPPY,
            SmallFlowerType.ORCHID,
            SmallFlowerType.ALLIUM,
            SmallFlowerType.HOUSTONIA,
            SmallFlowerType.TULIP_RED,
            SmallFlowerType.TULIP_ORANGE,
            SmallFlowerType.TULIP_WHITE,
            SmallFlowerType.TULIP_PINK,
            SmallFlowerType.OXEYE,
            SmallFlowerType.CORNFLOWER,
            SmallFlowerType.LILY_OF_THE_VALLEY
    });

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_FLOWER_TYPE);

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_POPPY = 0;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_BLUE_ORCHID = 1;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_ALLIUM = 2;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_AZURE_BLUET = 3;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_RED_TULIP = 4;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_ORANGE_TULIP = 5;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_WHITE_TULIP = 6;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_PINK_TULIP = 7;

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value. Use FlowerType instead")
    public static final int TYPE_OXEYE_DAISY = 8;
    public static final int TYPE_CORNFLOWER = 9;
    public static final int TYPE_LILY_OF_THE_VALLEY = 10;

    public BlockFlower() {
        this(0);
    }

    public BlockFlower(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return RED_FLOWER;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return getFlowerType().getEnglishName();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public SmallFlowerType getFlowerType() {
        return getPropertyValue(RED_FLOWER_TYPE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    protected void setOnSingleFlowerType(SmallFlowerType acceptsOnly, SmallFlowerType attemptedToSet) {
        if (attemptedToSet == null || attemptedToSet == acceptsOnly) {
            return;
        }
        String persistenceName = getPersistenceName();
        throw new InvalidBlockPropertyValueException(
                new ArrayBlockProperty<>(persistenceName +"_type", false, new SmallFlowerType[]{acceptsOnly}),
                acceptsOnly,
                attemptedToSet,
                persistenceName+" only accepts "+acceptsOnly.name().toLowerCase()
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setFlowerType(SmallFlowerType flowerType) {
        setPropertyValue(RED_FLOWER_TYPE, flowerType);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean isSupportValid(Block block) {
        switch (block.getId()) {
            case GRASS:
            case DIRT:
            case FARMLAND:
            case PODZOL:
            case DIRT_WITH_ROOTS:
            case MOSS_BLOCK:
                return true;
            default:
                return false;
        }
    }

    @PowerNukkitOnly
    public boolean canPlantOn(Block block) {
        return isSupportValid(block);
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (canPlantOn(down)) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will break on normal update if the supporting block is invalid")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canPlantOn(down())) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        if (item.isFertilizer()) { //Bone meal
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));

            for (int i = 0; i < 8; i++) {
                Vector3 vec = this.add(
                        ThreadLocalRandom.current().nextInt(-3, 4),
                        ThreadLocalRandom.current().nextInt(-1, 2),
                        ThreadLocalRandom.current().nextInt(-3, 4));

                if (level.getBlock(vec).getId() == AIR && level.getBlock(vec.down()).getId() == GRASS && vec.getY() >= 0 && vec.getY() < level.getMaxHeight()) {
                    if (ThreadLocalRandom.current().nextInt(10) == 0) {
                        this.level.setBlock(vec, this.getUncommonFlower(), true);
                    } else {
                        this.level.setBlock(vec, get(this.getId()), true);
                    }
                }
            }

            return true;
        }

        return false;
    }

    protected Block getUncommonFlower() {
        return get(DANDELION);
    }
}

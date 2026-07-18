package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.block.BlockGrowEvent;
import org.powernukkitx.event.entity.EntityDamageByBlockEvent;
import org.powernukkitx.event.entity.EntityDamageEvent.DamageCause;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.tags.BlockTags;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static org.powernukkitx.block.property.CommonBlockProperties.AGE_16;
public class BlockCactus extends BlockTransparent implements BlockFlowerPot.FlowerPotBlock, Natural {

    public static final BlockProperties PROPERTIES = new BlockProperties(CACTUS,
            AGE_16);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(0.4)
            .resistance(2)
            .breaksWhenMoved(true)
            .sticksToPiston(false)
            .hasEntityCollision(true)
            .waterloggingLevel(1)
            .build();

    public BlockCactus(BlockState state) {
        super(state, DEFINITION);
    }

    public BlockCactus() {
        this(PROPERTIES.getDefaultState());
    }
  
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getMinX() {
        return this.x + 0.0625;
    }

    @Override
    public double getMinY() {
        return this.y;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.0625;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.9375;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.9375;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.9375;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return new SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.attack(new EntityDamageByBlockEvent(this, entity, DamageCause.CONTACT, 1));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = down();
            if (!down.hasTag(BlockTags.SAND) && !(down instanceof BlockCactus)) {
                this.getLevel().useBreakOn(this);
                return 0;
            }
            for (int side = 2; side <= 5; ++side) {
                Block block = getSide(BlockFace.fromIndex(side));
                if (!block.canBeFlowedInto()) {
                    this.getLevel().useBreakOn(this);
                    return 0;
                }
            }
            return 0;
        }
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down() instanceof BlockCactus) { return 0; }
            if (this.getAge() < getMaxAge()) {
                this.setAge(this.getAge() + 1);
                this.getLevel().setBlock(this, this);
                return 0;
            }
            for (int y = 1; y < 4; ++y) {
                Block b = this.getLevel().getBlock(new Vector3(this.x, this.y + y, this.z));
                if (b.getId().equals(getId())) { continue; }
                if(!b.isAir()) return Level.BLOCK_UPDATE_NORMAL;
                int rand = Utils.rand(0, 100);
                boolean cactusFlower = (y < 2 && rand < 10) || y == 3 && rand <= 25;
                if(cactusFlower) {
                    Block block0 = b.north();
                    Block block1 = b.south();
                    Block block2 = b.west();
                    Block block3 = b.east();
                    if (!block0.isAir() || !block1.isAir() || !block2.isAir() || !block3.isAir()) {
                        this.setAge(getMinAge());
                        this.getLevel().setBlock(this, this, false, false);
                        return 1;
                    }
                } else if(y == 3) {
                    this.setAge(getMinAge());
                    this.getLevel().setBlock(this, this, false, false);
                    return 1;
                }
                BlockGrowEvent event = new BlockGrowEvent(b, Block.get(cactusFlower ? BlockID.CACTUS_FLOWER : BlockID.CACTUS));
                Server.getInstance().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(b, event.getNewState(), true);
                }
                break;
            }
            this.setAge(getMinAge());
            this.getLevel().setBlock(this, this, false, false);
        }

        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        Block down = this.down();
        if (!down.hasTag(BlockTags.SAND) && !(down instanceof BlockCactus)) {
            return false;
        }
        Block block0 = north();
        Block block1 = south();
        Block block2 = west();
        Block block3 = east();
        if (block0.canBeFlowedInto() && block1.canBeFlowedInto() && block2.canBeFlowedInto() && block3.canBeFlowedInto()) {
            this.getLevel().setBlock(this, this, true);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Cactus";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                Item.get(BlockID.CACTUS, 0, 1)
        };
    }

    public int getAge() {
        return this.getPropertyValue(AGE_16);
    }

    public void setAge(int age) {
        this.setPropertyValue(AGE_16, age);
    }

    public static int getMaxAge() {
        return AGE_16.getMax();
    }

    public static int getMinAge() {
        return AGE_16.getMin();
    }

}

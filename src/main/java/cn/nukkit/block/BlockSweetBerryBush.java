package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockHarvestEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSweetBerries;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockSweetBerryBush extends BlockFlowable {

    public static final BlockProperties PROPERTIES = new BlockProperties(SWEET_BERRY_BUSH, CommonBlockProperties.GROWTH);

    public BlockSweetBerryBush() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSweetBerryBush(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Sweet Berry Bush";
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return getGrowth() == 0 ? 0 : 0.25;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        int age = MathHelper.clamp(getGrowth(), 0, 3);

        if (age < 3 && item.isFertilizer()) {
            BlockSweetBerryBush block = (BlockSweetBerryBush) this.clone();
            block.setGrowth(block.getGrowth() + 1);
            if (block.getGrowth() > 3) {
                block.setGrowth(3);
            }
            BlockGrowEvent ev = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                return false;
            }

            this.getLevel().setBlock(this, ev.getNewState(), false, true);
            this.level.addParticle(new BoneMealParticle(this));

            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            return true;
        }

        if (age < 2){
            return true;
        }

        int amount = 1 + ThreadLocalRandom.current().nextInt(2);
        if (age == 3) {
            amount++;
        }

        BlockHarvestEvent event = new BlockHarvestEvent(this,
                new BlockSweetBerryBush().setGrowth(1),
                new Item[]{ new ItemSweetBerries(0, amount) }
        );

        getLevel().getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            getLevel().setBlock(this, event.getNewState(), true, true);
            Item[] drops = event.getDrops();
            if (drops != null) {
                Position dropPos = add(0.5, 0.5, 0.5);
                for (Item drop : drops) {
                    if (drop != null) {
                        getLevel().dropItem(dropPos, drop);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getGrowth() < 3 && ThreadLocalRandom.current().nextInt(5) == 0
                    && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                BlockGrowEvent event = new BlockGrowEvent(this, Block.get(getId()).setPropertyValue(CommonBlockProperties.GROWTH, getGrowth() + 1));
                if (!event.isCancelled()) {
                    getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId().equals(SWEET_BERRY_BUSH) || !block.isAir()) {
            return false;
        }
        if (isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    public static boolean isSupportValid(Block block) {
        return switch (block.getId()) {
            case GRASS_BLOCK, DIRT, PODZOL, DIRT_WITH_ROOTS, MOSS_BLOCK -> true;
            default -> false;
        };
    }

    @Override
    public boolean hasEntityCollision() {
        return getGrowth() > 0;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (getGrowth() > 0) {
            if (entity.positionChanged && !entity.isSneaking() && ThreadLocalRandom.current().nextInt(20) == 0) {
                if (entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.CONTACT, 1))) {
                    getLevel().addSound(entity, Sound.BLOCK_SWEET_BERRY_BUSH_HURT);
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return getGrowth() > 0 ? this : null;
    }

    @Override
    public Item[] getDrops(Item item) {
        int age = MathHelper.clamp(getGrowth(), 0, 3);
        
        int amount = 1;
        if (age > 1) {
            amount = 1 + ThreadLocalRandom.current().nextInt(2);
            if (age == 3) {
                amount++;
            }
        }

        return new Item[]{ new ItemSweetBerries(0, amount) };
    }

    public int getGrowth() {
        return getPropertyValue(CommonBlockProperties.GROWTH);
    }

    public Block setGrowth(int growth) {
        return setPropertyValue(CommonBlockProperties.GROWTH, growth);
    }

    @Override
    public Item toItem() {
        return new ItemSweetBerries();
    }
}

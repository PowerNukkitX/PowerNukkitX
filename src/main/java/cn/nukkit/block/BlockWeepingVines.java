package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.property.CommonBlockProperties.WEEPING_VINES_AGE;

public class BlockWeepingVines extends BlockVinesNether {

    public static final BlockProperties PROPERTIES = new BlockProperties(WEEPING_VINES, WEEPING_VINES_AGE);

    private static final int MAX_AGE = WEEPING_VINES_AGE.getMax(); // expected 25

    public BlockWeepingVines() { this(PROPERTIES.getDefaultState()); }

    public BlockWeepingVines(BlockState blockstate) { super(blockstate); }

    @Override
    @NotNull
    public BlockProperties getProperties() { return PROPERTIES; }

    @Override
    public String getName() { return "Weeping Vines"; }

    @Override
    @NotNull
    public BlockFace getGrowthDirection() { return BlockFace.DOWN; }

    @Override
    public int getVineAge() { return getPropertyValue(WEEPING_VINES_AGE); }

    @Override
    public void setVineAge(int vineAge) {
        setPropertyValue(WEEPING_VINES_AGE, NukkitMath.clamp(vineAge, 0, MAX_AGE));
    }

    @Override
    public int getMaxVineAge() { return MAX_AGE; }

    private boolean isTip() {
        Block up = this.up();
        return !(up instanceof BlockWeepingVines);
    }

    private int countChainLength() {
        return (int) (getTopBlock().y - getBottomBlock().y);
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_RANDOM) return 0;

        if (!isTip()) return 0;

        // 10% chance
        if (ThreadLocalRandom.current().nextInt(10) != 0) return type;

        if (getBottomAge() >= getMaxVineAge()) return type;

        BlockWeepingVines bottom = getBottomBlock();
        Block belowBottom = bottom.down();
        if (!belowBottom.isAir()) return type;

        BlockWeepingVines newVine = new BlockWeepingVines();
        newVine.setVineAge(Math.min(bottom.getVineAge() + 1, getMaxVineAge()));
        level.setBlock(belowBottom.getPosition(), newVine, true, true);

        this.level.getServer().getLogger().debug("WeepingVines grew: newBottomAge=" + newVine.getVineAge() + " chainLen=" + countChainLength());

        return type;
    }

    private BlockWeepingVines getTopBlock() {
        BlockWeepingVines block = this;
        while (block.up() instanceof BlockWeepingVines up) {
            block = up;
        }
        return block;
    }

    private BlockWeepingVines getBottomBlock() {
        BlockWeepingVines block = this;
        while (block.down() instanceof BlockWeepingVines down) {
            block = down;
        }
        return block;
    }

    private int getBottomAge() {
        BlockWeepingVines bottom = getBottomBlock();
        return bottom.getVineAge();
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace face, float fx, float fy, float fz) {
        if (!item.isFertilizer()) return false;

        // Find the topmost vine (true tip) for this chain
        BlockWeepingVines top = getTopBlock();

        // Random growth amount 1..4 (vanilla behaviour)
        int toGrow = ThreadLocalRandom.current().nextInt(1, 5); // 1..4

        // Use bottom age to enforce MAX_AGE
        for (int i = 0; i < toGrow; i++) {
            // Recompute bottom and its age each iteration because chain grows
            BlockWeepingVines bottom = top.getBottomBlock();
            if (bottom.getVineAge() >= getMaxVineAge()) break;

            Block below = bottom.down();
            if (!below.isAir()) break;

            // Place new vine below with age = bottomAge + 1 (clamped)
            BlockWeepingVines newVine = new BlockWeepingVines();
            newVine.setVineAge(Math.min(bottom.getVineAge() + 1, getMaxVineAge()));
            level.setBlock(below.getPosition(), newVine, true, true);
        }

        // Consume item if player not in creative
        if (player != null && (player.gamemode & 0x01) == 0) {
            item.count--;
        }

        // Particle at the topmost vine (visual feedback)
        this.level.addParticle(new BoneMealParticle(this));

        return true;
    }

    @Override
    public Item[] getDrops(Item item) {
        Block above = this.up();
        boolean unsupported = !(above instanceof BlockWeepingVines) && !above.isSolid();

        // 33% chance to drop a vine https://minecraft.fandom.com/wiki/Weeping_Vines
        if (unsupported || item == null) {
            if (ThreadLocalRandom.current().nextDouble() < 0.33) {
                return new Item[]{ Item.get(WEEPING_VINES, 0, 1) };
            }
            return Item.EMPTY_ARRAY;
        }

        // Shears or Silk Touch always drop 1
        if (item.isShears() || item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{ Item.get(WEEPING_VINES, 0, 1) };
        }

        int fortune = item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING);
        double chance = switch (fortune) {
            case 1 -> 0.55;
            case 2 -> 0.77;
            case 3 -> 1.0;
            default -> 0.33;
        };

        return ThreadLocalRandom.current().nextDouble() < chance ? new Item[]{ Item.get(WEEPING_VINES, 0, 1) } : Item.EMPTY_ARRAY;
    }

    @Override
    public boolean onBreak(Item item) {
        Block block = this.down();
        while (block instanceof BlockWeepingVines) {
            if (ThreadLocalRandom.current().nextDouble() < 0.33) {
                this.level.dropItem(block, Item.get(WEEPING_VINES, 0, 1));
            }
            level.setBlock(block.getPosition(), Block.get(AIR), true, true);
            block = block.down();
        }

        return super.onBreak(item);
    }
}
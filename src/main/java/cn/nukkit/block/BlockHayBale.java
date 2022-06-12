package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

import static cn.nukkit.potion.Effect.getEffect;

/**
 * @author xtypr
 * @since 2015/11/24
 */
public class BlockHayBale extends BlockSolidMeta implements Faceable {
    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = BlockBone.PROPERTIES;

    public BlockHayBale() {
        this(0);
    }

    public BlockHayBale(int meta) {
        super(meta);
     }

    @Override
    public int getId() {
        return HAY_BALE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Hay Bale";
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        int[] faces = new int[]{
                0,
                0,
                0b1000,
                0b1000,
                0b0100,
                0b0100,
        };
        this.setDamage((this.getDamage() & 0x03) | faces[face.getIndex()]);
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x07);
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public boolean useDefaultFallDamage() {
        return false;
    }

    @Since("1.6.0.0-PNX")
    @PowerNukkitOnly
    @Override
    public void onEntityFallOn(Entity entity, float fallDistance) {
        int jumpBoost = entity.hasEffect(Effect.JUMP_BOOST)? (getEffect(Effect.JUMP_BOOST).getAmplifier() + 1) : 0;
        float damage = (float) Math.floor(fallDistance - 3 - jumpBoost);

        damage *= 0.2F;

        if (damage > 0) {
            entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.FALL, damage));
        }
    }

    @Override
    public Item toItem() {
        return Item.get(this.getId(), 0);
    }
}

package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.block.BlockFormEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

public class BlockMagma extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGMA);

    public BlockMagma() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMagma(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Magma Block";
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getLightLevel() {
        return 3;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity.hasEffect(EffectType.FIRE_RESISTANCE)) {
            return;
        }

        if (entity instanceof Player p) {
            if (p.getInventory().getBoots().getEnchantment(Enchantment.ID_FROST_WALKER) != null
                    || p.isCreative() || p.isSpectator() || p.isSneaking() || !p.level.gameRules.getBoolean(GameRule.FIRE_DAMAGE)) {
                return;
            }
        }

        entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.HOT_FLOOR, 1));
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block up = up();
            if (up instanceof BlockFlowingWater blockFlowingWater && (blockFlowingWater.getLiquidDepth() == 0 || blockFlowingWater.getLiquidDepth() == 8)) {
                BlockFormEvent event = new BlockFormEvent(up, new BlockBubbleColumn().setPropertyValue(CommonBlockProperties.DRAG_DOWN, true));
                if (!event.isCancelled()) {
                    if (event.getNewState().getWaterloggingLevel() > 0) {
                        this.getLevel().setBlock(up, 1, new BlockFlowingWater(), true, false);
                    }
                    this.getLevel().setBlock(up, 0, event.getNewState(), true, true);
                }
            }
        }
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}

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
    public static final BlockProperties $1 = new BlockProperties(MAGMA);
    /**
     * @deprecated 
     */
    

    public BlockMagma() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMagma(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Magma Block";
    }

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 30;
    }

    @Override
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
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
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $2 = up();
            if (up instanceof BlockFlowingWater blockFlowingWater && (blockFlowingWater.getLiquidDepth() == 0 || blockFlowingWater.getLiquidDepth() == 8)) {
                BlockFormEvent $3 = new BlockFormEvent(up, new BlockBubbleColumn().setPropertyValue(CommonBlockProperties.DRAG_DOWN, true));
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
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

}

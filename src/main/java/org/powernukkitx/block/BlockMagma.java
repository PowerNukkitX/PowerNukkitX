package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.block.BlockFormEvent;
import org.powernukkitx.event.entity.EntityDamageByBlockEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.GameRule;
import org.powernukkitx.level.Level;
import org.jetbrains.annotations.NotNull;

public class BlockMagma extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGMA);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.5)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .lightEmission(3)
            .canHarvestWithHand(false)
            .build();

    public BlockMagma() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMagma(BlockState blockState) {
        super(blockState, DEFINITION);
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
        if (entity.hasEffect(EffectType.FIRE_RESISTANCE) || entity.isFireImmune()) {
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
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
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

    
}

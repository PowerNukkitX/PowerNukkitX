package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.inventory.InventorySlice;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemGoldIngot;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.inventory.EntityEquipmentInventory.OFFHAND;


public class PiglinTradeExecutor implements EntityControl, IBehaviorExecutor {

    protected int tick = 0;

    public PiglinTradeExecutor() {}

    @Override
    public boolean execute(EntityIntelligent entity) {
        tick++;
        if(tick < 160) {
            if(tick % 30 == 0 && ThreadLocalRandom.current().nextBoolean()) {
                entity.level.addSound(entity, Sound.MOB_PIGLIN_ADMIRING_ITEM);
            }
            return true;
        } else {
            if(entity instanceof EntityMob mob) {
                Item offhand = mob.getItemInOffhand();
                if(offhand instanceof ItemGoldIngot && !mob.isBaby()) {
                    mob.getEquipmentInventory().decreaseCount(OFFHAND);
                    Vector3 motion = entity.getDirectionVector().multiply(0.4);
                    entity.level.dropItem(entity.add(0, 1.3, 0), getDrop(), motion, 40);
                }
            }
            return false;
        }
    }


    @Override
    public void onStart(EntityIntelligent entity) {
        tick = -1;
        removeLookTarget(entity);
        entity.setDataFlag(EntityFlag.ADMIRING);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        if(entity instanceof EntityMob mob) {
            clearOffhand(mob, mob.getItemInOffhand());
        }
        entity.setMovementSpeed(entity.getDefaultSpeed());
        entity.setEnablePitch(false);
        entity.setDataFlag(EntityFlag.ADMIRING, false);
    }

    public Item getDrop() {
        Item item;
        int random = ThreadLocalRandom.current().nextInt(459);
        if(random < 5) {
            item = Item.get(Item.ENCHANTED_BOOK);
            item.addEnchantment(Enchantment.get(Enchantment.ID_SOUL_SPEED).setLevel(ThreadLocalRandom.current().nextInt(1, 3)));
        } else if(random < 13) {
            item = Item.get(Item.IRON_BOOTS);
            item.addEnchantment(Enchantment.get(Enchantment.ID_SOUL_SPEED).setLevel(ThreadLocalRandom.current().nextInt(1, 3)));
        } else if(random < 21) {
            item = Item.get(Item.SPLASH_POTION, EffectType.FIRE_RESISTANCE.id());
        } else if(random < 29) {
            item = Item.get(Item.POTION, EffectType.FIRE_RESISTANCE.id());
        } else if(random < 39) {
            item = Item.get(Item.POTION);
        } else if(random < 49) {
            item = Item.get(Item.IRON_NUGGET, 0 , ThreadLocalRandom.current().nextInt(10, 37));
        } else if(random < 59) {
            item = Item.get(Item.ENDER_PEARL, 0 , ThreadLocalRandom.current().nextInt(2, 5));
        } else if(random < 79) {
            item = Item.get(Item.STRING, 0 , ThreadLocalRandom.current().nextInt(3, 10));
        } else if(random < 99) {
            item = Item.get(Item.QUARTZ, 0 , ThreadLocalRandom.current().nextInt(5, 13));
        } else if(random < 139) {
            item = Item.get(Block.OBSIDIAN);
        } else if(random < 179) {
            item = Item.get(Block.CRYING_OBSIDIAN, 0 , ThreadLocalRandom.current().nextInt(1, 4));
        } else if(random < 219) {
            item = Item.get(Item.FIRE_CHARGE);
        } else if(random < 259) {
            item = Item.get(Item.LEATHER, 0 , ThreadLocalRandom.current().nextInt(2, 5));
        } else if(random < 299) {
            item = Item.get(Block.SOUL_SAND, 0 , ThreadLocalRandom.current().nextInt(2, 9));
        } else if(random < 339) {
            item = Item.get(Item.NETHERBRICK, 0 , ThreadLocalRandom.current().nextInt(2, 9));
        } else if(random < 379) {
            item = Item.get(Item.ARROW, 0 , ThreadLocalRandom.current().nextInt(6, 12));
        } else if(random < 419) {
            item = Item.get(Block.GRAVEL, 0 , ThreadLocalRandom.current().nextInt(8, 17));
        } else {
            item = Item.get(Block.BLACKSTONE, 0 , ThreadLocalRandom.current().nextInt(8, 17));
        }
        return item;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        onStop(entity);
    }

    public void clearOffhand(EntityMob mob, Item item) {
        new InventorySlice(mob.getEquipmentInventory(), 2, mob.getEquipmentInventory().getSize()).addItem(item);
        mob.getEquipmentInventory().clear(OFFHAND);
    }
}



package cn.nukkit.item.customitem;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.ItemDurable;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.Utils;

/**
 * @author lt_name
 */
public abstract class ItemCustomTool extends ItemCustom implements ItemDurable {

    public static final int DURABILITY_WOODEN = ItemTool.DURABILITY_WOODEN;
    public static final int DURABILITY_GOLD = ItemTool.DURABILITY_GOLD;
    public static final int DURABILITY_STONE = ItemTool.DURABILITY_STONE;
    public static final int DURABILITY_IRON = ItemTool.DURABILITY_IRON;
    public static final int DURABILITY_DIAMOND = ItemTool.DURABILITY_DIAMOND;
    public static final int DURABILITY_NETHERITE = ItemTool.DURABILITY_NETHERITE;
    public static final int DURABILITY_FLINT_STEEL = ItemTool.DURABILITY_FLINT_STEEL;
    public static final int DURABILITY_SHEARS = ItemTool.DURABILITY_SHEARS;
    public static final int DURABILITY_BOW = ItemTool.DURABILITY_BOW;
    public static final int DURABILITY_CROSSBOW = ItemTool.DURABILITY_CROSSBOW;
    public static final int DURABILITY_TRIDENT = ItemTool.DURABILITY_TRIDENT;
    public static final int DURABILITY_FISHING_ROD = ItemTool.DURABILITY_FISHING_ROD;
    public static final int DURABILITY_CARROT_ON_A_STICK = ItemTool.DURABILITY_CARROT_ON_A_STICK;
    public static final int DURABILITY_WARPED_FUNGUS_ON_A_STICK = ItemTool.DURABILITY_WARPED_FUNGUS_ON_A_STICK;

    public ItemCustomTool(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemCustomTool(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemCustomTool(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemCustomTool(int id, Integer meta, int count, String name) {
        this(id, meta, count, name, name);
    }

    public ItemCustomTool(int id, Integer meta, int count, String name, String textureName) {
        super(id, meta, count, name, textureName);
    }

    @Override
    public int getCreativeCategory() {
        return 3;
    }

    @Override
    public CompoundTag getComponentsData() {
        CompoundTag data = super.getComponentsData();

        data.getCompound("components")
                .putCompound("minecraft:durability",
                        new CompoundTag().putInt("max_durability", this.getMaxDurability())
                )
                .getCompound("item_properties")
                .putInt("damage", this.getAttackDamage());

        if(this.isPickaxe()) {
            data.getCompound("components")
                    .putCompound("minecraft:digger", getPickaxeDiggerNBT(this.getTier()));
        }else if(this.isAxe()) {
            data.getCompound("components")
                    .putCompound("minecraft:digger", getAxeDiggerNBT(this.getTier()));
        }else if(this.isShovel()) {
            data.getCompound("components")
                    .putCompound("minecraft:digger", getShovelDiggerNBT(this.getTier()));
        }

        return data;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean useOn(Block block) {
        if (this.isUnbreakable() || isDurable() || noDamageOnBreak()) {
            return true;
        }

        if (block.getToolType() == ItemTool.TYPE_PICKAXE && this.isPickaxe() ||
                block.getToolType() == ItemTool.TYPE_SHOVEL && this.isShovel() ||
                block.getToolType() == ItemTool.TYPE_AXE && this.isAxe() ||
                block.getToolType() == ItemTool.TYPE_HOE && this.isHoe() ||
                block.getToolType() == ItemTool.TYPE_SWORD && this.isSword() ||
                block.getToolType() == ItemTool.TYPE_SHEARS && this.isShears()
        ) {
            this.meta++;
        } else if (!this.isShears() && block.calculateBreakTime(this) > 0) {
            this.meta += 2;
        } else if (this.isHoe()) {
            if (block.getId() == GRASS || block.getId() == DIRT) {
                this.meta++;
            }
        } else {
            this.meta++;
        }

        if (this.meta > this.getMaxDurability()) {
            this.count--;
        }

        return true;
    }

    @Override
    public boolean useOn(Entity entity) {
        if (this.isUnbreakable() || isDurable() || noDamageOnAttack()) {
            return true;
        }

        if ((entity != null) && !this.isSword()) {
            this.meta += 2;
        } else {
            this.meta++;
        }

        if (this.meta > this.getMaxDurability()) {
            this.count--;
        }

        return true;
    }

    private boolean isDurable() {
        if (!hasEnchantments()) {
            return false;
        }

        Enchantment durability = getEnchantment(Enchantment.ID_DURABILITY);
        return durability != null && durability.getLevel() > 0 && (100 / (durability.getLevel() + 1)) <= Utils.random.nextInt(100);
    }

    @Override
    public boolean isUnbreakable() {
        Tag tag = this.getNamedTagEntry("Unbreakable");
        return tag instanceof ByteTag && ((ByteTag) tag).data > 0;
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }

    @Override
    public int getEnchantAbility() {
        switch (this.getTier()) {
            case ItemTool.TIER_STONE:
                return 5;
            case ItemTool.TIER_WOODEN:
                return 15;
            case ItemTool.TIER_DIAMOND:
                return 10;
            case ItemTool.TIER_GOLD:
                return 22;
            case ItemTool.TIER_IRON:
                return 14;
            case ItemTool.TIER_NETHERITE:
                return 10; //TODO
        }

        return 0;
    }

    /**
     * No damage to item when it's used to attack entities
     * @return whether the item should take damage when used to attack entities
     */
    public boolean noDamageOnAttack() {
        return false;
    }

    /**
     * No damage to item when it's used to break blocks
     * @return whether the item should take damage when used to break blocks
     */
    public boolean noDamageOnBreak() {
        return false;
    }

    public static CompoundTag getPickaxeDiggerNBT(int tier){
        int speed = 1;
        if(tier == 0){
            return new CompoundTag().putBoolean("use_efficiency",true);
        }else if(tier == 5){
            speed = 6;
        }else if(tier == 4){
            speed = 5;
        }else if(tier == 3){
            speed = 4;
        }else if(tier == 2){
            speed = 3;
        }else if(tier == 1){
            speed = 2;
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags",
                                        "q.any_tag('stone', 'metal', 'diamond_pick_diggable', 'mob_spawner', 'rail')"))
                .putInt("speed",speed));
        return diggerRoot.putList(destroy_speeds);
    }

    public static CompoundTag getAxeDiggerNBT(int tier){
        int speed = 1;
        if(tier == 0){
            return new CompoundTag().putBoolean("use_efficiency",true);
        }else if(tier == 5){
            speed = 6;
        }else if(tier == 4){
            speed = 5;
        }else if(tier == 3){
            speed = 4;
        }else if(tier == 2){
            speed = 3;
        }else if(tier == 1){
            speed = 2;
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags", "q.any_tag('wood', 'pumpkin', 'plant')"))
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

    public static CompoundTag getShovelDiggerNBT(int tier){
        int speed = 1;
        if(tier == 0){
            return new CompoundTag().putBoolean("use_efficiency",true);
        }else if(tier == 5){
            speed = 6;
        }else if(tier == 4){
            speed = 5;
        }else if(tier == 3){
            speed = 4;
        }else if(tier == 2){
            speed = 3;
        }else if(tier == 1){
            speed = 2;
        }
        CompoundTag diggerRoot = new CompoundTag().putBoolean("use_efficiency",true);
        ListTag<Tag> destroy_speeds = new ListTag<>("destroy_speeds");
        destroy_speeds.add(new CompoundTag()
                .putCompound("block",
                        new CompoundTag()
                                .putString("tags", "q.any_tag('sand', 'dirt', 'gravel', 'snow')"))
                .putInt("speed", speed));
        return diggerRoot.putList(destroy_speeds);
    }

}

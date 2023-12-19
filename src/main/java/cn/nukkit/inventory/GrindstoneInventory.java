package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.API;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;


public class GrindstoneInventory extends FakeBlockUIComponent {

    public static final int OFFSET = 16;
    
    private static final int SLOT_FIRST_ITEM = 0;
    private static final int SLOT_SECOND_ITEM = 1;
    private static final int SLOT_RESULT = 50 - OFFSET;


    @API(usage = API.Usage.INCUBATING, definition = API.Definition.INTERNAL)
    public static final int GRINDSTONE_EQUIPMENT_UI_SLOT = OFFSET + SLOT_FIRST_ITEM;


    @API(usage = API.Usage.INCUBATING, definition = API.Definition.INTERNAL)
    public static final int GRINDSTONE_INGREDIENT_UI_SLOT = OFFSET + SLOT_SECOND_ITEM;

    private int resultExperience;


    public GrindstoneInventory(PlayerUIInventory playerUI, Position position) {
        super(playerUI, InventoryType.GRINDSTONE, OFFSET, position);
    }

    @Override
    public void close(Player who) {
        onClose(who);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);
        who.craftingType = Player.CRAFTING_SMALL;

        Item[] drops = new Item[]{ getFirstItem(), getSecondItem() };
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop);
            }
        }

        clear(SLOT_FIRST_ITEM);
        clear(SLOT_SECOND_ITEM);

        who.resetCraftingGridType();
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        who.craftingType = Player.CRAFTING_GRINDSTONE;
    }


    public Item getFirstItem() {
        return getItem(SLOT_FIRST_ITEM);
    }


    public Item getSecondItem() {
        return getItem(SLOT_SECOND_ITEM);
    }


    public Item getResult() {
        return getItem(2);
    }


    public boolean setFirstItem(Item item, boolean send) {
        return setItem(SLOT_FIRST_ITEM, item, send);
    }


    public boolean setFirstItem(Item item) {
        return setFirstItem(item, true);
    }


    public boolean setSecondItem(Item item, boolean send) {
        return setItem(SLOT_SECOND_ITEM, item, send);
    }


    public boolean setSecondItem(Item item) {
        return setSecondItem(item, true);
    }


    public boolean setResult(Item item, boolean send) {
        return setItem(2, item, send);
    }


    public boolean setResult(Item item) {
        return setResult(item, true);
    }
    
    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        try {
            if (index > 1) {
                return;
            }
            updateResult(send);
        } finally {
            super.onSlotChange(index, before, send);
        }
    }


    public boolean updateResult(boolean send) {
        Item firstItem = getFirstItem();
        Item secondItem = getSecondItem();
        if (!firstItem.isNull() && !secondItem.isNull() && firstItem.getId() != secondItem.getId()) {
            setResult(Item.get(0), send);
            setResultExperience(0);
            return false;
        }

        if (firstItem.isNull()) {
            Item air = firstItem;
            firstItem = secondItem;
            secondItem = air;
        }

        if (firstItem.isNull()) {
            setResult(Item.get(0), send);
            setResultExperience(0);
            return false;
        }

        if (firstItem.getId() == ItemID.ENCHANTED_BOOK) {
            if (secondItem.isNull()) {
                setResult(Item.get(ItemID.BOOK, 0, firstItem.getCount()), send);
                recalculateResultExperience();
            } else {
                setResultExperience(0);
                setResult(Item.get(0), send);
            }
            return false;
        }

        Item result = firstItem.clone();
        CompoundTag tag = result.getNamedTag();
        if (tag == null) tag = new CompoundTag();
        tag.remove("ench");
        
        result.setCompoundTag(tag);
        if (!secondItem.isNull() && firstItem.getMaxDurability() > 0) {
            int first = firstItem.getMaxDurability() - firstItem.getMeta();
            int second = secondItem.getMaxDurability() - secondItem.getMeta();
            int reduction = first + second + firstItem.getMaxDurability() * 5 / 100;
            int resultingDamage = Math.max(firstItem.getMaxDurability() - reduction + 1, 0);
            result.setMeta(resultingDamage);
        }
        setResult(result, send);
        recalculateResultExperience();
        return true;
    }


    public void recalculateResultExperience() {
        if (getResult().isNull()) {
            setResultExperience(0);
            return;
        }

        Item firstItem = getFirstItem();
        Item secondItem = getSecondItem();
        if (!firstItem.hasEnchantments() && !secondItem.hasEnchantments()) {
            setResultExperience(0);
            return;
        }

        int resultExperience = Stream.of(firstItem, secondItem)
                .flatMap(item -> {
                    // Support stacks of enchanted items and skips invalid stacks (e.g. negative stacks, enchanted air)
                    if (item.isNull()) {
                        return Stream.empty();
                    } else if (item.getCount() == 1) {
                        return Arrays.stream(item.getEnchantments());
                    } else {
                        Enchantment[][] enchantments = new Enchantment[item.getCount()][];
                        Arrays.fill(enchantments, item.getEnchantments());
                        return Arrays.stream(enchantments).flatMap(Arrays::stream);
                    }
                })
                .mapToInt(enchantment-> enchantment.getMinEnchantAbility(enchantment.getLevel()))
                .sum();

        resultExperience = ThreadLocalRandom.current().nextInt(
                NukkitMath.ceilDouble((double)resultExperience / 2),
                resultExperience + 1
        );

        setResultExperience(resultExperience);
    }

    @NotNull
    @Override
    public Item getItem(int index) {
        if (index < 0 || index > 3) {
            return Item.get(0);
        }
        if (index == 2) {
            index = SLOT_RESULT;
        }

        return super.getItem(index);
    }


    @Override
    public Item getUnclonedItem(int index) {
        if (index < 0 || index > 3) {
            return Item.get(0);
        }
        if (index == 2) {
            index = SLOT_RESULT;
        }

        return super.getUnclonedItem(index);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (index < 0 || index > 3) {
            return false;
        }
        
        if (index == 2) {
            index = SLOT_RESULT;
        }
        
        return super.setItem(index, item, send);
    }


    public int getResultExperience() {
        return resultExperience;
    }


    public void setResultExperience(int returnLevels) {
        this.resultExperience = returnLevels;
    }
}

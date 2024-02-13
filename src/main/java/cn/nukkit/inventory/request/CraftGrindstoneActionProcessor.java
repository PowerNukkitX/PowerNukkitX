package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.GrindstoneEvent;
import cn.nukkit.inventory.GrindstoneInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftGrindstoneAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectIntMutablePair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * @author CoolLoong
 */
@Slf4j
public class CraftGrindstoneActionProcessor implements ItemStackRequestActionProcessor<CraftGrindstoneAction> {

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_REPAIR_AND_DISENCHANT;
    }

    @Nullable
    @Override
    public ActionResponse handle(CraftGrindstoneAction action, Player player, ItemStackRequestContext context) {
        Optional<Inventory> topWindow = player.getTopWindow();
        if (topWindow.isEmpty()) {
            log.error("the player's inventory is empty!");
            return context.error();
        }
        GrindstoneInventory inventory = (GrindstoneInventory) topWindow.get();
        Item firstItem = inventory.getFirstItem();
        Item secondItem = inventory.getSecondItem();
        if ((firstItem == null || firstItem.isNull()) && (secondItem == null || secondItem.isNull())) {
            return context.error();
        }
        Pair<Item, Integer> pair = updateGrindstoneResult(player, inventory);
        if (pair == null) {
            return context.error();
        }
        Integer exp = pair.right();
        GrindstoneEvent event = new GrindstoneEvent(inventory, firstItem == null ? Item.AIR : firstItem, pair.left(), secondItem == null ? Item.AIR : secondItem, exp, player);
        player.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.removeAllWindows(false);
            player.sendAllInventories();
            return context.error();
        }
        player.addExperience(event.getExperienceDropped());
        player.getCreativeOutputInventory().setItem(event.getResultItem());
        return null;
    }

    public @Nullable Pair<Item, Integer> updateGrindstoneResult(Player player, GrindstoneInventory inventory) {
        Item firstItem = inventory.getFirstItem();
        Item secondItem = inventory.getSecondItem();
        Pair<Item, Integer> resultPair = ObjectIntMutablePair.of(Item.AIR, 0);
        if (!firstItem.isNull() && !secondItem.isNull() && !Objects.equals(firstItem.getId(), secondItem.getId())) {
            return null;
        }

        if (firstItem.isNull()) {
            Item air = firstItem;
            firstItem = secondItem;
            secondItem = air;
        }

        if (firstItem.isNull()) {
            return null;
        }

        if (Objects.equals(firstItem.getId(), ItemID.ENCHANTED_BOOK)) {
            if (secondItem.isNull()) {
                resultPair.left(Item.get(ItemID.BOOK, 0, firstItem.getCount()));
                resultPair.right(recalculateResultExperience(inventory));
            } else {
                return null;
            }
            return resultPair;
        }
        int resultExperience = recalculateResultExperience(inventory);
        Item result = firstItem.clone();
        CompoundTag tag = result.getNamedTag();
        if (tag == null) tag = new CompoundTag();
        tag.remove("ench");
        tag.remove("custom_ench");
        result.setCompoundTag(tag);

        if (!secondItem.isNull() && firstItem.getMaxDurability() > 0) {
            int first = firstItem.getMaxDurability() - firstItem.getDamage();
            int second = secondItem.getMaxDurability() - secondItem.getDamage();
            int reduction = first + second + firstItem.getMaxDurability() * 5 / 100;
            int resultingDamage = Math.max(firstItem.getMaxDurability() - reduction + 1, 0);
            result.setDamage(resultingDamage);
        }
        resultPair.left(result);
        resultPair.right(resultExperience);
        return resultPair;
    }

    public int recalculateResultExperience(GrindstoneInventory inventory) {
        Item firstItem = inventory.getFirstItem();
        Item secondItem = inventory.getSecondItem();
        if (!firstItem.hasEnchantments() && !secondItem.hasEnchantments()) {
            return 0;
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
                .mapToInt(enchantment -> enchantment.getMinEnchantAbility(enchantment.getLevel()))
                .sum();

        resultExperience = ThreadLocalRandom.current().nextInt(
                NukkitMath.ceilDouble((double) resultExperience / 2),
                resultExperience + 1
        );
        return resultExperience;
    }
}

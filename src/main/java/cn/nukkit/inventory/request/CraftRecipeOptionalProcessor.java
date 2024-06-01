package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.CartographyTableInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFilledMap;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequest;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftRecipeOptionalAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import io.netty.util.internal.StringUtil;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectIntMutablePair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author CoolLoong
 */
@Slf4j
public class CraftRecipeOptionalProcessor implements ItemStackRequestActionProcessor<CraftRecipeOptionalAction> {
    @Override
    public ActionResponse handle(CraftRecipeOptionalAction action, Player player, ItemStackRequestContext context) {
        Optional<Inventory> topWindow = player.getTopWindow();
        if (topWindow.isEmpty()) {
            log.error("the player's inventory is empty!");
            return context.error();
        }
        ItemStackRequest itemStackRequest = context.getItemStackRequest();
        Inventory inventory = topWindow.get();

        String filterString = null;
        if (itemStackRequest.getFilterStrings().length != 0 && !itemStackRequest.getFilterStrings()[0].isBlank()) {
            int filteredStringIndex = action.getFilteredStringIndex();
            String[] filterStrings = itemStackRequest.getFilterStrings();
            filterString = filterStrings[filteredStringIndex];
            if (filterString.isBlank() || filterString.length() > 64) {
                log.debug(player.getName() + ": FilterTextPacket with too long text");
                return context.error();
            }
        }

        if (inventory instanceof AnvilInventory anvilInventory) {
            Pair<Item, Integer> pair = updateAnvilResult(player, anvilInventory, filterString);
            if (pair != null) {
                player.getCreativeOutputInventory().setItem(pair.left());
                player.setExperience(player.getExperience() - pair.right());
            } else{
                return context.error();
            }
        } else if (inventory instanceof CartographyTableInventory cartographyInventory) {
            Item item = updateCartographyTableResult(player, cartographyInventory, filterString);
            if (item != null) {
                player.getCreativeOutputInventory().setItem(item);
            } else {
                return context.error();
            }
        } else {
            //todo more
        }
        return null;
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RECIPE_OPTIONAL;
    }

    public @Nullable Pair<Item, Integer> updateAnvilResult(Player player, AnvilInventory inventory, @Nullable String filterString) {
        Item target = inventory.getInputSlot();
        Item sacrifice = inventory.getMaterialSlot();
        if (target.isNull() && sacrifice.isNull()) {
            return null;
        }

        Pair<Item, Integer> resultPair = ObjectIntMutablePair.of(Item.AIR, 1);

        int extraCost = 0;
        int costHelper = 0;
        String repairMaterial = getRepairMaterial(target);
        Item result = target.clone();

        Set<Enchantment> enchantments = new LinkedHashSet<>(Arrays.asList(target.getEnchantments()));
        if (!sacrifice.isNull()) {
            boolean enchantedBook = Objects.equals(sacrifice.getId(), Item.ENCHANTED_BOOK) && sacrifice.getEnchantments().length > 0;
            int repair;
            int repair2;
            int repair3;
            if (result.getMaxDurability() != -1 && Objects.equals(sacrifice.getId(), repairMaterial)) {//Anvil - repair
                repair = Math.min(result.getDamage(), result.getMaxDurability() / 4);
                if (repair <= 0) {
                    return null;
                }

                for (repair2 = 0; repair > 0 && repair2 < sacrifice.getCount(); ++repair2) {
                    repair3 = result.getDamage() - repair;
                    result.setDamage(repair3);
                    ++extraCost;
                    repair = Math.min(result.getDamage(), result.getMaxDurability() / 4);
                }
            } else {
                if (!enchantedBook && (!Objects.equals(result.getId(), sacrifice.getId()) || result.getMaxDurability() == -1)) {//Anvil - ench
                    player.getLevel().addSound(player, Sound.RANDOM_ANVIL_USE, 1, 1);
                    return null;
                }

                if ((result.getMaxDurability() != -1) && !enchantedBook) {
                    repair = target.getMaxDurability() - target.getDamage();
                    repair2 = sacrifice.getMaxDurability() - sacrifice.getDamage();
                    repair3 = repair2 + result.getMaxDurability() * 12 / 100;
                    int totalRepair = repair + repair3;
                    int finalDamage = result.getMaxDurability() - totalRepair + 1;
                    if (finalDamage < 0) {
                        finalDamage = 0;
                    }

                    if (finalDamage < result.getDamage()) {
                        result.setDamage(finalDamage);
                        extraCost += 2;
                    }
                }

                Enchantment[] sacrificeEnchantments = sacrifice.getEnchantments();
                boolean compatibleFlag = false;
                boolean incompatibleFlag = false;
                Iterator<Enchantment> enchantmentIterator = Arrays.stream(sacrificeEnchantments).iterator();

                iter:
                while (true) {
                    Enchantment sacrificeEnchantment;
                    do {
                        if (!enchantmentIterator.hasNext()) {
                            if (incompatibleFlag && !compatibleFlag) {
                                return null;
                            }
                            break iter;
                        }

                        sacrificeEnchantment = enchantmentIterator.next();
                    } while (sacrificeEnchantment == null);

                    Enchantment resultEnchantment = result.getEnchantment(sacrificeEnchantment.id);
                    int targetLevel = resultEnchantment != null ? resultEnchantment.getLevel() : 0;
                    int resultLevel = sacrificeEnchantment.getLevel();
                    resultLevel = targetLevel == resultLevel ? resultLevel + 1 : Math.max(resultLevel, targetLevel);
                    boolean compatible = sacrificeEnchantment.canEnchant(target);
                    if (player.isCreative() || Objects.equals(target.getId(), Item.ENCHANTED_BOOK)) {
                        compatible = true;
                    }

                    Iterator<Enchantment> targetEnchIter = Stream.of(target.getEnchantments()).iterator();

                    while (targetEnchIter.hasNext()) {
                        Enchantment targetEnchantment = targetEnchIter.next();
                        if (!Enchantment.equal(targetEnchantment, sacrificeEnchantment) &&
                                (!sacrificeEnchantment.isCompatibleWith(targetEnchantment) ||
                                        !targetEnchantment.isCompatibleWith(sacrificeEnchantment))) {
                            compatible = false;
                            ++extraCost;
                        }
                    }

                    if (!compatible) {
                        incompatibleFlag = true;
                    } else {
                        compatibleFlag = true;
                        if (resultLevel > sacrificeEnchantment.getMaxLevel()) {
                            resultLevel = sacrificeEnchantment.getMaxLevel();
                        }

                        Enchantment usedEnch;
                        if (sacrificeEnchantment.getIdentifier() == null) {
                            usedEnch = Enchantment.getEnchantment(sacrificeEnchantment.getId());
                        } else {
                            usedEnch = Enchantment.getEnchantment(sacrificeEnchantment.getIdentifier());
                        }
                        enchantments.add(usedEnch.setLevel(resultLevel));
                        int rarity;
                        int weight = sacrificeEnchantment.getRarity().getWeight();
                        if (weight >= 10) {
                            rarity = 1;
                        } else if (weight >= 5) {
                            rarity = 2;
                        } else if (weight >= 2) {
                            rarity = 4;
                        } else {
                            rarity = 8;
                        }

                        if (enchantedBook) {
                            rarity = Math.max(1, rarity / 2);
                        }

                        extraCost += rarity * Math.max(0, resultLevel - targetLevel);
                        if (target.getCount() > 1) {
                            extraCost = 40;
                        }
                    }
                }
            }
        }

        //Anvil - rename
        if (StringUtil.isNullOrEmpty(filterString)) {
            player.getLevel().addSound(player, Sound.RANDOM_ANVIL_USE, 1, 1);
            if (target.hasCustomName()) {
                costHelper = 1;
                extraCost += costHelper;
                result.clearCustomName();
            }
        } else {
            costHelper = 1;
            extraCost += costHelper;
            result.setCustomName(filterString);
        }

        int levelCost = getRepairCost(result) + (sacrifice.isNull() ? 0 : getRepairCost(sacrifice));
        resultPair.right(levelCost + extraCost);
        if (extraCost <= 0) {
            result = Item.AIR;
        }

        if (costHelper == extraCost && costHelper > 0 && resultPair.right() >= 40) {
            resultPair.right(39);
        }

        if (resultPair.right() >= 40 && !player.isCreative()) {
            result = Item.AIR;
        }

        if (!result.isNull()) {
            int repairCost = getRepairCost(result);
            if (!sacrifice.isNull() && repairCost < getRepairCost(sacrifice)) {
                repairCost = getRepairCost(sacrifice);
            }

            if (costHelper != extraCost || costHelper == 0) {
                repairCost = repairCost * 2 + 1;
            }

            CompoundTag namedTag = result.getNamedTag();
            if (namedTag == null) {
                namedTag = new CompoundTag();
            }
            namedTag.putInt("RepairCost", repairCost);
            namedTag.remove("ench");
            result.setNamedTag(namedTag);
            if (!enchantments.isEmpty()) {
                result.addEnchantment(enchantments.toArray(Enchantment.EMPTY_ARRAY));
            }
        }
        resultPair.left(result);
        return resultPair;
    }

    public @Nullable Item updateCartographyTableResult(Player player, CartographyTableInventory inventory, String filterString) {
        Server server = player.getServer();
        Item input = inventory.getInput();
        Item additional = inventory.getAdditional();
        Item result = Item.AIR;

        if (input.isNull() && additional.isNull()) {
            return null;
        }

        if (input.getId().equals(Item.PAPER) && additional.isNull()) {
            result = Item.get(Item.EMPTY_MAP);
        }

        if (input.getId().equals(Item.EMPTY_MAP) || input.getId().equals(Item.FILLED_MAP) && additional.isNull()) {
            result = input.clone();
        }

        if ((input.getId().equals(Item.EMPTY_MAP) || input.getId().equals(Item.FILLED_MAP) || input.getId().equals(Item.PAPER)) && additional.getId().equals(Item.COMPASS)) {
            Item item = input.getId().equals(Item.PAPER) ? Item.get(Item.EMPTY_MAP) : input.clone();
            item.setDamage(2);
            result = item;
        }

        if (input.getId().equals(Item.FILLED_MAP) && additional.getId().equals(BlockID.GLASS_PANE)) {
            Item item = input.clone();
            item.setDamage(6);
            result = item;
        }

        if (input.getId().equals(Item.FILLED_MAP) && additional.getId().equals(Item.EMPTY_MAP)) {
            Item item = input.clone();
            item.setCount(2);
            result = item;
        }

        if (input.getId().equals(Item.FILLED_MAP) && additional.getId().equals(Item.PAPER)) {
            ItemFilledMap item = (ItemFilledMap) input.clone();
            Level level = server.getLevel(item.getMapWorld());
            int startX = item.getMapStartX();
            int startZ = item.getMapStartZ();
            int scale = item.getMapScale() + 1;

            item.renderMap(level, startX, startZ, scale);
            item.sendImage(player, item.getMapScale());

            result = item;
        }

        if (!StringUtil.isNullOrEmpty(filterString)) {
            result.setCustomName(filterString);
        } else {
            result.clearCustomName();
        }

        return result;
    }

    private static int getRepairCost(Item item) {
        return item.hasCompoundTag() && item.getNamedTag().contains("RepairCost") ? item.getNamedTag().getInt("RepairCost") : 0;
    }

    private static String getRepairMaterial(Item target) {
        return switch (target.getId()) {
            case ItemID.WOODEN_SWORD, ItemID.WOODEN_PICKAXE, ItemID.WOODEN_SHOVEL, ItemID.WOODEN_AXE, ItemID.WOODEN_HOE ->
                    ItemID.PLANKS;
            case ItemID.IRON_SWORD, ItemID.IRON_PICKAXE, ItemID.IRON_SHOVEL, ItemID.IRON_AXE, ItemID.IRON_HOE, ItemID.IRON_HELMET, ItemID.IRON_CHESTPLATE, ItemID.IRON_LEGGINGS, ItemID.IRON_BOOTS, ItemID.CHAINMAIL_HELMET, ItemID.CHAINMAIL_CHESTPLATE, ItemID.CHAINMAIL_LEGGINGS, ItemID.CHAINMAIL_BOOTS ->
                    ItemID.IRON_INGOT;
            case ItemID.GOLDEN_SWORD, ItemID.GOLDEN_PICKAXE, ItemID.GOLDEN_SHOVEL, ItemID.GOLDEN_AXE, ItemID.GOLDEN_HOE, ItemID.GOLDEN_HELMET, ItemID.GOLDEN_CHESTPLATE, ItemID.GOLDEN_LEGGINGS, ItemID.GOLDEN_BOOTS ->
                    ItemID.GOLD_INGOT;
            case ItemID.DIAMOND_SWORD, ItemID.DIAMOND_PICKAXE, ItemID.DIAMOND_SHOVEL, ItemID.DIAMOND_AXE, ItemID.DIAMOND_HOE, ItemID.DIAMOND_HELMET, ItemID.DIAMOND_CHESTPLATE, ItemID.DIAMOND_LEGGINGS, ItemID.DIAMOND_BOOTS ->
                    ItemID.DIAMOND;
            case ItemID.LEATHER_HELMET, ItemID.LEATHER_CHESTPLATE, ItemID.LEATHER_LEGGINGS, ItemID.LEATHER_BOOTS ->
                    ItemID.LEATHER;
            case ItemID.STONE_SWORD, ItemID.STONE_PICKAXE, ItemID.STONE_SHOVEL, ItemID.STONE_AXE, ItemID.STONE_HOE ->
                    BlockID.COBBLESTONE;
            case ItemID.NETHERITE_SWORD, ItemID.NETHERITE_PICKAXE, ItemID.NETHERITE_SHOVEL, ItemID.NETHERITE_AXE, ItemID.NETHERITE_HOE, ItemID.NETHERITE_HELMET, ItemID.NETHERITE_CHESTPLATE, ItemID.NETHERITE_LEGGINGS, ItemID.NETHERITE_BOOTS ->
                    ItemID.NETHERITE_INGOT;
            case ItemID.ELYTRA -> ItemID.PHANTOM_MEMBRANE;
            default -> BlockID.AIR;
        };
    }
}

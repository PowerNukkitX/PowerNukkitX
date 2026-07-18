package org.powernukkitx.inventory.request;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.event.inventory.CraftItemEvent;
import org.powernukkitx.event.inventory.EnchantItemEvent;
import org.powernukkitx.inventory.EnchantInventory;
import org.powernukkitx.inventory.InputInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.SmithingInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentHelper;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.network.protocol.types.TrimData;
import org.powernukkitx.recipe.Input;
import org.powernukkitx.recipe.MultiRecipe;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.SmithingTransformRecipe;
import org.powernukkitx.recipe.UserDataShapelessRecipe;
import org.powernukkitx.recipe.SmithingTrimRecipe;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.TradeRecipeBuildUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.TrimMaterial;
import org.cloudburstmc.protocol.bedrock.data.TrimPattern;
import org.cloudburstmc.protocol.bedrock.data.inventory.EnchantmentInstance;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemEnchantOption;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftRecipeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Allay Project 2023/12/1
 *
 * @author daoge_cmd | Cool_Loong
 */
@Slf4j
public class CraftRecipeActionProcessor implements ItemStackRequestActionProcessor<CraftRecipeAction> {
    public static final String RECIPE_DATA_KEY = "recipe";
    public static final String ENCH_RECIPE_KEY = "ench_recipe";
    public static final String GRID_CONSUMED_KEY = "grid_consumed";

    public boolean checkTrade(CompoundTag recipeInput, Item input, int subtract) {
        String id = input.getId();
        int damage = input.getDamage();
        int count = input.getCount();
        int required = Math.max(recipeInput.getByte("Count") - subtract, 1);
        if (required > count || recipeInput.getShort("Damage") != damage || !recipeInput.getString("Name").equals(id)) {
            log.error("The trade recipe does not match, expect {} actual {}, count {}", recipeInput, input, required);
            return true;
        }
        if (recipeInput.containsCompound("tag")) {
            CompoundTag tag = recipeInput.getCompound("tag");
            CompoundTag compoundTag = input.getNbt();
            if (!tag.equals(compoundTag)) {
                log.error("The trade recipe tag does not match tag, expect {} actual {}", tag, compoundTag);
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResponse handle(CraftRecipeAction action, Player player, ItemStackRequestContext context) {
        Inventory inventory = player.getTopWindow().orElseGet(player::getCraftingGrid);
        if (action.getRecipeNetId().getRawId() >= EnchantmentHelper.ENCH_RECIPEID) {  //handle ench recipe
            EnchantmentHelper.ItemEnchantOptionWithEntry enchantOptionWithEntry = EnchantmentHelper.RECIPE_MAP.get(action.getRecipeNetId().getRawId());
            if (enchantOptionWithEntry == null) {
                log.error("Can't find enchant recipe from netId {}", action.getRecipeNetId());
                return context.error();
            }
            final ItemEnchantOption enchantOptionData = enchantOptionWithEntry.getOption();
            Item first = inventory.getItem(0);
            if (first.isNull()) {
                log.error("Can't find enchant input!");
                return context.error();
            }
            Item item = first.clone().autoAssignStackNetworkId();
            if (item.getId().equals(Item.BOOK)) item = Item.get(Item.ENCHANTED_BOOK);
            final List<Enchantment> enchantments = new ObjectArrayList<>();
            for (EnchantmentInstance instance : enchantOptionData.getItemEnchants().getEnchants0()) {
                final Enchantment enchantment = Enchantment.get(instance.getEnchantType());
                enchantment.setLevel(instance.getEnchantLevel());
                enchantments.add(enchantment);
            }
            item.addEnchantment(enchantments.toArray(Enchantment.EMPTY_ARRAY));
            EnchantItemEvent event = new EnchantItemEvent((EnchantInventory) inventory, first.clone().autoAssignStackNetworkId(), item, enchantOptionData.getCost(), player);
            Server.getInstance().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                if ((player.getGamemode() & 0x01) == 0) {
                    int lapisCost = enchantOptionWithEntry.getEntry() + 1;
                    if (inventory.getItem(1).getCount() < lapisCost) {
                        return context.error();
                    }
                    player.setExperience(player.getExperience(), player.getExperienceLevel() - lapisCost);
                    inventory.decreaseCount(0, first.getCount());
                    inventory.decreaseCount(1, lapisCost);
                }
                player.getCreativeOutputInventory().setItem(item);
                EnchantmentHelper.RECIPE_MAP.remove(action.getRecipeNetId().getRawId());
                player.regenerateEnchantmentSeed();
                context.put(ENCH_RECIPE_KEY, true);
            }
            return null;
        } else if (action.getRecipeNetId().getRawId() >= TradeRecipeBuildUtils.TRADE_RECIPEID) {//handle village trade recipe
            CompoundTag tradeRecipe = TradeRecipeBuildUtils.RECIPE_MAP.get(action.getRecipeNetId().getRawId());
            if (tradeRecipe == null) {
                log.error("Can't find trade recipe from netId {}", action.getRecipeNetId());
                return context.error();
            }
            if (action.getNumberOfRequestedCrafts() < 1) {
                log.error("Invalid number of requested crafts {}", action.getNumberOfRequestedCrafts());
                return context.error();
            }
            Item first = inventory.getUnclonedItem(0);
            Item second = inventory.getUnclonedItem(1);
            Item output = ItemHelper.read(tradeRecipe.getCompound("sell"));
            int reputation = 0;
            if (inventory.getHolder() instanceof EntityVillagerV2 villager) {
                reputation = villager.getReputation(player);
            }
            output.setCount(output.getCount() * action.getNumberOfRequestedCrafts());
            if (first.isNull() && second.isNull()) {
                log.error("Can't find trade input!");
                return context.error();
            }
            boolean ca = tradeRecipe.contains("buyA");
            boolean cb = tradeRecipe.contains("buyB");

            int reductionA = (int) (reputation * (tradeRecipe.containsFloat("priceMultiplierA") ? tradeRecipe.getFloat("priceMultiplierA") : 0));
            int reductionB = (int) (reputation * (tradeRecipe.containsFloat("priceMultiplierB") ? tradeRecipe.getFloat("priceMultiplierB") : 0));

            if (ca && cb) {
                if ((first.isNull() || second.isNull())) {
                    log.error("Can't find trade input!");
                    return context.error();
                } else {
                    if (checkTrade(tradeRecipe.getCompound("buyA"), first, reductionA)) return context.error();
                    if (checkTrade(tradeRecipe.getCompound("buyB"), second, reductionB)) return context.error();
                    if (tradeRecipe.getInt("uses") + action.getNumberOfRequestedCrafts() > tradeRecipe.getInt("maxUses"))
                        return context.error();
                    player.getCreativeOutputInventory().setItem(output);
                }
            } else if (ca) {
                if (first.isNull()) {
                    log.error("Can't find trade input!");
                    return context.error();
                } else {
                    if (checkTrade(tradeRecipe.getCompound("buyA"), first, reductionA)) return context.error();
                    if (tradeRecipe.getInt("uses") + action.getNumberOfRequestedCrafts() > tradeRecipe.getInt("maxUses"))
                        return context.error();
                    inventory.sendContents(player);
                    player.getCreativeOutputInventory().setItem(output);
                }
            }
            if (ca) {
                int craftsN = action.getNumberOfRequestedCrafts();
                int requiredA = Math.max(tradeRecipe.getCompound("buyA").getByte("Count") - reductionA, 1);
                if (requiredA * craftsN > first.getCount()) {
                    return context.error();
                }
                int requiredB = 0;
                if (cb) {
                    requiredB = Math.max(tradeRecipe.getCompound("buyB").getByte("Count") - reductionB, 1);
                    if (requiredB * craftsN > second.getCount()) {
                        return context.error();
                    }
                }
                inventory.decreaseCount(0, requiredA * craftsN);
                if (cb) {
                    inventory.decreaseCount(1, requiredB * craftsN);
                }
                int traderExp = tradeRecipe.contains("traderExp") ? tradeRecipe.getInt("traderExp") : 0;
                int rewardExp = tradeRecipe.contains("rewardExp") ? tradeRecipe.getInt("rewardExp") : 0;
                player.addExperience(rewardExp * action.getNumberOfRequestedCrafts());
                tradeRecipe.putInt("uses", tradeRecipe.getInt("uses") + action.getNumberOfRequestedCrafts());
                if (inventory.getHolder() instanceof EntityVillagerV2 villager) {
                    villager.addExperience(traderExp * action.getNumberOfRequestedCrafts());
                    villager.addGossip(player.getXUID(), EntityVillagerV2.Gossip.TRADING, 2);
                }
            }
            return null;
        }

        InputInventory craft;
        if (player.getTopWindow().isPresent() && inventory instanceof InputInventory input) {
            craft = input;
        } else {
            craft = player.getCraftingGrid();
        }
        int numberOfRequestedCrafts = action.getNumberOfRequestedCrafts();
        Recipe recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetId().getRawId());
        Input input = craft.getInput();
        Item[][] data = input.getData();
        ArrayList<Item> items = new ArrayList<>();
        for (var d : data) {
            Collections.addAll(items, d);
        }
        CraftItemEvent craftItemEvent = new CraftItemEvent(player, items.toArray(Item.EMPTY_ARRAY), recipe, numberOfRequestedCrafts);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }
        var matched = recipe.match(input);
        if (!matched) {
            if (recipe instanceof SmithingTrimRecipe) {
                return handleSmithingTrim(player, context);
            } else if (recipe instanceof SmithingTransformRecipe smithingTransformRecipe) {
                return handleSmithingUpgrade(smithingTransformRecipe, player, context);
            }
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetId(), recipe.getRecipeId(), recipe.getType());
            return context.error();
        } else {
            Inventory craftInventory = (Inventory) craft;
            for (int slot = 0; slot < craftInventory.getSize(); slot++) {
                Item ingredient = craftInventory.getItem(slot);
                if (ingredient.isNull()) continue;
                if (ingredient.getCount() < numberOfRequestedCrafts) {
                    log.warn("Not enough ingredients in slot {}! Expected: {}, Actual: {}", slot, numberOfRequestedCrafts, ingredient.getCount());
                    return context.error();
                }
            }
            for (int slot = 0; slot < craftInventory.getSize(); slot++) {
                if (!craftInventory.getItem(slot).isNull()) {
                    craftInventory.decreaseCount(slot, numberOfRequestedCrafts);
                }
            }
            context.put(GRID_CONSUMED_KEY, true);
            if (recipe instanceof MultiRecipe && recipe.getResults().isEmpty()) {
                context.put(RECIPE_DATA_KEY, recipe);
            } else if (recipe.getResults().size() == 1) {
                // If the recipe has a single output item, the client will not send a CreateAction; in this case, we will output the item directly to CREATED_OUTPUT in CraftRecipeAction
                // If the recipe has multiple output items, the client will send a CreateAction; in this case, we will output the items to CREATED_OUTPUT within the CreateActionProcessor
                var output = recipe.getResults().getFirst().clone();
                if (recipe instanceof UserDataShapelessRecipe) {
                    for (Item[] row : data) {
                        for (Item inputItem : row) {
                            if (!inputItem.isNull() && inputItem.hasNbt()) {
                                output.setNbtBytes(inputItem.getNbtBytes());
                                break;
                            }
                        }
                    }
                }
                output.setCount(output.getCount() * numberOfRequestedCrafts);
                var createdOutput = player.getCreativeOutputInventory();
                createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
                context.put(RECIPE_DATA_KEY, recipe);
            }
        }
        return null;
    }

    public ActionResponse handleSmithingUpgrade(SmithingTransformRecipe recipe, Player player, ItemStackRequestContext context) {
        Optional<Inventory> topWindow = player.getTopWindow();
        if (topWindow.isEmpty()) {
            log.error("the player's haven't open any inventory!");
            return context.error();
        }
        if (!(topWindow.get() instanceof SmithingInventory smithingInventory)) {
            log.error("the player's haven't open smithing inventory! Instead {}", topWindow.get().getClass().getSimpleName());
            return context.error();
        }
        Item equipment = smithingInventory.getEquipment();
        Item ingredient = smithingInventory.getIngredient();
        Item template = smithingInventory.getTemplate();

        ItemDescriptor expectEquipment = recipe.getBase();
        ItemDescriptor expectIngredient = recipe.getAddition();
        ItemDescriptor expectTemplate = recipe.getTemplate();
        boolean match = expectEquipment.match(equipment);
        match &= expectIngredient.match(ingredient);
        match &= expectTemplate.match(template);
        if (match) {
            Item result = recipe.getResult().clone();
            CompoundTag tag = equipment.getNbt();
            if (tag != null) {
                result.setNbt(tag);
            }
            player.getCreativeOutputInventory().setItem(result);
            smithingInventory.decreaseCount(0, 1);
            smithingInventory.decreaseCount(1, 1);
            smithingInventory.decreaseCount(2, 1);
            return null;
        }
        return context.error();
    }

    public ActionResponse handleSmithingTrim(Player player, ItemStackRequestContext context) {
        Optional<Inventory> topWindow = player.getTopWindow();
        if (topWindow.isEmpty()) {
            log.error("the player's haven't open any inventory!");
            return context.error();
        }
        if (!(topWindow.get() instanceof SmithingInventory smithingInventory)) {
            log.error("the player's haven't open smithing inventory!");
            return context.error();
        }
        Item equipment = smithingInventory.getEquipment();
        Item ingredient = smithingInventory.getIngredient();
        Item template = smithingInventory.getTemplate();

        if (!ingredient.isNull() && !template.isNull()) {
            Optional<TrimPattern> find1 = TrimData.trimPatterns.stream().filter(trimPattern -> template.getId().equals(trimPattern.getItemName())).findFirst();
            Optional<TrimMaterial> find2 = TrimData.trimMaterials.stream().filter(trimMaterial -> ingredient.getId().equals(trimMaterial.getItemName())).findFirst();
            if (equipment.isNull() || find1.isEmpty() || find2.isEmpty()) {
                return context.error();
            }
            TrimPattern trimPattern = find1.get();
            TrimMaterial trimMaterial = find2.get();
            Item result = equipment.clone();
            CompoundTag trim = new CompoundTag().putString("Material", trimMaterial.getMaterialId())
                    .putString("Pattern", trimPattern.getPatternId());
            CompoundTag compound = ingredient.getNbt();
            if (compound == null) {
                compound = result.getOrCreateNbt();
            } else compound = compound.copy(); // Ensure no cached CompoundTags are used double
            compound.putCompound("Trim", trim);
            result.setNbt(compound);
            player.getCreativeOutputInventory().setItem(result);
            smithingInventory.decreaseCount(0, 1);
            smithingInventory.decreaseCount(1, 1);
            smithingInventory.decreaseCount(2, 1);
            return null;
        }
        return context.error();
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RECIPE;
    }
}

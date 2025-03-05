package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.InputInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.PlayerEnchantOptionsPacket;
import cn.nukkit.network.protocol.types.TrimData;
import cn.nukkit.network.protocol.types.TrimMaterial;
import cn.nukkit.network.protocol.types.TrimPattern;
import cn.nukkit.network.protocol.types.itemstack.request.action.ConsumeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftRecipeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.recipe.Input;
import cn.nukkit.recipe.Recipe;
import cn.nukkit.recipe.SmithingTransformRecipe;
import cn.nukkit.recipe.SmithingTrimRecipe;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.TradeRecipeBuildUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public boolean checkTrade(CompoundTag recipeInput, Item input, int subtract) {
        String id = input.getId();
        int damage = input.getDamage();
        int count = input.getCount();
        int required = Math.max(recipeInput.getByte("Count") - subtract, 1);
        if (required > count || recipeInput.getShort("Damage") != damage || !recipeInput.getString("Name").equals(id)) {
            log.error("The trade recipe does not match, expect {} actual {}, count {}", recipeInput, input, required);
            return true;
        }
        if (recipeInput.contains("tag")) {
            CompoundTag tag = recipeInput.getCompound("tag");
            CompoundTag compoundTag = input.getNamedTag();
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
        if (action.getRecipeNetworkId() >= PlayerEnchantOptionsPacket.ENCH_RECIPEID) {  //handle ench recipe
            PlayerEnchantOptionsPacket.EnchantOptionData enchantOptionData = PlayerEnchantOptionsPacket.RECIPE_MAP.get(action.getRecipeNetworkId());
            if (enchantOptionData == null) {
                log.error("Can't find enchant recipe from netId " + action.getRecipeNetworkId());
                return context.error();
            }
            Item first = inventory.getItem(0);
            if (first.isNull()) {
                log.error("Can't find enchant input!");
                return context.error();
            }
            Item item = first.clone().autoAssignStackNetworkId();
            if(item.getId().equals(Item.BOOK)) item = Item.get(Item.ENCHANTED_BOOK);
            List<Enchantment> enchantments = enchantOptionData.enchantments();
            item.addEnchantment(enchantments.toArray(Enchantment.EMPTY_ARRAY));
            EnchantItemEvent event = new EnchantItemEvent((EnchantInventory) inventory, first.clone().autoAssignStackNetworkId(), item, enchantOptionData.minLevel(), player);
            Server.getInstance().getPluginManager().callEvent(event);
            if(!event.isCancelled()) {
                if ((player.getGamemode() & 0x01) == 0) {
                    player.setExperience(player.getExperience(), player.getExperienceLevel() - (enchantOptionData.entry()+1));
                }
                player.getCreativeOutputInventory().setItem(item);
                PlayerEnchantOptionsPacket.RECIPE_MAP.remove(action.getRecipeNetworkId());
                player.regenerateEnchantmentSeed();
                context.put(ENCH_RECIPE_KEY, true);
            }
            return null;
        } else if (action.getRecipeNetworkId() >= TradeRecipeBuildUtils.TRADE_RECIPEID) {//handle village trade recipe
            CompoundTag tradeRecipe = TradeRecipeBuildUtils.RECIPE_MAP.get(action.getRecipeNetworkId());
            if (tradeRecipe == null) {
                log.error("Can't find trade recipe from netId {}", action.getRecipeNetworkId());
                return context.error();
            }
            Item first = inventory.getUnclonedItem(0);
            Item second = inventory.getUnclonedItem(1);
            Item output = NBTIO.getItemHelper(tradeRecipe.getCompound("sell"));
            int reputation = 0;
            if(inventory.getHolder() instanceof EntityVillagerV2 villager) {
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
                    player.getCreativeOutputInventory().setItem(output);
                }
            } else if (ca) {
                if (first.isNull()) {
                    log.error("Can't find trade input!");
                    return context.error();
                } else {
                    if (checkTrade(tradeRecipe.getCompound("buyA"), first, reductionA)) return context.error();
                    if(tradeRecipe.getInt("uses") + action.getNumberOfRequestedCrafts() > tradeRecipe.getInt("maxUses")) return context.error();
                    inventory.sendContents(player);
                    player.getCreativeOutputInventory().setItem(output);
                }
            }
            if(ca) {
                int traderExp = tradeRecipe.contains("traderExp") ? tradeRecipe.getInt("traderExp") : 0;
                int rewardExp = tradeRecipe.contains("rewardExp") ? tradeRecipe.getInt("rewardExp") : 0;
                player.addExperience(rewardExp*action.getNumberOfRequestedCrafts());
                tradeRecipe.putInt("uses", tradeRecipe.getInt("uses") + action.getNumberOfRequestedCrafts());
                if(inventory.getHolder() instanceof EntityVillagerV2 villager) {
                    villager.addExperience(traderExp*action.getNumberOfRequestedCrafts());
                    villager.addGossip(player.getLoginChainData().getXUID(), EntityVillagerV2.Gossip.TRADING, 2);
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
        Recipe recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetworkId());
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
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetworkId(), recipe.getRecipeId(), recipe.getType());
            return context.error();
        } else {
            // Validate if the player has provided enough ingredients
            var itemStackArray = player.getCraftingGrid().getInput().getFlatItems();
            for (int slot = 0; slot < itemStackArray.length; slot++) {
                var ingredient = itemStackArray[slot];
                // Skip empty slot because we have checked item type above
                if (ingredient.isNull()) continue;
                if (ingredient.getCount() < numberOfRequestedCrafts) {
                    log.warn("Not enough ingredients in slot {}! Expected: {}, Actual: {}", slot, numberOfRequestedCrafts, ingredient.getCount());
                    return context.error();
                }
            }
            // Validate the consume action count which client sent
            // 还有一部分检查被放在了ConsumeActionProcessor里面（例如消耗物品数量检查）
            var consumeActions = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1);
            var consumeActionCountNeeded = input.canConsumerItemCount();
            if (consumeActions.size() != consumeActionCountNeeded) {
                log.warn("Mismatched consume action count! Expected: " + consumeActionCountNeeded + ", Actual: " + consumeActions.size() + " on inventory " + craft.getClass().getSimpleName());
                return context.error();
            }
            if (recipe.getResults().size() == 1) {
                // 若配方输出物品为1，客户端将不会发送CreateAction，此时我们直接在CraftRecipeAction输出物品到CREATED_OUTPUT
                // 若配方输出物品为多个，客户端将会发送CreateAction，此时我们将在CreateActionProcessor里面输出物品到CREATED_OUTPUT
                var output = recipe.getResults().getFirst().clone();
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
            log.error("the player's haven't open smithing inventory! Instead " + topWindow.get().getClass().getSimpleName());
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
            CompoundTag tag = equipment.getNamedTag();
            if (tag != null) {
                result.setCompoundTag(tag.copy());
            }
            player.getCreativeOutputInventory().setItem(result);
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
            Optional<TrimPattern> find1 = TrimData.trimPatterns.stream().filter(trimPattern -> template.getId().equals(trimPattern.itemName())).findFirst();
            Optional<TrimMaterial> find2 = TrimData.trimMaterials.stream().filter(trimMaterial -> ingredient.getId().equals(trimMaterial.itemName())).findFirst();
            if (equipment.isNull() || find1.isEmpty() || find2.isEmpty()) {
                return context.error();
            }
            TrimPattern trimPattern = find1.get();
            TrimMaterial trimMaterial = find2.get();
            Item result = equipment.clone();
            CompoundTag trim = new CompoundTag().putString("Material", trimMaterial.materialId())
                    .putString("Pattern", trimPattern.patternId());
            CompoundTag compound = ingredient.getNamedTag();
            if (compound == null) {
                compound = result.getOrCreateNamedTag();
            } else compound = compound.copy(); // Ensure no cached CompoundTags are used double
            compound.putCompound("Trim", trim);
            result.setNamedTag(compound);
            player.getCreativeOutputInventory().setItem(result);
            return null;
        }
        return context.error();
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RECIPE;
    }

    public static List<ConsumeAction> findAllConsumeActions(ItemStackRequestAction[] actions, int startIndex) {
        var found = new ArrayList<ConsumeAction>();
        for (int i = startIndex; i < actions.length; i++) {
            var action = actions[i];
            if (action instanceof ConsumeAction consumeAction) {
                found.add(consumeAction);
            }
        }
        return found;
    }
}

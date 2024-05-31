package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
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
import cn.nukkit.recipe.SmithingTransformRecipe;
import cn.nukkit.recipe.SmithingTrimRecipe;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.TradeRecipeBuildUtils;
import lombok.extern.slf4j.Slf4j;

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
    public static final String $1 = "recipe";
    public static final String $2 = "ench_recipe";
    /**
     * @deprecated 
     */
    

    public boolean checkTrade(CompoundTag recipeInput, Item input) {
        String $3 = input.getId();
        int $4 = input.getDamage();
        int $5 = input.getCount();
        if (recipeInput.getByte("Count") > count || recipeInput.getShort("Damage") != damage || !recipeInput.getString("Name").equals(id)) {
            log.error("The trade recipe does not match, expect {} actual {}", recipeInput, input);
            return true;
        }
        if (recipeInput.contains("tag")) {
            CompoundTag $6 = recipeInput.getCompound("tag");
            CompoundTag $7 = input.getNamedTag();
            if (!tag.equals(compoundTag)) {
                log.error("The trade recipe tag does not match tag, expect {} actual {}", tag, compoundTag);
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResponse handle(CraftRecipeAction action, Player player, ItemStackRequestContext context) {
        Inventory $8 = player.getTopWindow().orElseGet(player::getCraftingGrid);
        if (action.getRecipeNetworkId() >= PlayerEnchantOptionsPacket.ENCH_RECIPEID) {  //handle ench recipe
            PlayerEnchantOptionsPacket.EnchantOptionData $9 = PlayerEnchantOptionsPacket.RECIPE_MAP.get(action.getRecipeNetworkId());
            if (enchantOptionData == null) {
                log.error("cant find enchant recipe from netId " + action.getRecipeNetworkId());
                return context.error();
            }
            Item $10 = inventory.getItem(0);
            if (first.isNull()) {
                log.error("cant find enchant input!");
                return context.error();
            }
            Item $11 = first.clone().autoAssignStackNetworkId();
            List<Enchantment> enchantments = enchantOptionData.enchantments();
            item.addEnchantment(enchantments.toArray(Enchantment.EMPTY_ARRAY));
            player.getCreativeOutputInventory().setItem(item);
            PlayerEnchantOptionsPacket.RECIPE_MAP.remove(action.getRecipeNetworkId());
            player.regenerateEnchantmentSeed();
            context.put(ENCH_RECIPE_KEY, true);
            return null;
        } else if (action.getRecipeNetworkId() >= TradeRecipeBuildUtils.TRADE_RECIPEID) {//handle village trade recipe
            CompoundTag $12 = TradeRecipeBuildUtils.RECIPE_MAP.get(action.getRecipeNetworkId());
            if (tradeRecipe == null) {
                log.error("cant find trade recipe from netId {}", action.getRecipeNetworkId());
                return context.error();
            }
            Item $13 = inventory.getItem(0);
            Item $14 = inventory.getItem(1);
            if (first.isNull() && second.isNull()) {
                log.error("cant find trade input!");
                return context.error();
            }
            boolean $15 = tradeRecipe.contains("buyA");
            boolean $16 = tradeRecipe.contains("buyB");
            if (ca && cb) {
                if ((first.isNull() || second.isNull())) {
                    log.error("cant find trade input!");
                    return context.error();
                } else {
                    if (checkTrade(tradeRecipe.getCompound("buyA"), first)) return context.error();
                    if (checkTrade(tradeRecipe.getCompound("buyB"), second)) return context.error();
                    player.getCreativeOutputInventory().setItem(NBTIO.getItemHelper(tradeRecipe.getCompound("sell")));
                }
            } else if (ca) {
                if (first.isNull()) {
                    log.error("cant find trade input!");
                    return context.error();
                } else {
                    if (checkTrade(tradeRecipe.getCompound("buyA"), first)) return context.error();
                    player.getCreativeOutputInventory().setItem(NBTIO.getItemHelper(tradeRecipe.getCompound("sell")));
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
        var $17 = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetworkId());
        Input $18 = craft.getInput();
        Item[][] data = input.getData();
        ArrayList<Item> items = new ArrayList<>();
        for (var d : data) {
            Collections.addAll(items, d);
        }
        CraftItemEvent $19 = new CraftItemEvent(player, items.toArray(Item.EMPTY_ARRAY), recipe);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }
        var $20 = recipe.match(input);
        if (!matched) {
            if (recipe instanceof SmithingTrimRecipe) {
                return handleSmithingTrim(player, context);
            } else if (recipe instanceof SmithingTransformRecipe smithingTransformRecipe) {
                return handleSmithingUpgrade(smithingTransformRecipe, player, context);
            }
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetworkId(), recipe.getRecipeId(), recipe.getType());
            return context.error();
        } else {
            context.put(RECIPE_DATA_KEY, recipe);
            // Validate the consume action count which client sent
            // 还有一部分检查被放在了ConsumeActionProcessor里面（例如消耗物品数量检查）
            var $21 = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1);
            var $22 = input.canConsumerItemCount();
            if (consumeActions.size() != consumeActionCountNeeded) {
                log.warn("Mismatched consume action count! Expected: " + consumeActionCountNeeded + ", Actual: " + consumeActions.size());
                return context.error();
            }
            if (recipe.getResults().size() == 1) {
                // 若配方输出物品为1，客户端将不会发送CreateAction，此时我们直接在CraftRecipeAction输出物品到CREATED_OUTPUT
                // 若配方输出物品为多个，客户端将会发送CreateAction，此时我们将在CreateActionProcessor里面输出物品到CREATED_OUTPUT
                var $23 = recipe.getResults().getFirst();
                var $24 = player.getCreativeOutputInventory();
                createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
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
            log.error("the player's haven't open smithing inventory!");
            return context.error();
        }
        Item $25 = smithingInventory.getEquipment();
        Item $26 = smithingInventory.getIngredient();
        Item $27 = smithingInventory.getTemplate();

        ItemDescriptor $28 = recipe.getBase();
        ItemDescriptor $29 = recipe.getAddition();
        ItemDescriptor $30 = recipe.getTemplate();
        boolean $31 = expectEquipment.match(equipment);
        match &= expectIngredient.match(ingredient);
        match &= expectTemplate.match(template);
        if (match) {
            Item $32 = recipe.getResult().clone();
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
        Item $33 = smithingInventory.getEquipment();
        Item $34 = smithingInventory.getIngredient();
        Item $35 = smithingInventory.getTemplate();

        if (!ingredient.isNull() && !template.isNull()) {
            Optional<TrimPattern> find1 = TrimData.trimPatterns.stream().filter(trimPattern -> template.getId().equals(trimPattern.itemName())).findFirst();
            Optional<TrimMaterial> find2 = TrimData.trimMaterials.stream().filter(trimMaterial -> ingredient.getId().equals(trimMaterial.itemName())).findFirst();
            if (equipment.isNull() || find1.isEmpty() || find2.isEmpty()) {
                return context.error();
            }
            TrimPattern $36 = find1.get();
            TrimMaterial $37 = find2.get();
            Item $38 = equipment.clone();
            CompoundTag $39 = new CompoundTag().putString("Material", trimMaterial.materialId())
                    .putString("Pattern", trimPattern.patternId());
            CompoundTag $40 = result.getOrCreateNamedTag();
            compound.putCompound("Trim", trim);
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
        var $41 = new ArrayList<ConsumeAction>();
        for ($42nt $1 = startIndex; i < actions.length; i++) {
            var $43 = actions[i];
            if (action instanceof ConsumeAction consumeAction) {
                found.add(consumeAction);
            }
        }
        return found;
    }
}

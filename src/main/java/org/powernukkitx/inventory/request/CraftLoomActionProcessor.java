package org.powernukkitx.inventory.request;

import org.powernukkitx.Player;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.LoomInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBanner;
import org.powernukkitx.item.ItemBannerPattern;
import org.powernukkitx.item.ItemDye;
import org.powernukkitx.network.protocol.types.BannerPattern;
import org.powernukkitx.network.protocol.types.BannerPatternType;
import org.powernukkitx.utils.DyeColor;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftLoomAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author CoolLoong
 */
@Slf4j
public class CraftLoomActionProcessor implements ItemStackRequestActionProcessor<CraftLoomAction> {
    private static final int MAX_PATTERNS = 6;
    private static final int TYPE_OMINOUS = 1;

    // Patterns that cannot be woven from dye alone, they need the matching banner pattern item in the
    // material slot. The item itself is not consumed.
    private static final Set<BannerPatternType> PATTERN_ITEM_REQUIRED = EnumSet.of(
        BannerPatternType.FLOWER,
        BannerPatternType.CREEPER,
        BannerPatternType.SKULL,
        BannerPatternType.MOJANG,
        BannerPatternType.GLOBE,
        BannerPatternType.PIGLIN,
        BannerPatternType.FLOW,
        BannerPatternType.GUSTER
    );

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_LOOM;
    }

    @Nullable
    @Override
    public ActionResponse handle(CraftLoomAction action, Player player, ItemStackRequestContext context) {
        Optional<Inventory> topWindow = player.getTopWindow();
        if (topWindow.isEmpty()) {
            log.error("the player's haven't open any inventory!");
            return context.error();
        }
        if (!(topWindow.get() instanceof LoomInventory loomInventory)) {
            log.error("the player's haven't open loom inventory!");
            return context.error();
        }
        Item banner = loomInventory.getBanner();
        Item dye = loomInventory.getDye();
        if (!(banner instanceof ItemBanner itemBanner) || !(dye instanceof ItemDye itemDye)) {
            log.debug("{}: loom craft without a banner and a dye", player.getName());
            return context.error();
        }
        if (itemBanner.getType() == TYPE_OMINOUS) {
            log.debug("{}: ominous banners cannot be woven", player.getName());
            return context.error();
        }
        if (itemBanner.getPatternsSize() >= MAX_PATTERNS) {
            log.debug("{}: banner already carries {} patterns", player.getName(), itemBanner.getPatternsSize());
            return context.error();
        }

        BannerPatternType patternType = BannerPatternType.fromCode(action.getPatternNameId());
        if (patternType == null) {
            log.debug("{}: unknown loom pattern {}", player.getName(), action.getPatternNameId());
            return context.error();
        }

        Item pattern = loomInventory.getPattern();
        if (pattern instanceof ItemBannerPattern itemBannerPattern) {
            if (itemBannerPattern.getPatternType() != patternType) {
                log.debug("{}: loom pattern {} does not match the pattern item {}", player.getName(), patternType, itemBannerPattern.getPatternType());
                return context.error();
            }
        } else if (PATTERN_ITEM_REQUIRED.contains(patternType)) {
            log.debug("{}: loom pattern {} requires its banner pattern item", player.getName(), patternType);
            return context.error();
        }

        DyeColor dyeColor = itemDye.getDyeColor();
        int times = Math.max(1, action.getNumCrafts());
        if (times > banner.getCount() || times > dye.getCount() || times > itemBanner.getMaxStackSize()) {
            return context.error();
        }
        ItemBanner result = (ItemBanner) banner.clone();
        result.setCount(times);
        result.addPattern(new BannerPattern(patternType, dyeColor));
        player.getCreativeOutputInventory().setItem(result);
        loomInventory.decreaseCount(0, times);
        loomInventory.decreaseCount(1, times);
        context.markServerConsumed(ContainerEnumName.LOOM_INPUT_CONTAINER, ContainerEnumName.LOOM_DYE_CONTAINER, ContainerEnumName.LOOM_MATERIAL_CONTAINER);
        return null;
    }
}

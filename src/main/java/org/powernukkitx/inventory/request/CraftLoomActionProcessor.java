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
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftLoomAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author CoolLoong
 */
@Slf4j
public class CraftLoomActionProcessor implements ItemStackRequestActionProcessor<CraftLoomAction> {
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
        if ((banner == null || banner.isNull()) || (dye == null || dye.isNull())) {
            return context.error();
        }

        Item pattern = loomInventory.getPattern();
        BannerPatternType patternType = BannerPatternType.fromCode(action.getPatternId());
        if (pattern instanceof ItemBannerPattern itemBannerPattern && action.getPatternId() != null && !action.getPatternId().isBlank()) {
            patternType = itemBannerPattern.getPatternType();
            if (!action.getPatternId().equals(patternType.getCode())) return context.error();
        }
        DyeColor dyeColor = DyeColor.BLACK;
        if (dye instanceof ItemDye itemDye) {
            dyeColor = itemDye.getDyeColor();
        }
        int times = Math.max(1, action.getTimesCrafted());
        if (times > banner.getCount() || times > dye.getCount()) {
            return context.error();
        }
        ItemBanner result = (ItemBanner) banner.clone();
        result.setCount(times);
        if (patternType != null) {
            result.addPattern(new BannerPattern(patternType, dyeColor));
        } else {
            result.setBaseColor(dyeColor);
        }
        player.getCreativeOutputInventory().setItem(result);
        ConsumeActionHelper.consume(loomInventory, 0, times);
        ConsumeActionHelper.consume(loomInventory, 1, times);
        return null;
    }
}

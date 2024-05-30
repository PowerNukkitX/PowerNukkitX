package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBanner;
import cn.nukkit.item.ItemBannerPattern;
import cn.nukkit.item.ItemDye;
import cn.nukkit.network.protocol.types.BannerPattern;
import cn.nukkit.network.protocol.types.BannerPatternType;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftLoomAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.utils.DyeColor;
import lombok.extern.slf4j.Slf4j;
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
        BannerPatternType patternType = null;
        if (pattern instanceof ItemBannerPattern itemBannerPattern && action.getPatternId() != null && !action.getPatternId().isBlank()) {
            patternType = itemBannerPattern.getPatternType();
            if (!action.getPatternId().equals(patternType.getCode())) return context.error();
        }
        DyeColor dyeColor = DyeColor.BLACK;
        if (dye instanceof ItemDye itemDye) {
            dyeColor = itemDye.getDyeColor();
        }
        ItemBanner result = new ItemBanner();
        if (patternType != null) {
            result.addPattern(new BannerPattern(patternType, dyeColor));
        } else {
            result.setBaseColor(dyeColor);
        }
        player.getCreativeOutputInventory().setItem(result);
        return null;
    }
}

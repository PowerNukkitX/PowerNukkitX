package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemDye extends Item {

    @Deprecated
    public static final int WHITE = DyeColor.WHITE.getItemDyeMeta();
    @Deprecated
    public static final int ORANGE = DyeColor.ORANGE.getItemDyeMeta();
    @Deprecated
    public static final int MAGENTA = DyeColor.MAGENTA.getItemDyeMeta();
    @Deprecated
    public static final int LIGHT_BLUE = DyeColor.LIGHT_BLUE.getItemDyeMeta();
    @Deprecated
    public static final int YELLOW = DyeColor.YELLOW.getItemDyeMeta();
    @Deprecated
    public static final int LIME = DyeColor.LIME.getItemDyeMeta();
    @Deprecated
    public static final int PINK = DyeColor.PINK.getItemDyeMeta();
    @Deprecated
    public static final int GRAY = DyeColor.GRAY.getItemDyeMeta();
    @Deprecated
    public static final int LIGHT_GRAY = DyeColor.LIGHT_GRAY.getItemDyeMeta();
    @Deprecated
    public static final int CYAN = DyeColor.CYAN.getItemDyeMeta();
    @Deprecated
    public static final int PURPLE = DyeColor.PURPLE.getItemDyeMeta();
    @Deprecated
    public static final int BLUE = DyeColor.BLUE.getItemDyeMeta();
    @Deprecated
    public static final int BROWN = DyeColor.BROWN.getItemDyeMeta();
    @Deprecated
    public static final int GREEN = DyeColor.GREEN.getItemDyeMeta();
    @Deprecated
    public static final int RED = DyeColor.RED.getItemDyeMeta();
    @Deprecated
    public static final int BLACK = DyeColor.BLACK.getItemDyeMeta();
    @Deprecated


    public static final int BONE_MEAL = DyeColor.BONE_MEAL.getItemDyeMeta();

    public ItemDye() {
        this(0, 1);
    }

    public ItemDye(Integer meta) {
        this(meta, 1);
    }

    public ItemDye(DyeColor dyeColor) {
        this(dyeColor.getItemDyeMeta(), 1);
    }

    public ItemDye(DyeColor dyeColor, int amount) {
        this(dyeColor.getItemDyeMeta(), amount);
    }

    public ItemDye(Integer meta, int amount) {
        super(DYE, meta, amount, meta <= 15 ? DyeColor.getByDyeData(meta).getDyeName() : DyeColor.getByDyeData(meta).getName() + " Dye");

        if (this.meta == DyeColor.BROWN.getDyeData()) {
            this.block = Block.get(BlockID.COCOA);
        }
    }


    protected ItemDye(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }


    @Override
    public boolean isFertilizer() {
        return getId() == DYE && getDyeColor().equals(DyeColor.BONE_MEAL);
    }


    public boolean isLapisLazuli() {
        return getId() == DYE && getDyeColor().equals(DyeColor.BLUE);
    }


    public boolean isCocoaBeans() {
        return getId() == DYE && getDyeColor().equals(DyeColor.BROWN);
    }

    @Deprecated
    public static BlockColor getColor(int meta) {
        return DyeColor.getByDyeData(meta).getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeData(meta);
    }

    @Deprecated
    public static String getColorName(int meta) {
        return DyeColor.getByDyeData(meta).getName();
    }
}

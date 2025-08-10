package cn.nukkit.item.customitem.data;

/**
 * Determines which animation plays when using an item.
 *
 * @see <a href="https://wiki.bedrock.dev/items/item-components#use-animation">Bedrock Wiki</a>
 */
public enum ItemUseAnimation {
    EAT,
    DRINK,
    BOW,
    BLOCK,
    CAMERA,
    CROSSBOW,
    NONE,
    BRUSH,
    SPEAR,
    SPYGLASS;


    public String asString() {
        return this.name().toLowerCase();
    }

    public int getId() {
        return this.ordinal() + 1;
    }
}

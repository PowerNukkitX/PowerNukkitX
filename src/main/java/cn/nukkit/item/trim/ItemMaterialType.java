package cn.nukkit.item.trim;

/**
 * @author glorydark
 * @date {2023/8/9} {17:26}
 */
public enum ItemMaterialType {

    MATERIAL_QUARTZ("quartz"),
    MATERIAL_IRON("iron"),
    MATERIAL_NETHERITE("netherite"),
    MATERIAL_REDSTONE("redstone"),
    MATERIAL_COPPER("copper"),
    MATERIAL_GOLD("gold"),
    MATERIAL_EMERALD("emerald"),
    MATERIAL_LAPIS("lapis"),
    MATERIAL_AMETHYST("amethyst");

    private final String materialName;

    ItemMaterialType(String input){
        this.materialName = input;
    }

}

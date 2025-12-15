package cn.nukkit.item.customitem.data;


/**
 * Controls the major group of custom items in the creative inventory.
 * <br>Possible values:
 * 0. NONE
 * 1. ANVIL
 * 2. ARROW
 * 3. AXE
 * 4. BANNER
 * 5. BANNER_PATTERN
 * 6. BED
 * 7. BOAT
 * 8. BOOTS
 * 9. BUTTONS
 * 10. CHALKBOARD
 * 11. CHEST
 * 12. CHESTPLATE
 * 13. CONCRETE
 * 14. CONCRETE_POWDER
 * 15. COOKED_FOOD
 * 16. COPPER
 * 17. CORAL
 * 18. CORAL_DECORATIONS
 * 19. CROP
 * 20. DOOR
 * 21. DYE
 * 22. ENCHANTED_BOOK
 * 23. FENCE
 * 24. FENCE_GATE
 * 25. FIREWORK
 * 26. FIREWORK_STARS
 * 27. FLOWER
 * 28. GLASS
 * 29. GLASS_PANE
 * 30. GLAZED_TERRACOTTA
 * 31. GRASS
 * 32. HELMET
 * 33. HOE
 * 34. HORSE_ARMOR
 * 35. LEAVES
 * 36. LEGGINGS
 * 37. LINGERING_POTION
 * 38. LOG
 * 39. MINECART
 * 40. MISC_FOOD
 * 41. MOB_EGGS
 * 42. MONSTER_STONE_EGG
 * 43. MUSHROOM
 * 44. NETHERWART_BLOCK
 * 45. ORE
 * 46. PERMISSION
 * 47. PICKAXE
 * 48. PLANKS
 * 49. POTION
 * 50. PRESSURE_PLATE
 * 51. RAIL
 * 52. RAW_FOOD
 * 53. RECORD
 * 54. SANDSTONE
 * 55. SAPLING
 * 56. SEED
 * 57. SHOVEL
 * 58. SHULKER_BOX
 * 59. SIGN
 * 60. SKULL
 * 61. SLAB
 * 62. SPLASH_POTION
 * 63. STAINED_CLAY
 * 64. STAIRS
 * 65. STONE
 * 66. STONE_BRICK
 * 67. SWORD
 * 68. TRAPDOOR
 * 69. WALLS
 * 70. WOOD
 * 71. WOOL
 * 72. WOOL_CARPET
 * 73. CANDLES
 * 74. GOAT_HORN
 * <p>
 * Represents the major creative inventory group of a custom item.
 * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-categories">bedrock wiki</a>
 */
public enum CreativeGroup {
    NONE(""),

    ANVIL("itemGroup.name.anvil"),

    ARROW("itemGroup.name.arrow"),

    AXE("itemGroup.name.axe"),

    BANNER("itemGroup.name.banner"),

    BANNER_PATTERN("itemGroup.name.banner_pattern"),

    BED("itemGroup.name.bed"),

    BOAT("itemGroup.name.boat"),

    BOOTS("itemGroup.name.boots"),

    BUTTONS("itemGroup.name.buttons"),

    CHALKBOARD("itemGroup.name.chalkboard"),

    CHEST("itemGroup.name.chest"),

    CHESTPLATE("itemGroup.name.chestplate"),

    CONCRETE("itemGroup.name.concrete"),

    CONCRETE_POWDER("itemGroup.name.concretePowder"),

    COOKED_FOOD("itemGroup.name.cookedFood"),

    COOPPER("itemGroup.name.copper"),

    CORAL("itemGroup.name.coral"),

    CORAL_DECORATIONS("itemGroup.name.coral_decorations"),

    CROP("itemGroup.name.crop"),

    DOOR("itemGroup.name.door"),

    DYE("itemGroup.name.dye"),

    ENCHANTED_BOOK("itemGroup.name.enchantedBook"),

    FENCE("itemGroup.name.fence"),

    FENCE_GATE("itemGroup.name.fenceGate"),

    FIREWORK("itemGroup.name.firework"),

    FIREWORK_STARS("itemGroup.name.fireworkStars"),

    FLOWER("itemGroup.name.flower"),

    GLASS("itemGroup.name.glass"),

    GLASS_PANE("itemGroup.name.glassPane"),

    GLAZED_TERRACOTTA("itemGroup.name.glazedTerracotta"),

    GRASS("itemGroup.name.grass"),

    HELMET("itemGroup.name.helmet"),

    HOE("itemGroup.name.hoe"),

    HORSE_ARMOR("itemGroup.name.horseArmor"),

    LEAVES("itemGroup.name.leaves"),

    LEGGINGS("itemGroup.name.leggings"),

    LINGERING_POTION("itemGroup.name.lingeringPotion"),

    LOG("itemGroup.name.log"),

    MINECRAFT("itemGroup.name.minecart"),

    MISC_FOOD("itemGroup.name.miscFood"),

    MOB_EGGS("itemGroup.name.mobEgg"),

    MONSTER_STONE_EGG("itemGroup.name.monsterStoneEgg"),

    MUSHROOM("itemGroup.name.mushroom"),

    NETHERWART_BLOCK("itemGroup.name.netherWartBlock"),

    ORE("itemGroup.name.ore"),

    PERMISSION("itemGroup.name.permission"),

    PICKAXE("itemGroup.name.pickaxe"),

    PLANKS("itemGroup.name.planks"),

    POTION("itemGroup.name.potion"),

    PRESSURE_PLATE("itemGroup.name.pressurePlate"),

    RAIL("itemGroup.name.rail"),

    RAW_FOOD("itemGroup.name.rawFood"),

    RECORD("itemGroup.name.record"),

    SANDSTONE("itemGroup.name.sandstone"),

    SAPLING("itemGroup.name.sapling"),

    SEED("itemGroup.name.seed"),

    SHOVEL("itemGroup.name.shovel"),

    SHULKER_BOX("itemGroup.name.shulkerBox"),

    SIGN("itemGroup.name.sign"),

    SKULL("itemGroup.name.skull"),

    SLAB("itemGroup.name.slab"),

    SLASH_POTION("itemGroup.name.splashPotion"),

    STAINED_CLAY("itemGroup.name.stainedClay"),

    STAIRS("itemGroup.name.stairs"),

    STONE("itemGroup.name.stone"),

    STONE_BRICK("itemGroup.name.stoneBrick"),

    SWORD("itemGroup.name.sword"),

    TRAPDOOR("itemGroup.name.trapdoor"),

    WALLS("itemGroup.name.walls"),

    WOOD("itemGroup.name.wood"),

    WOOL("itemGroup.name.wool"),

    WOOL_CARPET("itemGroup.name.woolCarpet"),

    CANDLES("itemGroup.name.candles"),

    GOAT_HORN("itemGroup.name.goatHorn");

    private final String groupName;

    CreativeGroup(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}

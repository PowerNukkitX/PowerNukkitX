// noinspection JSUnresolvedFunction,JSUnresolvedVariable

import {id} from ":plugin-id"
import {JavaClassBuilder} from ":jvm"
import {CommonJSPlugin as CommonJSPluginClass} from "cn.nukkit.plugin.CommonJSPlugin"
import {String as JString} from "java.lang.String";
import {Item} from "cn.nukkit.item.Item";
import {Food} from "cn.nukkit.item.food.Food";
import {FoodNormal} from "cn.nukkit.item.food.FoodNormal";
import {ItemCustomTool} from "cn.nukkit.item.customitem.ItemCustomTool";
import {ItemCustomArmor} from "cn.nukkit.item.customitem.ItemCustomArmor";
import {CustomItemDefinition} from "cn.nukkit.item.customitem.CustomItemDefinition";
import {ItemCreativeCategory} from "cn.nukkit.item.customitem.data.ItemCreativeCategory";

const JPrimitiveBoolean = Java.type("boolean");
const JPrimitiveInt = Java.type("int");
const jsPlugin = CommonJSPluginClass.jsPluginIdMap.get(id);

/**
 * 创造物品栏分类
 */
export class CreativeInventoryType {
    static CONSTRUCTOR = new CreativeInventoryType(1);
    static NATURE = new CreativeInventoryType(2);
    static EQUIPMENT = new CreativeInventoryType(3);
    static ITEM = new CreativeInventoryType(4);

    /**
     * @private
     * @param typeId 创造物品栏分类ID
     */
    constructor(typeId) {
        this.typeId = typeId;
    }
}

/**
 * 工具种类
 */
export class ToolType {
    static NONE = new ToolType(ItemCustomTool.TYPE_NONE);
    static SWORD = new ToolType(ItemCustomTool.TYPE_SWORD);
    static SHOVEL = new ToolType(ItemCustomTool.TYPE_SHOVEL);
    static PICKAXE = new ToolType(ItemCustomTool.TYPE_PICKAXE);
    static AXE = new ToolType(ItemCustomTool.TYPE_AXE);
    static HOE = new ToolType(ItemCustomTool.TYPE_HOE);
    static SHEARS = new ToolType(ItemCustomTool.TYPE_SHEARS);

    /**
     * @private
     * @param typeId 工具种类ID
     */
    constructor(typeId) {
        this.typeId = typeId;
    }
}

/**
 * 工具分级
 */
export class ToolTier {
    static WOODEN = new ToolTier(ItemCustomTool.TIER_WOODEN);
    static GOLD = new ToolTier(ItemCustomTool.TIER_GOLD);
    static STONE = new ToolTier(ItemCustomTool.TIER_STONE);
    static IRON = new ToolTier(ItemCustomTool.TIER_IRON);
    static DIAMOND = new ToolTier(ItemCustomTool.TIER_DIAMOND);
    static NETHERITE = new ToolTier(ItemCustomTool.TIER_NETHERITE);

    /**
     * @private
     * @param tierId 工具分级ID
     */
    constructor(tierId) {
        this.tierId = tierId;
    }
}

/**
 * 防具种类
 */
export class ArmorType {
    static HELMET = new ArmorType(0);
    static CHESTPLATE = new ArmorType(1);
    static LEGGINGS = new ArmorType(2);
    static BOOTS = new ArmorType(3);

    /**
     * @private
     * @param typeId 防具种类ID
     */
    constructor(typeId) {
        this.typeId = typeId;
    }
}

/**
 * 防具分级
 */
export class ArmorTier {
    static LEATHER = new ArmorTier(ItemCustomArmor.TIER_LEATHER);
    static CHAIN = new ArmorTier(ItemCustomArmor.TIER_CHAIN);
    static IRON = new ArmorTier(ItemCustomArmor.TIER_IRON);
    static GOLD = new ArmorTier(ItemCustomArmor.TIER_GOLD);
    static DIAMOND = new ArmorTier(ItemCustomArmor.TIER_DIAMOND);
    static NETHERITE = new ArmorTier(ItemCustomArmor.TIER_NETHERITE);
    static OTHER = new ArmorTier(ItemCustomArmor.TIER_OTHER);

    /**
     * @private
     * @param tierId 防具分级ID
     */
    constructor(tierId) {
        this.tierId = tierId;
    }
}

/**
 * 用于操作方块和物品的工具类
 */
export class BlockItemUtil {
    /**
     * 注册一个简单的自定义物品
     * @param id {string} 物品ID
     * @param name {string} 物品ID
     * @param type {CreativeInventoryType} 创造物品栏分类
     * @param textureName {string} 贴图名称，在材质包中可以指定
     * @param stackSize {number} 最大堆叠
     * @param canOnOffhand {boolean} 是否可以装备在副手
     * @param handEquipped {boolean} 控制第三人称手持物品的显示方式
     * @param foil {boolean} 自定义物品是否带有附魔光辉效果
     */
    static registerSimpleItem(id, name, type, textureName, stackSize, canOnOffhand, handEquipped, foil) {
        const jClassBuilder = new JavaClassBuilder(id.replaceAll(":", "."), "cn.nukkit.item.customitem.ItemCustom");
        jClassBuilder.setJSDelegate({
            new() {
                return [id, name, textureName];
            },
            constructor(javaThis) {
            },
            getDefinition(javaThis) {
                return CustomItemDefinition
                    .simpleBuilder(javaThis, ItemCreativeCategory.fromID(type.typeId))
                    .allowOffHand(canOnOffhand)
                    .handEquipped(handEquipped)
                    .foil(foil)
                    .build();
            },
            getMaxStackSize() {
                return stackSize;
            }
        }).addJavaConstructor("new", "constructor", [JString, JString, JString])
            .addJavaMethod("getMaxStackSize", "getMaxStackSize", JPrimitiveInt)
            .addJavaMethod("getDefinition", "getDefinition", CustomItemDefinition);
        Item.registerCustomItem(jClassBuilder.compileToJavaClass());
    }

    /**
     * 注册一个食品物品
     * @param id {string} 物品ID
     * @param name {string} 物品ID
     * @param type {CreativeInventoryType} 创造物品栏分类
     * @param textureName {string} 贴图名称，在材质包中可以指定
     * @param stackSize {number} 最大堆叠
     * @param canOnOffhand {boolean} 是否可以装备在副手
     * @param consumeTime {number} 食用耗时
     * @param isDrink {boolean} 是否是饮品，为false则为食品，如true则为饮品（药水等）
     * @param canAlwaysEat {boolean} 是否能满饥饿度了还吃
     * @param restoreFood {number} 恢复的饥饿值
     * @param restoreSaturation {number} 恢复的饱食度
     * @param handEquipped {boolean} 控制第三人称手持物品的显示方式
     * @param foil {boolean} 自定义物品是否带有附魔光辉效果
     */
    static registerFoodItem(id, name, type, textureName,
                            stackSize, canOnOffhand, consumeTime, isDrink, canAlwaysEat,
                            restoreFood, restoreSaturation, handEquipped, foil) {
        const jClassBuilder = new JavaClassBuilder(id.replaceAll(":", "."), "cn.nukkit.item.customitem.ItemCustomEdible");
        jClassBuilder.setJSDelegate({
            new() {
                return [id, name, textureName];
            },
            constructor(javaThis) {
            },
            getDefinition(javaThis) {
                return CustomItemDefinition
                    .edibleBuilder(javaThis, ItemCreativeCategory.fromID(type.typeId))
                    .allowOffHand(canOnOffhand)
                    .handEquipped(handEquipped)
                    .foil(foil)
                    .build();
            },
            getMaxStackSize() {
                return stackSize;
            },
            getEatTick() {
                return consumeTime;
            },
            isDrink() {
                return isDrink;
            },
            canAlwaysEat() {
                return canAlwaysEat;
            }
        }).addJavaConstructor("new", "constructor", [JString, JString, JString])
            .addJavaMethod("getMaxStackSize", "getMaxStackSize", JPrimitiveInt)
            .addJavaMethod("getEatTick", "getEatTick", JPrimitiveInt)
            .addJavaMethod("isDrink", "isDrink", JPrimitiveBoolean)
            .addJavaMethod("canAlwaysEat", "canAlwaysEat", JPrimitiveBoolean)
            .addJavaMethod("getDefinition", "getDefinition", CustomItemDefinition);
        Item.registerCustomItem(jClassBuilder.compileToJavaClass());
        const foodNormal = new FoodNormal(restoreFood, restoreSaturation);
        foodNormal.addRelative(id, 0, jsPlugin);
        Food.registerFood(foodNormal, jsPlugin);
    }

    /**
     * 注册一个工具物品
     * @param id {string} 物品ID
     * @param name {string} 物品ID
     * @param type {CreativeInventoryType} 创造物品栏分类
     * @param textureName {string} 贴图名称，在材质包中可以指定
     * @param stackSize {number} 最大堆叠
     * @param canOnOffhand {boolean} 是否可以装备在副手
     * @param toolType {ToolType} 工具种类
     * @param toolTier {ToolTier} 工具分级
     * @param durability {number} 最大耐久
     * @param damageOnAttackEntity {boolean} 是否因伤害实体而减少耐久
     * @param damageOnBreakBlock {boolean} 是否因破坏方块而减少耐久
     * @param handEquipped {boolean} 控制第三人称手持物品的显示方式
     * @param foil {boolean} 自定义物品是否带有附魔光辉效果
     */
    static registerToolItem(id, name, type, textureName,
                            stackSize, canOnOffhand, toolType, toolTier, durability,
                            damageOnAttackEntity, damageOnBreakBlock, handEquipped, foil) {
        const jClassBuilder = new JavaClassBuilder(id.replaceAll(":", "."), "cn.nukkit.item.customitem.ItemCustomTool");
        const delegate = {
            new() {
                return [id, name, textureName];
            },
            constructor(javaThis) {
            },
            getDefinition(javaThis) {
                return CustomItemDefinition
                    .toolBuilder(javaThis, ItemCreativeCategory.fromID(type.typeId))
                    .allowOffHand(canOnOffhand)
                    .handEquipped(handEquipped)
                    .foil(foil)
                    .build();
            },
            getMaxStackSize() {
                return stackSize;
            },
            getTier() {
                return toolTier.tierId;
            },
            getMaxDurability() {
                return durability;
            },
            noDamageOnAttack() {
                return !damageOnAttackEntity;
            },
            noDamageOnBreak() {
                return !damageOnBreakBlock;
            }
        };
        jClassBuilder
            .addJavaConstructor("new", "constructor", [JString, JString, JString])
            .addJavaMethod("getMaxStackSize", "getMaxStackSize", JPrimitiveInt);
        if (toolType === ToolType.AXE) {
            jClassBuilder.addJavaMethod("isAxe", "isAxe", JPrimitiveBoolean);
            delegate.isAxe = () => true;
        } else if (toolType === ToolType.HOE) {
            jClassBuilder.addJavaMethod("isHoe", "isHoe", JPrimitiveBoolean);
            delegate.isHoe = () => true;
        } else if (toolType === ToolType.PICKAXE) {
            jClassBuilder.addJavaMethod("isPickaxe", "isPickaxe", JPrimitiveBoolean);
            delegate.isPickaxe = () => true;
        } else if (toolType === ToolType.SHOVEL) {
            jClassBuilder.addJavaMethod("isShovel", "isShovel", JPrimitiveBoolean);
            delegate.isShovel = () => true;
        } else if (toolType === ToolType.SWORD) {
            jClassBuilder.addJavaMethod("isSword", "isSword", JPrimitiveBoolean);
            delegate.isSword = () => true;
        } else if (toolType === ToolType.SHEARS) {
            jClassBuilder.addJavaMethod("isShears", "isShears", JPrimitiveBoolean);
            delegate.isShears = () => true;
        }
        jClassBuilder.addJavaMethod("getTier", "getTier", JPrimitiveInt)
            .addJavaMethod("getMaxDurability", "getMaxDurability", JPrimitiveInt)
            .addJavaMethod("noDamageOnAttack", "noDamageOnAttack", JPrimitiveBoolean)
            .addJavaMethod("noDamageOnBreak", "noDamageOnBreak", JPrimitiveBoolean)
            .addJavaMethod("getDefinition", "getDefinition", CustomItemDefinition)
            .setJSDelegate(delegate);
        Item.registerCustomItem(jClassBuilder.compileToJavaClass());
    }

    /**
     * 注册一个防具自定义物品
     * @param id {string} 物品ID
     * @param name {string} 物品ID
     * @param type {CreativeInventoryType} 创造物品栏分类
     * @param textureName {string} 贴图名称，在材质包中可以指定
     * @param stackSize {number} 最大堆叠
     * @param canOnOffhand {boolean} 是否可以装备在副手
     * @param armorType {ArmorType} 防具种类
     * @param armorTier {ArmorTier} 防具等级
     * @param durability {number} 防具耐久
     * @param armorPoint {number} 防具提供的盔甲点数
     * @param handEquipped {boolean} 控制第三人称手持物品的显示方式
     * @param foil {boolean} 自定义物品是否带有附魔光辉效果
     */
    static registerArmorItem(id, name, type, textureName,
                             stackSize, canOnOffhand, armorType, armorTier,
                             durability, armorPoint, handEquipped, foil) {
        const jClassBuilder = new JavaClassBuilder(id.replaceAll(":", "."), "cn.nukkit.item.customitem.ItemCustomArmor");
        const delegate = {
            new() {
                return [id, name, textureName];
            },
            constructor(javaThis) {
            },
            getDefinition(javaThis) {
                return CustomItemDefinition
                    .armorBuilder(javaThis, ItemCreativeCategory.fromID(type.typeId))
                    .allowOffHand(canOnOffhand)
                    .handEquipped(handEquipped)
                    .foil(foil)
                    .build();
            },
            getMaxStackSize() {
                return stackSize;
            },
            getTier() {
                return armorTier.tierId;
            },
            getMaxDurability() {
                return durability;
            },
            getArmorPoint() {
                return armorPoint;
            }
        };
        jClassBuilder.setJSDelegate(delegate).addJavaConstructor("new", "constructor", [JString, JString, JString])
            .addJavaMethod("getMaxStackSize", "getMaxStackSize", JPrimitiveInt);
        if (armorType === ArmorType.HELMET) {
            jClassBuilder.addJavaMethod("isHelmet", "isHelmet", JPrimitiveBoolean);
            delegate.isHelmet = () => true;
        } else if (armorType === ArmorType.CHESTPLATE) {
            jClassBuilder.addJavaMethod("isChestplate", "isChestplate", JPrimitiveBoolean);
            delegate.isChestplate = () => true;
        } else if (armorType === ArmorType.LEGGINGS) {
            jClassBuilder.addJavaMethod("isLeggings", "isLeggings", JPrimitiveBoolean);
            delegate.isLeggings = () => true;
        } else if (armorType === ArmorType.BOOTS) {
            jClassBuilder.addJavaMethod("isBoots", "isBoots", JPrimitiveBoolean);
            delegate.isBoots = () => true;
        }
        jClassBuilder.addJavaMethod("getTier", "getTier", JPrimitiveInt)
            .addJavaMethod("getMaxDurability", "getMaxDurability", JPrimitiveInt)
            .addJavaMethod("getArmorPoint", "getArmorPoint", JPrimitiveInt)
            .addJavaMethod("getDefinition", "getDefinition", CustomItemDefinition)
            .setJSDelegate(delegate);
        Item.registerCustomItem(jClassBuilder.compileToJavaClass());
    }
}
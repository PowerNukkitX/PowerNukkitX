// noinspection JSUnresolvedFunction

import {id} from ":plugin-id"
import {JavaClassBuilder} from ":jvm"
import {CommonJSPlugin as CommonJSPluginClass} from "cn.nukkit.plugin.CommonJSPlugin"
import {String as JString} from "java.lang.String";
import {Item} from "cn.nukkit.item.Item";
import {Food} from "cn.nukkit.item.food.Food";
import {FoodNormal} from "cn.nukkit.item.food.FoodNormal";

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
 * 用于操作方块和物品的工具类
 */
export class BlockItemUtil {
    /**
     * 注册一个简单的自定义物品
     * @param id {string} 物品ID
     * @param name {string} 物品ID
     * @param type {CreativeInventoryType} 创造物品栏分类
     * @param textureName {string} 贴图名称，在材质包中可以指定
     * @param textureSize {number} 贴图大小，单位为像素，如为非正方形则以长边为准
     * @param stackSize {number} 最大堆叠
     * @param canOnOffhand {boolean} 是否可以装备在副手
     */
    static registerSimpleItem(id, name, type, textureName, textureSize, stackSize, canOnOffhand) {
        const jClassBuilder = new JavaClassBuilder(id.replaceAll(":", "."), "cn.nukkit.item.customitem.ItemCustom");
        jClassBuilder.setJSDelegate({
            new() {
                return [id, name, textureName];
            },
            constructor(javaThis) {
                javaThis.setTextureSize(textureSize);
            },
            allowOffHand() {
                return canOnOffhand;
            },
            getMaxStackSize() {
                return stackSize;
            },
            getCreativeCategory() {
                return type.typeId;
            }
        }).addJavaConstructor("new", "constructor", [JString, JString, JString])
            .addJavaMethod("allowOffHand", "allowOffHand", JPrimitiveBoolean)
            .addJavaMethod("getMaxStackSize", "getMaxStackSize", JPrimitiveInt)
            .addJavaMethod("getCreativeCategory", "getCreativeCategory", JPrimitiveInt);
        Item.registerCustomItem(jClassBuilder.compileToJavaClass());
    }

    /**
     * 注册一个食品物品
     * @param id {string} 物品ID
     * @param name {string} 物品ID
     * @param type {CreativeInventoryType} 创造物品栏分类
     * @param textureName {string} 贴图名称，在材质包中可以指定
     * @param textureSize {number} 贴图大小，单位为像素，如为非正方形则以长边为准
     * @param stackSize {number} 最大堆叠
     * @param canOnOffhand {boolean} 是否可以装备在副手
     * @param consumeTime {number} 食用耗时
     * @param isDrink {boolean} 是否是饮品，为false则为食品，如true则为饮品（药水等）
     * @param canAlwaysEat {boolean} 是否能满饥饿度了还吃
     * @param restoreFood {number} 恢复的饥饿值
     * @param restoreSaturation {number} 恢复的饱食度
     */
    static registerFoodItem(id, name, type, textureName, textureSize,
                            stackSize, canOnOffhand, consumeTime, isDrink, canAlwaysEat,
                            restoreFood, restoreSaturation) {
        const jClassBuilder = new JavaClassBuilder(id.replaceAll(":", "."), "cn.nukkit.item.customitem.ItemCustomEdible");
        jClassBuilder.setJSDelegate({
            new() {
                return [id, name, textureName];
            },
            constructor(javaThis) {
                javaThis.setTextureSize(textureSize);
            },
            allowOffHand() {
                return canOnOffhand;
            },
            getMaxStackSize() {
                return stackSize;
            },
            getCreativeCategory() {
                return type.typeId;
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
            .addJavaMethod("allowOffHand", "allowOffHand", JPrimitiveBoolean)
            .addJavaMethod("getMaxStackSize", "getMaxStackSize", JPrimitiveInt)
            .addJavaMethod("getCreativeCategory", "getCreativeCategory", JPrimitiveInt)
            .addJavaMethod("getEatTick", "getEatTick", JPrimitiveInt)
            .addJavaMethod("isDrink", "isDrink", JPrimitiveBoolean)
            .addJavaMethod("canAlwaysEat", "canAlwaysEat", JPrimitiveBoolean);
        Item.registerCustomItem(jClassBuilder.compileToJavaClass());
        const foodNormal = new FoodNormal(restoreFood, restoreSaturation);
        foodNormal.addRelative(id, 0, jsPlugin);
        Food.registerFood(foodNormal, jsPlugin);
    }
}
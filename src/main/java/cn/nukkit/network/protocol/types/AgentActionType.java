package cn.nukkit.network.protocol.types;

/**
 * @since v503
 * <p>
 * Enum members without a JSON example use the following format:
 * <pre>
 *     {
 *         "result": true|false
 *     }
 * </pre>
 */
public enum AgentActionType {
    NONE,
    ATTACK,
    COLLECT,
    DESTROY,
    DETECT_REDSTONE,
    DETECT_OBSTACLE,
    DROP,
    DROP_ALL,
    /**
     * JSON Data:
     * <pre>
     *     {
     *         "result": {
     *             "block": {
     *                 "id": "dirt",
     *                 "namespace": "minecraft",
     *                 "aux": 0
     *             }
     *         }
     *     }
     * </pre>
     */
    INSPECT,
    /**
     * JSON Data:
     * <pre>
     *     {
     *         "result": {
     *             "block": {
     *                 "aux": 0
     *             }
     *         }
     *     }
     * </pre>
     */
    INSPECT_DATA,
    /**
     * JSON Data:
     * <pre>
     *     {
     *         "result": {
     *             "item": {
     *                 "count": 10
     *             }
     *         }
     *     }
     * </pre>
     */
    INSPECT_ITEM_COUNT,
    /**
     * <b>Note:</b> If the enchantment level is above 10, the i18n string should not be used.
     * <p>
     * JSON Data:
     * <pre>
     *     {
     *         "result": {
     *             "item": {
     *                 "id": "dirt",
     *                 "namespace": "minecraft",
     *                 "aux": 0,
     *                 "maxStackSize": 64,
     *                 "stackSize": 1,
     *                 "freeStackSize": 63,
     *                 "enchantments: [
     *                     {
     *                         "name": "enchantment.knockback enchantment.level.1" | "enchantment.knockback 20",
     *                         "type": 1,
     *                         "level": 1
     *                     }
     *                 ]
     *             }
     *         }
     *     }
     * </pre>
     */
    INSPECT_ITEM_DETAIL,
    /**
     * JSON Data:
     * <pre>
     *     {
     *         "result": {
     *             "item": {
     *                 "maxStackSize": 64,
     *                 "stackSize": 1,
     *                 "freeStackSize": 63 // (maxStackSize - stackSize)
     *             }
     *         }
     *     }
     * </pre>
     */
    INSPECT_ITEM_SPACE,
    INTERACT,
    /**
     * JSON Data:
     * <pre>
     *     {
     *         "status": {
     *             "statusName": "moving" | "blocked | "reached"
     *         }
     *     }
     * </pre>
     */
    MOVE,
    PLACE_BLOCK,
    TILL,
    TRANSFER_ITEM_TO,
    TURN
}

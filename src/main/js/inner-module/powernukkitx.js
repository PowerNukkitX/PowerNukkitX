// noinspection JSUnresolvedVariable

import {id} from ":plugin-id"

const NukkitClass = Java.type("cn.nukkit.Nukkit");
const CommonJSPluginClass = Java.type("cn.nukkit.plugin.CommonJSPlugin");
const JSEventManager = Java.type("cn.nukkit.plugin.js.JSEventManager");
const EventPriorityClass = Java.type("cn.nukkit.event.EventPriority");

const jsPlugin = CommonJSPluginClass.jsPluginIdMap.get(id);
const eventManager = new JSEventManager(jsPlugin);

/**
 * 事件优先级枚举，越低越优先执行
 */
export class EventPriority {
    static LOWEST = EventPriorityClass.LOWEST;
    static LOW = EventPriorityClass.LOW;
    static NORMAL = EventPriorityClass.NORMAL;
    static HIGH = EventPriorityClass.HIGH;
    static HIGHEST = EventPriorityClass.HIGHEST;
    static MONITOR = EventPriorityClass.MONITOR;
}

/**
 * PowerNukkitX服务端操作对象
 */
export const PowerNukkitX = {
    /**
     * 获取PowerNukkitX服务器版本
     * @return {string}
     */
    getServerVersion: () => {
        return NukkitClass.GIT_COMMIT;
    },
    /**
     *
     * @param fullEventName {string}
     * @param priority {EventPriority}
     * @param callback {(event) => void}
     */
    listenEvent: (fullEventName, priority, callback) => {
        eventManager.register(fullEventName, priority, callback);
    }
}
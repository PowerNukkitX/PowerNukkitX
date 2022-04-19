// noinspection JSUnresolvedVariable,JSUnresolvedFunction

import {id} from ":plugin-id"

const CommonJSPluginClass = Java.type("cn.nukkit.plugin.CommonJSPlugin");
const JSConcurrentManagerClass = Java.type("cn.nukkit.plugin.js.JSConcurrentManager");

const jsPlugin = CommonJSPluginClass.jsPluginIdMap.get(id);
const concurrentManager = new JSConcurrentManagerClass(jsPlugin);

/**
 * 将对象包装为原子对象
 * @param obj
 */
export function atomic(obj) {
    return concurrentManager.warpSafe(obj);
}

/**
 * 获取原子对象等待最长时间，单位ms，默认30000
 * @type {number}
 */
export function getAtomicTimeout() {
    return concurrentManager.getLockTimeout();
}

/**
 * 设置原子对象等待最长时间，单位ms，默认30000
 * @param timeout {number}
 */
export function setAtomicTimeout(timeout) {
    return concurrentManager.setLockTimeout(timeout);
}

export class Worker {
    /**
     * 创建一个Worker
     * @param sourcePath {string} worker源代码路径
     * @param startImmediately {boolean} 是否立即启动worker执行，默认true
     */
    constructor(sourcePath, startImmediately = true) {
        this.jsWorker = concurrentManager.createWorker(sourcePath);
        this.jsWorker.init();
        Object.defineProperty(this, 'onmessage', {
            get: function () {
                return this.jsWorker.getSourceReceiveCallback();
            },
            set: function (callback) {
                return this.jsWorker.setSourceReceiveCallback(callback);
            }
        });
        if (startImmediately) {
            this.jsWorker.start();
        }
    }

    /**
     * 启动尚未启动的worker
     */
    start() {
        this.jsWorker.start();
    }

    /**
     * 向Worker发送信息，返回Worker的onmessage函数的返回值
     * @param values
     * @returns {any}
     */
    postMessage(...values) {
        return this.jsWorker.postMessage(values);
    }

    /**
     * 向Worker发送异步信息，返回一个Promise
     * @param values
     * @returns {Promise<any>}
     */
    postMessageAsync(...values) {
        return this.jsWorker.postMessageAsync(values);
    }

    /**
     * 终结（强制停止）此Worker
     */
    terminate() {
        this.jsWorker.close();
    }
}
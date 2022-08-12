// noinspection JSUnresolvedVariable,JSUnresolvedFunction

import {id} from ":plugin-id"
import {JSJVMManager} from "cn.nukkit.plugin.js.JSJVMManager"
import {JType} from "cn.nukkit.plugin.js.compiler.JType";
import {CommonJSPlugin as CommonJSPluginClass} from "cn.nukkit.plugin.CommonJSPlugin"
import {JClassBuilder} from "cn.nukkit.plugin.js.compiler.JClassBuilder";
import {JMethod} from "cn.nukkit.plugin.js.compiler.JMethod";
import {JConstructor} from "cn.nukkit.plugin.js.compiler.JConstructor";
import {JSuperMethod} from "cn.nukkit.plugin.js.compiler.JSuperMethod";
import {JSuperField} from "cn.nukkit.plugin.js.compiler.JSuperField";

const jsPlugin = CommonJSPluginClass.jsPluginIdMap.get(id);
const jvmManager = new JSJVMManager(jsPlugin);

/**
 * 获取当前使用的JVM的版本
 * @return {string}
 */
export function getJVMVersion() {
    return jvmManager.getJVMVersion();
}

/**
 * 获取当前使用的JVM中的JIT的版本
 * @return {string}
 */
export function getJITVersion() {
    return jvmManager.getJITVersion();
}

/**
 * 强制执行彻底的垃圾回收以释放内存，可能导致服务器短时卡顿
 */
export function gc() {
    jvmManager.gc();
}

/**
 * 从指定路径动态加载jar包
 * @param jarPath {string} jar包路径
 */
export function loadJar(jarPath) {
    jsPlugin.getClassLoader().addJar(jarPath);
}

/**
 * @external JavaClass
 */

export class JavaClassBuilder {
    /**
     * 构造一个Java类生成器，用于在JS中对Java类进行相关操作
     * @param className {string} 将要创建的Java类的名称
     * @param {string|JavaClass} [extendClass] 此Java类继承的父Java类的名称，如不写默认为java.lang.Object
     */
    constructor(className, extendClass) {
        this._classBuilder = new JClassBuilder(jsPlugin);
        this._classBuilder.setClassName(className);
        if (extendClass) {
            this._classBuilder.setSuperClass(this._toJType(extendClass));
        }
    }

    /**
     * @return {JClassBuilder}
     */
    get jClassBuilder() {
        return this._classBuilder;
    }

    /**
     * @private
     * @param type {string|JavaClass} java类对象或者其名称字符串
     * @return {JType}
     */
    _toJType(type) {
        if (typeof type === "string") {
            return JType.ofClassName(type);
        } else {
            return JType.of(type);
        }
    }

    /**
     * 添加一个实现了的接口
     * @param interfaceClass {string|JavaClass} java类对象或者其名称字符串
     * @return {JavaClassBuilder} this
     */
    addJavaInterface(interfaceClass) {
        this._classBuilder.addInterfaceClass(this._toJType(interfaceClass));
        return this;
    }

    /**
     * 让此代理对象代理新建的java类中的所有操作
     * @param delegateObj {any} 代理对象
     * @return {JavaClassBuilder} this
     */
    setJSDelegate(delegateObj) {
        this._classBuilder.setDelegate(delegateObj);
        return this;
    }

    /**
     * 向新生成的Java类中添加一个构造函数
     * @param superDelegateName {string} super代理名，即delegateObj中的一个函数的名称，返回值数组将会用作调用父super构造函数的参数
     * @param constructorDelegateName {string} 构造函数代理名，即delegateObj中的一个函数的名称，参数为java对象自身加上所有argTypes中的参数
     * @param superTypes {Array<string|JavaClass>} 要传递给super父构造函数的所有参数类型
     * @param argTypes {...(string|JavaClass)} 构造函数的所有参数类型
     * @return {JavaClassBuilder} this
     */
    addJavaConstructor(superDelegateName, constructorDelegateName, superTypes, ...argTypes) {
        this._classBuilder.addConstructor(new JConstructor(this._classBuilder, superDelegateName, constructorDelegateName,
            superTypes.map(e => this._toJType(e)), argTypes.map(e => this._toJType(e))));
        return this;
    }

    /**
     * 向新生成的Java类中添加新方法
     * @param methodName {string} 方法名
     * @param delegateName {string} 代理名，即delegateObj中的一个函数的名称，参数为java对象自身加上所有argTypes中的参数
     * @param returnType {string|JavaClass} 方法返回类型
     * @param argTypes {...(string|JavaClass)} 方法参数类型
     * @return {JavaClassBuilder} this
     */
    addJavaMethod(methodName, delegateName, returnType, ...argTypes) {
        this._classBuilder.addMethod(new JMethod(this._classBuilder, methodName, delegateName,
            this._toJType(returnType), argTypes.map(e => this._toJType(e))));
        return this;
    }

    /**
     * 允许访问父类中的方法，添加后可以使用“__super__父类方法名”来调用父类中的方法，即使它是protected的也可以
     * @param methodName {string} 父类中的方法名
     * @param returnType {string|JavaClass} 方法返回类型
     * @param argTypes {...(string|JavaClass)} 方法参数类型
     * @return {JavaClassBuilder} this
     */
    addJavaSuperMethod(methodName, returnType, ...argTypes) {
        this._classBuilder.addSuperMethod(new JSuperMethod(this._classBuilder, methodName,
            this._toJType(returnType), argTypes.map(e => this._toJType(e))));
        return this;
    }

    /**
     * 允许访问并设置父类中的protected字段
     * @param fieldName {string} 父类中的字段名
     * @param type {string|JavaClass} 字段类型，可以是java类对象或其名称字符串
     * @return {JavaClassBuilder} this
     */
    addJavaSuperField(fieldName, type) {
        this._classBuilder.addSuperField(new JSuperField(this._classBuilder, fieldName, this._toJType(type)));
        return this;
    }

    /**
     * 将这个ClassBuilder表示的类进行编译，并返回编译后的JavaClass对象。
     * 编译速度会很快，通常在0.01秒内完成。
     * @return {JavaClass} 编译完后的JavaClass对象，可以在Java或者JS中通过new或反射创建。
     */
    compileToJavaClass() {
        return this._classBuilder.compileToClass();
    }

    /**
     * 将这个ClassBuilder表示的类进行编译，并将编译后的字节码储存在指定路径中。
     * 编译速度会很快，通常在0.01秒内完成。
     * @param path {string} 保存路径
     */
    compileToFile(path) {
        this._classBuilder.compileToClass(path);
    }
}
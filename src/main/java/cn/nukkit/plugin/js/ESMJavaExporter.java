package cn.nukkit.plugin.js;

// TODO: 2022/4/21 支持更多导出类型
// TODO: 2022/4/21 检查内部类和匿名类导入
public final class ESMJavaExporter {
    public static String exportJava(Class<?> clazz) {
        return "export const " +
                clazz.getSimpleName() + " = Java.type(\"" + clazz.getName() + "\")";
    }
}

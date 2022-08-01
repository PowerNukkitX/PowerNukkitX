package cn.nukkit.item;

import cn.nukkit.item.customitem.ItemCustom;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class TestCustomItem extends ItemCustom {
    public static Context context;
    public static Value delegate;
    public static Value[] cons;

    public TestCustomItem() {
        super(cons[0].as(String.class), cons[1].as(String.class));
        __initJSConstructor__("super_0", new Object[0]);
        cons = null;
        __callJS__("constructor", new Object[0]);
    }

    public TestCustomItem(String id, String name) {
        super(id, name);
        __callJS__("constructor", new Object[]{id, name});
    }

    public static void __initJSConstructor__(String delegateName, Object[] args) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (context) {
            var tmp = delegate.getMember(delegateName).execute(args);
            if (tmp.isNull()) return;
            if (tmp.hasArrayElements()) {
                cons = new Value[(int) tmp.getArraySize()];
                for (int i = 0, len = cons.length; i < len; i++) {
                    cons[i] = tmp.getArrayElement(i);
                }
            } else {
                cons = new Value[]{tmp};
            }
        }
    }

    private static Value __callJS__(String delegateName, Object[] args) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (context) {
            var func = delegate.getMember(delegateName);
            if (func.canExecute()) {
                return func.execute(args);
            } else {
                return Value.asValue(null);
            }
        }
    }

    public boolean __super__allowOffHand() {
        return super.allowOffHand();
    }

    @Override
    public boolean allowOffHand() {
        return __callJS__("allowOffHand", new Object[]{}).as(Boolean.class);
    }
}

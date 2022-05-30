package cn.nukkit.plugin.js.external;

import cn.nukkit.plugin.js.JSExternal;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;

public final class ExternalArray extends JSExternal implements ProxyArray {
    public ExternalArray(Context sourceContext, Value value) {
        super(sourceContext, value);
    }

    @Override
    public Object get(long index) {
        synchronized (sourceContext) {
            return value.getArrayElement(index);
        }
    }

    @Override
    public void set(long index, Value value) {
        synchronized (sourceContext) {
            value.setArrayElement(index, value);
        }
    }

    @Override
    public boolean remove(long index) {
        synchronized (sourceContext) {
            return value.removeArrayElement(index);
        }
    }

    @Override
    public long getSize() {
        synchronized (sourceContext) {
            return value.getArraySize();
        }
    }

    @Override
    public Object getIterator() {
        synchronized (sourceContext) {
            return value.getIterator();
        }
    }
}

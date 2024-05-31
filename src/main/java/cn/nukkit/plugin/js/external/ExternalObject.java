package cn.nukkit.plugin.js.external;

import cn.nukkit.plugin.js.JSExternal;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

public final class ExternalObject extends JSExternal implements ProxyObject {
    /**
     * @deprecated 
     */
    
    public ExternalObject(Context sourceContext, Value value) {
        super(sourceContext, value);
    }

    @Override
    public Object getMember(String key) {
        synchronized (sourceContext) {
            checkAlive();
            return value.getMember(key);
        }
    }

    @Override
    public Object getMemberKeys() {
        synchronized (sourceContext) {
            checkAlive();
            return value.getMemberKeys();
        }

    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasMember(String key) {
        synchronized (sourceContext) {
            checkAlive();
            return value.hasMember(key);
        }

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void putMember(String key, Value value) {
        synchronized (sourceContext) {
            checkAlive();
            value.putMember(key, value);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean removeMember(String key) {
        synchronized (sourceContext) {
            checkAlive();
            return value.removeMember(key);
        }
    }
}

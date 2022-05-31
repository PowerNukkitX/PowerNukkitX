package cn.nukkit.plugin.js;

import lombok.Data;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

@Data
public abstract class JSExternal {
    protected final Context sourceContext;
    protected final Value value;
    private boolean alive = true;

    protected final void checkAlive() {
        if(!alive) {
            throw new ReferenceNotAliveException("Reference targeting " + value.getMetaQualifiedName() + " has already be disposed.");
        }
    }

    public static final class ReferenceNotAliveException extends RuntimeException {
        public ReferenceNotAliveException(String message) {
            super(message);
        }
    }
}

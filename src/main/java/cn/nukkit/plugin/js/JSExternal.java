package cn.nukkit.plugin.js;

import lombok.Data;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

@Data
public abstract class JSExternal {
    protected final Context sourceContext;
    protected final Value value;
}

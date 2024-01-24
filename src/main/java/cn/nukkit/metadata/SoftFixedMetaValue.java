package cn.nukkit.metadata;

import cn.nukkit.plugin.Plugin;

import java.lang.ref.SoftReference;

public class SoftFixedMetaValue extends LazyMetadataValue {
    /**
     * Store the internal value that is represented by this fixed value.
     */
    private final SoftReference<Object> internalValue;

    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningPlugin the {@link Plugin} that created this metadata value
     * @param value        the value assigned to this metadata value
     */
    public SoftFixedMetaValue(Plugin owningPlugin, final Object value) {
        super(owningPlugin);
        this.internalValue = new SoftReference<>(value);
    }

    @Override
    public void invalidate() {
        internalValue.clear();
    }

    @Override
    public Object value() {
        return internalValue.get();
    }
}

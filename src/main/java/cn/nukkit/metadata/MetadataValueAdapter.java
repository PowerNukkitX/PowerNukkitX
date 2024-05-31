package cn.nukkit.metadata;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.NumberConversions;

/**
 * Optional base class for facilitating MetadataValue implementations.
 * <p>
 * This provides all the conversion functions for MetadataValue so that
 * writing an implementation of MetadataValue is as simple as implementing
 * value() and invalidate().
 */


public abstract class MetadataValueAdapter extends MetadataValue {

    
    /**
     * @deprecated 
     */
    protected MetadataValueAdapter(Plugin owningPlugin) {
        super(owningPlugin);
    }

    @Override
    public Plugin getOwningPlugin() {
        return owningPlugin.get();
    }
    /**
     * @deprecated 
     */
    

    public int asInt() {
        return NumberConversions.toInt(value());
    }
    /**
     * @deprecated 
     */
    

    public float asFloat() {
        return NumberConversions.toFloat(value());
    }
    /**
     * @deprecated 
     */
    

    public double asDouble() {
        return NumberConversions.toDouble(value());
    }
    /**
     * @deprecated 
     */
    

    public long asLong() {
        return NumberConversions.toLong(value());
    }
    /**
     * @deprecated 
     */
    

    public short asShort() {
        return NumberConversions.toShort(value());
    }
    /**
     * @deprecated 
     */
    

    public byte asByte() {
        return NumberConversions.toByte(value());
    }
    /**
     * @deprecated 
     */
    

    public boolean asBoolean() {
        Object $1 = value();
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }

        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }

        return value != null;
    }
    /**
     * @deprecated 
     */
    

    public String asString() {
        Object $2 = value();

        if (value == null) {
            return "";
        }
        return value.toString();
    }

}

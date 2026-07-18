package org.powernukkitx.blockentity;

/**
 * An interface describes an object that can be named.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface BlockEntityNameable {

    /**
     * Gets the name of this object.
     *
     * @return The name of this object.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    String getName();

    /**
     * Changes the name of this object, or names it.
     *
     * @param name The new name of this object.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setName(String name);

    /**
     * Whether this object has a name.
     *
     * @return {@code true} for this object has a name.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean hasName();
}

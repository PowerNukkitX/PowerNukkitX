package org.powernukkitx.permission;

/**
 * Who can be an operator(OP).
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @see org.powernukkitx.permission.Permissible
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface ServerOperator {

    /**
     * Returns if this object is an operator.
     *
     * @return if this object is an operator.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean isOp();

    /**
     * Sets this object to be an operator or not to be.
     *
     * @param value {@code true} for giving this operator or {@code false} for cancelling.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    void setOp(boolean value);
}

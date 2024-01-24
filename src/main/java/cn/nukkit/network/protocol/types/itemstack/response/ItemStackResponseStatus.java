package cn.nukkit.network.protocol.types.itemstack.response;

/**
 * Represents the status of a {@link ItemStackResponse}.
 */
public enum ItemStackResponseStatus {
    /**
     * The transaction request was valid.
     */
    OK,

    /**
     * The transaction request was invalid.
     */
    ERROR
}

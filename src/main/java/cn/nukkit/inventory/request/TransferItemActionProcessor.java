package cn.nukkit.inventory.request;

import cn.nukkit.network.protocol.types.itemstack.request.action.TransferItemStackRequestAction;
import lombok.extern.slf4j.Slf4j;

/**
 * Allay Project 2023/7/28
 *
 * @author daoge_cmd
 */
@Slf4j
public abstract class TransferItemActionProcessor<T extends TransferItemStackRequestAction> implements ItemStackRequestActionProcessor<T> {

}

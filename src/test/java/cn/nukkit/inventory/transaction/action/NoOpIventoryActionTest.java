package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * @author joserobjr
 * @since 2021-12-20
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class NoOpIventoryActionTest {
    @Mock
    Player player;

    @Mock
    Item source;

    @Mock
    Item target;

    NoOpIventoryAction action;

    @BeforeEach
    void setUp() {
        action = new NoOpIventoryAction(source, target) {
            @Override
            public boolean isValid(Player source) {
                return true;
            }
        };
    }

    @Test
    void execute() {
        assertTrue(action.execute(player));
    }

    @Test
    void onExecuteSuccess() {
        action.onExecuteSuccess(player);
        verifyNoInteractions(player);
        verifyNoInteractions(source);
        verifyNoInteractions(target);
    }

    @Test
    void onExecuteFail() {
        action.onExecuteFail(player);
        verifyNoInteractions(player);
        verifyNoInteractions(source);
        verifyNoInteractions(target);
    }
}

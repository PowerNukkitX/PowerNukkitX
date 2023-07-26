package cn.nukkit.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CommandTest {

    @SuppressWarnings("deprecation")
    @Test
    void getDefaultCommandData() {
        assertThrows(UnsupportedOperationException.class, Command::generateDefaultData);
    }
}

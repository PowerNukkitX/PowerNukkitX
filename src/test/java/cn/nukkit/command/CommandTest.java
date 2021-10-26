package cn.nukkit.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandTest {

    @SuppressWarnings("deprecation")
    @Test
    void getDefaultCommandData() {
        assertThrows(UnsupportedOperationException.class, Command::generateDefaultData);
    }
}

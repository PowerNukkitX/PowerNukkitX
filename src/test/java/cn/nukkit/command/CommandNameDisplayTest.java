package cn.nukkit.command;

import cn.nukkit.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for command-line argument parsing (quoting/spaces) and the permission-gated
 * {@link Command#getViewableName(CommandSender, Player)} display-name helper.
 */
class CommandNameDisplayTest {

    @Test
    void parseArguments_splitsOnSpaces() {
        ArrayList<String> args = SimpleCommandMap.parseArguments("tp alice bob");
        assertEquals(List.of("tp", "alice", "bob"), args);
    }

    @Test
    void parseArguments_collapsesRepeatedSpaces() {
        ArrayList<String> args = SimpleCommandMap.parseArguments("say  hi   there");
        assertEquals(List.of("say", "hi", "there"), args);
    }

    @Test
    void parseArguments_keepsQuotedNameWithSpacesAsSingleArg() {
        // A nicked display name containing a space can still be targeted when quoted.
        ArrayList<String> args = SimpleCommandMap.parseArguments("kick \"Example Player\" spamming");
        assertEquals(List.of("kick", "Example Player", "spamming"), args);
    }

    @Test
    void parseArguments_emptyLineYieldsNoArgs() {
        assertTrue(SimpleCommandMap.parseArguments("   ").isEmpty());
    }

    @Test
    void getViewableName_withoutPermissionReturnsDisplayName() {
        Player target = mock(Player.class);
        when(target.getDisplayName()).thenReturn("Nick");
        when(target.getName()).thenReturn("RealName");

        CommandSender viewer = mock(CommandSender.class);
        when(viewer.hasPermission(Command.TARGET_REAL_NAME_PERMISSION)).thenReturn(false);

        assertEquals("Nick", Command.getViewableName(viewer, target));
    }

    @Test
    void getViewableName_withPermissionReturnsRealName() {
        Player target = mock(Player.class);
        when(target.getDisplayName()).thenReturn("Nick");
        when(target.getName()).thenReturn("RealName");

        CommandSender viewer = mock(CommandSender.class);
        when(viewer.hasPermission(Command.TARGET_REAL_NAME_PERMISSION)).thenReturn(true);

        assertEquals("RealName", Command.getViewableName(viewer, target));
    }

    @Test
    void getViewableName_nullViewerReturnsDisplayName() {
        Player target = mock(Player.class);
        when(target.getDisplayName()).thenReturn("Nick");

        assertEquals("Nick", Command.getViewableName(null, target));
    }
}

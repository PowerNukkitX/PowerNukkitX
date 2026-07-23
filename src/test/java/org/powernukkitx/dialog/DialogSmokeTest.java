package org.powernukkitx.dialog;

import org.powernukkitx.PlayerFixture;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.dialog.element.ElementDialogButton;
import org.powernukkitx.dialog.window.FormWindowDialog;
import org.powernukkitx.dialog.window.ScrollingTextDialog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Coverage for the NPC dialog POJOs. FormWindowDialog requires a non-null bind
 * entity, so the real TestPlayer from the fixture is used as the entity. No packets
 * are sent - only builders, getters and JSON (de)serialization are exercised.
 */
public class DialogSmokeTest {

    static TestPlayer player;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        player = PlayerFixture.get();
    }

    @Test
    void buttonBuildsCmdDataAndGetters() {
        ElementDialogButton button = new ElementDialogButton("name", "line1\nline2");
        Assertions.assertEquals("name", button.getName());
        Assertions.assertEquals("line1\nline2", button.getText());
        Assertions.assertEquals(2, button.getData().size());
        Assertions.assertEquals("line1", button.getData().get(0).cmd_line);
        Assertions.assertEquals(ElementDialogButton.CmdLine.CMD_VER, button.getData().get(0).cmd_ver);

        button.setName("renamed");
        Assertions.assertEquals("renamed", button.getName());
        button.setText("only");
        Assertions.assertEquals(1, button.getData().size());

        button.setType(3);
        Assertions.assertEquals(3, button.getType());
        Assertions.assertNull(button.getNextDialog());

        button.setMode(ElementDialogButton.Mode.ON_ENTER);
        Assertions.assertEquals(ElementDialogButton.Mode.ON_ENTER, button.getMode());
        button.setMode(ElementDialogButton.Mode.BUTTON_MODE);
        Assertions.assertEquals(ElementDialogButton.Mode.BUTTON_MODE, button.getMode());

        ElementDialogButton full = new ElementDialogButton("n", "t", null,
                ElementDialogButton.Mode.ON_EXIT, 2);
        Assertions.assertEquals(ElementDialogButton.Mode.ON_EXIT, full.getMode());
        Assertions.assertEquals(2, full.getType());
    }

    @Test
    void dialogWindowBuildsAndSerializes() {
        FormWindowDialog dialog = new FormWindowDialog("Title", "Content", player);
        Assertions.assertEquals("Title", dialog.getTitle());
        Assertions.assertEquals("Content", dialog.getContent());
        Assertions.assertSame(player, dialog.getBindEntity());
        Assertions.assertEquals(player.getId(), dialog.getEntityId());
        Assertions.assertNotNull(dialog.getSceneName());
        Assertions.assertNotNull(dialog.getSkinData());
        Assertions.assertTrue(dialog.getButtons().isEmpty());

        dialog.setTitle("T2");
        dialog.setContent("C2");
        Assertions.assertEquals("T2", dialog.getTitle());
        Assertions.assertEquals("C2", dialog.getContent());

        dialog.addButton("simple");
        dialog.addButton(new ElementDialogButton("b", "text"));
        Assertions.assertEquals(2, dialog.getButtons().size());

        String json = dialog.getButtonJSONData();
        Assertions.assertNotNull(json);
        Assertions.assertFalse(json.isEmpty());

        String prevScene = dialog.getSceneName();
        dialog.updateSceneName();
        Assertions.assertNotEquals(prevScene, dialog.getSceneName());

        dialog.setSkinData("{}");
        Assertions.assertEquals("{}", dialog.getSkinData());
    }

    @Test
    void buttonJsonRoundTrip() {
        FormWindowDialog dialog = new FormWindowDialog("t", "c", player,
                new ArrayList<>(List.of(new ElementDialogButton("a", "aaa"))));
        String json = dialog.getButtonJSONData();

        FormWindowDialog target = new FormWindowDialog("t2", "c2", player);
        target.setButtonJSONData(json);
        Assertions.assertFalse(target.getButtons().isEmpty());

        // null-safe: bad json yields empty list, never null
        target.setButtonJSONData("null");
        Assertions.assertNotNull(target.getButtons());
    }

    @Test
    void nullBindEntityRejected() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new FormWindowDialog("t", "c", null));
    }

    @Test
    void scrollingTextDialogGetters() {
        FormWindowDialog dialog = new FormWindowDialog("t", "0123456789", player);
        ScrollingTextDialog scrolling = new ScrollingTextDialog(player, dialog);
        Assertions.assertSame(player, scrolling.getPlayer());
        Assertions.assertSame(dialog, scrolling.getDialog());
        Assertions.assertEquals(2, scrolling.getScrollingSpeed());
        Assertions.assertFalse(scrolling.isScrolling());
        Assertions.assertEquals(0, scrolling.getCursor());

        scrolling.setScrollingSpeed(5);
        Assertions.assertEquals(5, scrolling.getScrollingSpeed());
        scrolling.setScrolling(true);
        Assertions.assertTrue(scrolling.isScrolling());
        scrolling.stopScrolling();
        Assertions.assertFalse(scrolling.isScrolling());
        scrolling.setCursor(4);
        Assertions.assertEquals(4, scrolling.getCursor());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> scrolling.setCursor(9999));

        ScrollingTextDialog fast = new ScrollingTextDialog(player, dialog, 1);
        Assertions.assertEquals(1, fast.getScrollingSpeed());
    }
}

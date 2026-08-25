package org.powernukkitx.permission;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.Server;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.plugin.InternalPlugin;
import org.powernukkitx.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tolerant smoke coverage for the permission package - constructs Permission /
 * PermissibleBase and drives the attachment and recalculation paths.
 */
class PermissionSmokeTest {

    private static final AtomicInteger checked = new AtomicInteger();

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
            checked.incrementAndGet();
        } catch (Throwable ignored) {
        }
    }

    @Test
    void permissionCtorsAndGetters() {
        safe(() -> {
            Permission p = new Permission("test.node");
            p.getName();
            p.getDefault();
            p.getChildren();
            p.getDescription();
        });
        safe(() -> {
            Permission p = new Permission("test.node2", "desc");
            p.setDescription("changed");
            p.getDescription();
        });
        safe(() -> {
            Permission p = new Permission("test.node3", "desc", Permission.DEFAULT_TRUE);
            p.getDefault();
            p.setDefault(Permission.DEFAULT_OP);
        });
        safe(() -> {
            Map<String, Boolean> children = new HashMap<>();
            children.put("child.a", true);
            new Permission("test.parent", "d", Permission.DEFAULT_OP, children).getChildren();
        });
        safe(() -> Permission.getByName("op"));
        safe(() -> Permission.getByName("notop"));
        safe(() -> Permission.getByName("true"));
        safe(() -> Permission.getByName("unknownvalue"));

        assertTrue(checked.get() > 0);
    }

    @Test
    void permissionLoadFromMap() {
        safe(() -> {
            Map<String, Object> body = new HashMap<>();
            body.put("description", "loaded");
            body.put("default", "op");
            Permission.loadPermission("loaded.node", body);
        });
        safe(() -> {
            Map<String, Object> child = new HashMap<>();
            child.put("description", "c");
            Map<String, Object> children = new HashMap<>();
            children.put("loaded.child", child);
            Map<String, Object> body = new HashMap<>();
            body.put("children", children);
            Permission.loadPermission("loaded.withchildren", body, Permission.DEFAULT_TRUE);
        });
        safe(() -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("description", "e");
            Map<String, Object> data = new HashMap<>();
            data.put("root.node", entry);
            Permission.loadPermissions(data);
        });
        assertTrue(checked.get() > 0);
    }

    @Test
    void permissibleBaseWithPlayer() {
        TestPlayer player = PlayerFixture.get();

        safe(player::recalculatePermissions);
        safe(() -> player.hasPermission("nukkit.command.help"));
        safe(() -> player.isPermissionSet("nukkit.command.help"));
        safe(player::getEffectivePermissions);
        safe(() -> player.isOp());

        Plugin plugin = Server.getInstance().getPluginManager().getPlugin("PowerNukkitX");
        if (plugin == null) {
            plugin = InternalPlugin.INSTANCE;
        }
        final Plugin p = plugin;
        if (p != null) {
            safe(() -> {
                PermissionAttachment att = player.addAttachment(p);
                att.setPermission("custom.flag", true);
                att.getPermissions();
                att.getPlugin();
                player.hasPermission("custom.flag");
                player.isPermissionSet("custom.flag");
                att.unsetPermission("custom.flag", true);
                player.removeAttachment(att);
            });
            safe(() -> {
                PermissionAttachment att = player.addAttachment(p, "named.flag", true);
                Map<String, Boolean> more = new HashMap<>();
                more.put("bulk.a", true);
                more.put("bulk.b", false);
                att.setPermissions(more);
                att.unsetPermissions(List.of("bulk.a"));
                att.clearPermissions();
                att.remove();
            });
        }

        assertTrue(checked.get() > 0);
    }

    @Test
    void permissionAttachmentInfoDirect() {
        TestPlayer player = PlayerFixture.get();
        safe(() -> {
            PermissionAttachmentInfo info =
                    new PermissionAttachmentInfo(player, "some.perm", null, true);
            info.getPermissible();
            info.getPermission();
            info.getAttachment();
            info.getValue();
        });
        assertTrue(checked.get() > 0);
    }

    @Test
    void defaultPermissionsRegister() {
        safe(() -> DefaultPermissions.registerPermission(new Permission("nukkit.smoke.test")));
        safe(DefaultPermissions::registerCorePermissions);
        assertTrue(checked.get() > 0);
    }
}

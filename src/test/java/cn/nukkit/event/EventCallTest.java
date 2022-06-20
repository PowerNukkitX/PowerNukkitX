package cn.nukkit.event;

import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.lang.TextContainer;
import cn.nukkit.plugin.MethodEventExecutor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class EventCallTest implements Listener {
    TextContainer blackHole;

    public void handle(PlayerJoinEvent event) {
        blackHole = event.getJoinMessage();
    }

    @SneakyThrows
    //@Test
    public void testReflect() {
        var methodEventExecutor = new MethodEventExecutor(this.getClass().getMethod("handle", PlayerJoinEvent.class));
        var event = new PlayerJoinEvent(null, "???");
        var start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            methodEventExecutor.execute(this, event);
        }
        System.out.println("改进前一千万次事件调用耗时(ms):");
        System.out.println(System.currentTimeMillis() - start);
    }

    @SneakyThrows
    //@Test
    public void testASM() {
        var methodEventExecutor = MethodEventExecutor.compile(Thread.currentThread().getContextClassLoader(), this.getClass(),
                this.getClass().getMethod("handle", PlayerJoinEvent.class));
        var event = new PlayerJoinEvent(null, "???");
        var start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            assert methodEventExecutor != null;
            methodEventExecutor.execute(this, event);
        }
        System.out.println("改进后一千万次事件调用耗时(ms):");
        System.out.println(System.currentTimeMillis() - start);
    }
}

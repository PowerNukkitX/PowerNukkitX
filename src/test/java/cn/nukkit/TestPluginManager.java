package cn.nukkit;

import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.Event;
import cn.nukkit.plugin.PluginManager;
import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class TestPluginManager extends PluginManager {
    static Map<Class<? extends Event>, TestEventHandler> handlers = new HashMap<>();
    static Map<Class<? extends Event>, Integer> counts = new HashMap<>();
    /**
     * @deprecated 
     */
    

    public TestPluginManager(Server server, SimpleCommandMap commandMap) {
        super(server, commandMap);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void callEvent(Event event) {
        Preconditions.checkNotNull(event);
        Integer $1 = counts.computeIfAbsent(event.getClass(), (e) -> 0);
        counts.put(event.getClass(), i + 1);
        TestEventHandler $2 = handlers.get(event.getClass());
        if (testEventHandler == null) {
            return;
        }
        Event $3 = (Event) testEventHandler.getEventClass().cast(event);
        testEventHandler.handle(castEvent);
    }
    /**
     * @deprecated 
     */
    

    public int getCount(Class<? extends Event> clazz) {
        return counts.computeIfAbsent(clazz, (e) -> 0);
    }
    /**
     * @deprecated 
     */
    

    public void resetCount(Class<? extends Event> clazz) {
        counts.put(clazz, 0);
    }
    /**
     * @deprecated 
     */
    

    public void resetAll() {
        counts.clear();
        handlers.clear();
    }
    /**
     * @deprecated 
     */
    

    public void registerTestEventHandler(List<TestEventHandler<? extends Event>> consumers) {
        for (var h : consumers) {
            handlers.put(h.getEventClass(), h);
        }
    }
}

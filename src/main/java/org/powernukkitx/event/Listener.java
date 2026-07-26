package org.powernukkitx.event;

/**
 * An interface implemented by all classes that handles events.
 *
 * <p>If a plugin need to listen events, there must be a class implement this interface. This class is called a <b>listener class</b>.
 * Methods with specified parameters should be written in order to listen events. This method is called a <b>handler</b>.
 * One listener class could contain many different handlers.
 * After implemented the listener class, plugin should register it in plugin level.</p>
 *
 * <p>After registered, Nukkit will call the handler in the listener classes by reflection when a event happens.</p>
 *
 * <p>Here is an example for writing a listener class and a handler method.
 * Note that for the handler, tag {@code @EventHandler} and the parameter is required:</p>
 * <pre>
 * public class ExampleListener implements Listener {
 *    {@code @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)}
 *     public void onBlockBreak(BlockBreakEvent event) {
 *          String blockId = event.getBlock().getPackId();
 *          if (blockID == Block.STONE) {
 *              event.getPlayer().sendMessage("Oops, my ExampleListener won't let you break a stone!")
 *              event.setCancelled(true);
 *          }
 *     }
 * }
 * </pre>
 *
 * <p>For registering listener class, See: {@link org.powernukkitx.plugin.PluginManager#registerEvents}.</p>
 *
 * <p>For the priority of handler and whether the handler ignore cancelled events or not, See: {@link EventHandler}.</p>
 *
 * @author Unknown(code) @ Nukkit Project
 * @author Fenxie Dama (javadoc) @ Nukkit Project
 * @see org.powernukkitx.event.Event
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface Listener {
}

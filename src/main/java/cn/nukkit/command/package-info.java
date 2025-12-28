/**
 * Provides the core classes and interfaces for command handling in PowerNukkitX.
 * <p>
 * This package contains abstractions and implementations for command registration, execution, permission management,
 * command senders (players, console, entities, NPCs), command mapping, aliases, and annotation-based command systems.
 * <p>
 * Key components include:
 * <ul>
 *   <li>{@link cn.nukkit.command.Command} - Abstract base for all commands.</li>
 *   <li>{@link cn.nukkit.command.CommandSender} - Interface for entities capable of sending commands.</li>
 *   <li>{@link cn.nukkit.command.CommandMap} - Registry and execution system for commands.</li>
 *   <li>{@link cn.nukkit.command.ConsoleCommandSender} - Command sender for the server console.</li>
 *   <li>{@link cn.nukkit.command.ExecutorCommandSender} - Command sender for entities at specific locations.</li>
 *   <li>{@link cn.nukkit.command.NPCCommandSender} - Command sender for NPCs.</li>
 *   <li>{@link cn.nukkit.command.FormattedCommandAlias} - Command alias with argument substitution.</li>
 *   <li>{@link cn.nukkit.command.CommandExecutor} - Interface for command execution listeners.</li>
 * </ul>
 * <p>
 * The package supports advanced features such as multi-command aliases, argument formatting, permission attachments,
 * and integration with plugin systems. It is designed for extensibility and robust command management in Minecraft server environments.
 *
 * @author PowerNukkitX Project Team
 * @since PowerNukkitX 2.0.0
 */
package cn.nukkit.command;
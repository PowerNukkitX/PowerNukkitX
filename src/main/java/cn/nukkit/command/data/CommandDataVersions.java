package cn.nukkit.command.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a container for multiple versions of {@link CommandData} used in command registration and management.
 * <p>
 * This class is typically used to store different versions of command metadata, allowing for compatibility
 * with various protocol versions or command structures. The {@link #versions} list usually contains a single
 * {@link CommandData} instance, but can be extended to support multiple versions if needed.
 * <p>
 * Features:
 * <ul>
 *   <li>Stores a list of {@link CommandData} objects representing different command versions.</li>
 *   <li>Supports extensibility for future protocol or command changes.</li>
 *   <li>Used in command registration, packet serialization, and command management systems.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Instantiate and add {@link CommandData} objects to the {@link #versions} list.</li>
 *   <li>Use in command registration and packet handling to provide versioned command metadata.</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * CommandDataVersions dataVersions = new CommandDataVersions();
 * dataVersions.versions.add(new CommandData());
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CommandData
 */
public class CommandDataVersions {

    //size 1
    public List<CommandData> versions = new ArrayList<>();

}
# PowerNukkitX Architecture Guide

## Overview

PowerNukkitX is a high-performance Minecraft Bedrock Edition server implementation written in Java. It uses a modular architecture with a central `Server` class that orchestrates multiple subsystems through a tick-based game loop.

## Core Package Structure

cn.nukkit/
├── Server.java                    # Main server orchestrator
├── Player.java                    # Player entity and client connection
├── Nukkit.java                    # Entry point and launcher
├── PlayerHandle.java              # Player handle management
├── network/                       # Networking layer
├── event/                         # Event system (core to architecture)
├── plugin/                        # Plugin system
├── scheduler/                     # Task scheduling system
├── registry/                      # Object registries (critical pattern)
├── level/                         # World/Level management
├── entity/                        # Entity system
├── block/                         # Block system
├── blockentity/                   # Block entity system
├── item/                          # Item system
├── inventory/                     # Inventory system
├── command/                       # Command system
├── permission/                    # Permission system
├── metadata/                      # Metadata storage system
├── config/                        # Configuration management
├── nbt/                           # NBT data format
├── utils/                         # Utility classes
└── api/                           # API annotations for stability

## Key Architectural Components

### 1. Entry Point and Initialization Flow

Location: `Nukkit.java::main()`

Process:
1. Process command-line arguments (port, language, debug options)
2. Load/create configuration (pnx.yml)
3. Run setup wizard if first-time setup
4. Create Server instance
5. Server initializes all subsystems
6. tickProcessor() starts the main game loop

### 2. Server Class - Central Orchestrator

Location: `Server.java`

The Server class is the heart of PowerNukkitX with key responsibilities:

- ForkJoinPool for async compute tasks (terrain generation, compression)
- ServerScheduler for task scheduling
- PluginManager for plugin lifecycle
- Network for player connections
- Level management for worlds
- Player tracking and management
- Service management
- Metadata stores (EntityMetadataStore, PlayerMetadataStore, LevelMetadataStore)
- Resource pack management
- Scoreboard and function management

### 3. Game Loop - Tick Processor

Location: `Server.java::tickProcessor()` and `Server.java::tick()`

Runs at 20 ticks per second (50ms per tick):

1. tickProcessor() - Main loop controller
    - Maintains timing (50ms target per tick)

2. tick() - Per-tick operations:
   a. network.processInterfaces()     // Handle network I/O
   b. scheduler.mainThreadHeartbeat() // Execute main-thread tasks
   c. checkTickUpdates()              // Update entities/blocks
    - For each Level: level.doTick()
      d. Event calling and player updates
      e. Auto-save if configured

The loop sleeps to maintain exactly 20 ticks per second, with tick averaging and auto-scaling for overloaded servers.

### 4. Registry System - Core Design Pattern

Location: `registry/`

PowerNukkitX uses a registry pattern for all major game objects:

Registries.BLOCK          - Block class registry
Registries.ITEM           - Item class registry
Registries.ENTITY         - Entity class registry
Registries.BLOCKENTITY    - Block entity registry
Registries.BLOCKSTATE     - Block state to runtime ID mapping
Registries.POTION         - Potion effects
Registries.BIOME          - World biomes
Registries.FUEL           - Fuel burning properties
Registries.CREATIVE       - Creative mode items
Registries.RECIPE         - Crafting recipes
Registries.PACKET         - Network packets
Registries.EFFECT         - Visual effects
Registries.GENERATOR      - World generators
Registries.POPULATOR      - Chunk populators
Registries.GENERATE_FEATURE - World features

Key Pattern Uses:
- Fast reflection for class scanning
- Caching mechanisms for performance
- Definition objects (e.g., BlockDefinition) for metadata
- Plugin-provided custom definitions support

### 5. Event System - Decoupled Communication

Location: `event/`

Core architecture:

Event (abstract base class)
- Player events (PlayerLoginEvent, PlayerQuitEvent, etc.)
- Block events (BlockPlaceEvent, BlockBreakEvent, etc.)
- Entity events (EntityDamageEvent, EntityMoveEvent, etc.)
- Level events (LevelLoadEvent, LevelSaveEvent, etc.)
- Server events (ServerStartedEvent, ServerStopEvent, etc.)

EventHandler annotation marks listener methods
EventPriority controls execution order
PluginManager.callEvent() dispatches to listeners

Key Principle: All significant game actions fire events, allowing plugins to:
- Intercept and cancel actions
- Modify behavior
- React to game state changes
- Avoid tight coupling

### 6. Plugin System - Extensibility

Location: `plugin/`

PluginManager
- Loads plugins (supports .jar files)
- Manages lifecycle (onLoad, onEnable, onDisable)
- Registers event listeners
- Registers plugin commands
- Manages permissions

Plugin interface provides:
- onLoad() - Before other plugins load
- onEnable() - Initialization
- onDisable() - Cleanup
- Permission system integration

Integration Points:
- Plugins register event listeners via EventHandler annotations
- Plugins add commands via SimpleCommandMap
- Plugins store metadata on entities/players/levels
- Plugins access Server via Server.getInstance()

### 7. Networking Layer - Bedrock Protocol

Location: `network/`

Architecture:
- Uses Netty for high-performance async NIO
- RakNet protocol for UDP connections
- BedrockSession per connected client
    - Handles encryption/decryption
    - Packet encoding/decoding
- DataPacketProcessor processes each packet type (~100+ types)
- Query handler for server discovery

Key Points:
- Netty provides epoll/kqueue support for high performance
- Packets use BinaryStream serialization (size-prefixed binary)
- Encryption at session level
- Protocol version in ProtocolInfo interface

### 8. Block System - Complex State Management

Location: `block/`

Block (abstract)
- Extends Position and Metadatable
- Contains BlockState (immutable)
- References BlockDefinition (properties/behavior)
- BlockProperties define state variations (facing, age, etc.)
- Supports ~200+ block subclasses
- Custom blocks (CustomBlock, CustomBlockDefinition)

BlockDefinition (recent addition - feat/blockdefinition branch)
- Centralizes block properties/behavior
- Properties: hardness, resistance, lightLevel, etc.
- Behaviors: canHarvestWithHand, canSilkTouch, etc.
- Support for defaults (BlockSolid, BlockTransparent)

Interactions:
- Block registry maps IDs to Block classes
- Block state registry maps state to runtime ID
- ItemBlock connects block to inventory
- Level tracks block changes for physics/events

### 9. Entity System - Dynamic World Objects

Location: `entity/`

Entity (abstract)
- Extends Position and Metadatable
- Has EntityDataMap (client-side properties)
- Collision detection (AxisAlignedBB)
- Movement and physics
- Subclasses:
    - EntityLiving (has health)
    - EntityHuman (player/NPC)
    - EntityMob (monsters)
    - EntityAnimal (passive)
    - EntityItem (dropped items)
    - EntityProjectile (arrows)
    - CustomEntity (plugin-provided)

Entity Lifecycle:
1. Create (spawnEntity())
2. loadNBT() - Load from NBT data
3. Update loop (onUpdate())
    - Apply motion
    - Check collisions
    - Update effects
    - Fire events
    - Sync to clients
4. Close/Death

### 10. Level/World System - Persistent Worlds

Location: `level/`

Level (manages a single world/dimension)
- Extends Metadatable
- LevelProvider (persistent storage backend)
    - LevelDBProvider (uses LevelDB format)
- Chunk management
    - Cached in memory
    - ChunkLoader interface
    - Load/unload events
- Block updates via scheduler
- Tick-based physics simulation
    - Liquid flow, gravity, growth
    - Redstone systems
- Entity storage and updates
- Lighting system
- Weather/thunder
- Game rules (per-level)
- Dimensions (Overworld, Nether, End)

Level.doTick(tickCounter):
1. Tick updates (water, lava, fire, crops)
2. Block entity updates
3. Entity updates
4. Send updates to players
5. Check unload conditions

Multi-Level Support:
- Server can manage multiple levels
- Players teleport between levels
- Levels load/unload dynamically
- Each level has own physics/entities/blocks

### 11. Player System - Client Connection

Location: `Player.java`

Player extends EntityHuman
- Network connection (BedrockSession)
- Inventory (2D grid)
- Armor/Equipment
- Chat, commands, permissions
- Chunk loading (PlayerChunkManager)
- Forms/Dialogs
- Camera control
- Adventure settings
- Scoreboard integration

Player Lifecycle:
- Login (authentication, spawn)
- Active (in-game)
- Logout (cleanup, save)

Key Events Fired:
- PlayerPreLoginEvent
- PlayerLoginEvent
- PlayerQuitEvent
- PlayerMoveEvent
- PlayerInteractEvent
- PlayerDamageEvent

### 12. Task Scheduler System

Location: `scheduler/`

ServerScheduler
- Synchronous tasks (main thread)
    - Delayed (after N ticks)
    - Repeating (every N ticks)
    - Immediate
- Asynchronous tasks (worker threads)
    - AsyncTask subclass
    - onRun() in async context
    - onComplete() on main thread
- Configurable worker thread pool

Async Pattern:
Plugin schedules async task
→ Worker thread executes onRun()
→ Calls onComplete() on main thread
→ Can safely modify world

### 13. Command System - In-Game Control

Location: `command/`

CommandSender interface
- Player, ConsoleCommandSender
- Send messages
- Check permissions
- Execute commands

Command (abstract)
- Name, description, usage
- Required permission
- execute() method
- Static permission registration

SimpleCommandMap
- Registry of all commands
- Dispatches user input
- Handles permission checking
- Supports plugin commands

FunctionManager
- Function execution (data pack style)
- Command sequences

## Important Architectural Patterns

### 1. Singleton with getInstance()
Server.getInstance() provides global access to server instance.

### 2. Registry Pattern
Centralized object management:
- Fast lookup by ID or identifier
- Version sync with client
- Plugin extensibility
- Memory efficiency

### 3. Event-Driven Architecture
- Actions fire events
- Plugins listen to events
- Events can be cancelled/modified
- Modular, extensible design

### 4. Metadata System
Extendable property storage on:
- Entities (EntityMetadataStore)
- Players (PlayerMetadataStore)
- Levels (LevelMetadataStore)
- Blocks (BlockMetadataStore)

### 5. Tick-Based Simulation
- Fixed 20 TPS
- Deterministic updates
- Synchronizes with client
- Auto-scales on overload

### 6. Async I/O with Main-Thread Callbacks
Plugin schedules async task → Worker executes → onComplete() on main thread

### 7. Network Abstraction Layer
- Netty for I/O abstraction
- RakNet protocol handler
- BedrockSession encapsulation
- DataPacket polymorphism

### 8. Block State Immutability
- BlockState immutable
- Single cached instance per state
- Properties stored separately
- Reduces memory and simplifies concurrency

## Performance Optimizations

- ForkJoinPool for parallel terrain generation
- Chunk caching for frequently accessed chunks
- BlockState interning (single instance per state)
- FastReflection for cached reflective access
- Async I/O for database/file operations
- Netty NIO for high-performance networking
- Optional level threading for multi-core servers
- Automatic tick rate scaling for server load

## Configuration System

Location: `config/ServerSettings.java` (mapped from `pnx.yml`)

Key sections:
- baseSettings: Port, MOTD, max players, offline mode
- gameplaySettings: Gamemode, difficulty, PvP, nether/end
- networkSettings: Encryption, query, protocol
- performanceSettings: Async workers, tick rates
- levelSettings: World generation, auto-save
- debugSettings: Logging level

## Concurrency Model

Main execution contexts:
- Main Thread: Server tick thread (all world modifications)
- Network Threads: Netty EventLoopGroup (packet I/O, read-only)
- Async Workers: AsyncPool (database, file I/O)
- Compute Threads: ForkJoinPool (terrain, compression)
- Console Thread: User input

Safe interaction via:
- TaskScheduler for main-thread execution
- AsyncTask for async work with main-thread callback
- ConcurrentHashMap for thread-safe collections
- Volatile fields for visibility

## Entry Points for New Developers

1. Add a new command: Extend Command, register in plugin
2. Listen to events: Implement Listener, add @EventHandler methods
3. Create a plugin: Extend JavaPlugin
4. Add custom block: Extend Block, register in BlockRegistry
5. Add custom entity: Extend Entity, register in EntityRegistry
6. Schedule tasks: Use scheduler.scheduleTask/scheduleRepeatingTask
7. Access configuration: Use Server.getInstance().getSettings()
8. Store metadata: Use entity.setMetadata()
9. Send packets: Use player.sendPacket()
10. Interact with world: Use level.setBlock(), level.spawnEntity()

## Block Definition System (feat/blockdefinition branch)

Recent architectural improvement visible in codebase:

Block subclass (e.g., BlockOak)
→ Has BlockDefinition (builder pattern)
→ BlockDefinition.canHarvestWithHand = true
→ BlockDefinition.hardness = 2.0
→ Block registry uses definition
→ Support for defaults (BlockSolid, BlockTransparent)

Benefits:
- Simplified block properties
- Centralized behavior definition
- Easier to add new blocks
- Better for custom blocks

## Data Flow Examples

### Example 1: Player Breaking a Block

Player.PlayerInteractEvent → Event fired
→ Plugin can cancel event
→ Level.setBlock(position, air)
→ Block triggers physics (block update)
→ Updates schedule block neighbors for update
→ UpdateBlockPacket sent to clients
→ Block drops item (EntityItem spawned)

### Example 2: Scheduled Task Execution

Plugin schedules repeating task (20 tick period)
→ ServerScheduler queues in taskMap
→ Each tick: scheduler.mainThreadHeartbeat() processes
→ Task.onRun(currentTick) executed on main thread
→ If repeats, re-queued for next scheduled time

### Example 3: Block Definition System

Block subclass → Has BlockDefinition → Properties centralized
→ Block registry uses definition
→ Support for defaults (BlockSolid, BlockTransparent)

## Future Architecture Improvements

1. BlockDefinition System - Centralizing block properties
2. Block State Updater - Supporting format migrations
3. Ticking Area Manager - Efficient chunk loading
4. Position Tracking Service - Advanced entity tracking
5. Vibration System - Sculk sensor mechanics
6. CustomEntity/CustomBlock - Beyond hardcoded types

## Key Relationships Between Major Components

1. **Server → Registry**: Server initializes all registries during startup
2. **Server → Level**: Server manages multiple levels, handles level loading/unloading
3. **Level → Chunk → Block/Entity**: Blocks and entities exist within chunks in levels
4. **Player → Entity → Level**: Player is special entity that exists in a level
5. **Server → Scheduler → Event System**: Events fire through scheduler on main thread
6. **Network → Player → Server**: Network packets routed to player handlers
7. **Plugin → Event/Command/Scheduler**: Plugins extend via events and commands
8. **Block/Entity → Metadata**: Arbitrary data attachment via metadata system
9. **Inventory → Item → Block/Entity**: Items in inventory, items as entities, blocks as items
10. **Level → BlockEntity/Entity → Scheduler**: Entities and block entities updated each tick

   ---

This architecture has evolved through multiple Nukkit-based projects and supports both stability (via API annotations) and extensibility (via registries and events).
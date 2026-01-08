# Custom Commands Mod - Hunter vs Runner

A server-side Minecraft mod for version 1.21.4 that implements a Hunter vs Runner game mode with a unique senses system.

## Features

### Role Management
- **Hunters**: Unlimited players can be hunters. Each hunter receives a compass that tracks the runner's position in real-time.
- **Runner**: Only 1 player can be the runner at a time.
- Automatic compass tracking updates every tick for hunters
- Compass given automatically on hunter respawn

### Senses System
The mod includes a unique "senses" mechanic that applies various effects to players:

- **Sight**: Makes all hunters invisible (5 minutes)
- **Taste**: Gives the runner hunger effect (5 minutes)
- **Hearing**: Mutes all sounds for the runner (5 minutes) - displays message
- **Feel**: Gives the runner mining fatigue (5 minutes)
- **Smell**: Gives the runner nausea effect (5 minutes)

### Auto-Cycling
The senses system can automatically cycle through random senses every 5 minutes when started.

## Commands

**All commands require OP permissions (level 2).**

### Role Assignment Commands
- `/hunter` - Assigns yourself as a Hunter and gives you a tracking compass
- `/runner` - Assigns yourself as the Runner (only if no runner exists)
- `/unassign` - Removes your Hunter/Runner role

### Senses Commands
- `/senses start` - Starts automatic cycling of random senses (every 5 minutes)
- `/senses stop` - Stops the automatic cycling and clears all effects
- `/senses sight` - Manually applies the Sight sense (invisibility for hunters)
- `/senses taste` - Manually applies the Taste sense (hunger for runner)
- `/senses hearing` - Manually applies the Hearing sense (sound muting for runner)
- `/senses feel` - Manually applies the Feel sense (mining fatigue for runner)
- `/senses smell` - Manually applies the Smell sense (nausea for runner)

## How to Play

1. **Setup**: Have an OP player assign roles using `/hunter` and `/runner`
2. **Start the Game**: Use `/senses start` to begin the automatic sense cycling
3. **Gameplay**: 
   - Hunters track the runner using their compasses
   - Every 5 minutes, a random sense is lost, applying effects to players
   - A title message appears for ~5 seconds showing which sense was lost
4. **Stop**: Use `/senses stop` to end the game and clear effects

## Installation

1. Install NeoForge for Minecraft 1.21.4
2. Place the mod JAR file in your server's `mods` folder
3. Start your server
4. Grant OP permissions to players who will manage the game

## Building from Source

```bash
./gradlew build
```

The compiled mod will be in `build/libs/`

## Technical Details

- **Minecraft Version**: 1.21.4
- **Mod Loader**: NeoForge 21.4.156
- **Type**: Server-side only
- **Permissions**: Requires OP level 2

## Features Implementation

### Compass Tracking
- Uses Minecraft's LodestoneTracker data component
- Updates every game tick to follow the runner
- Automatically given to hunters on respawn

### Senses Effects
- All effects last exactly 5 minutes (6000 ticks)
- Title messages display for ~5 seconds
- Automatic random cycling every 5 minutes
- Can be manually triggered individually

### Thread-Safe Design
- Uses ScheduledExecutorService for timing
- Singleton pattern for managers
- Safe concurrent access to game state

## Credits

Created for a custom Minecraft server game mode.

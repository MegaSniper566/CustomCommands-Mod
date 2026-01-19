# Custom Commands Mod - Hunter vs Runner

A server-side Minecraft mod for version 1.21.4 that implements a Hunter vs Runner game mode with a unique senses system and bodyguard mechanic.

## Features

### Role Management
- **Hunters**: Unlimited players can be hunters. Each hunter receives a compass that tracks the runner's position.
- **Runner**: Only 1 player can be the runner at a time.
- **Bodyguard**: The runner can summon a spectator to help protect them for 15 minutes.
- Compass tracking updates on right-click (works in main hand and offhand)
- Compass given automatically on hunter respawn

### Senses System
The mod includes a unique "senses" mechanic that applies various effects to players:

- **Sight**: Makes all hunters invisible (3 minutes)
- **Taste**: Gives the runner hunger effect (3 minutes)
- **Hearing**: Mutes all sounds for the runner (3 minutes) - displays message
- **Feel**: Gives the runner mining fatigue (3 minutes)
- **Smell**: Gives the runner nausea effect (3 minutes)

### Senses Auto-Cycling Features
- Automatically cycles through random senses every 3 minutes when started
- Never repeats the same sense twice in a row
- 5-second cooldown between sense changes to prevent effect conflicts
- Displays "CHOOSING NEW SENSE TO BE LOST..." message during cooldown
- Title message shows which sense was lost for ~5 seconds

### Bodyguard System
- Runner can summon a random spectator as a bodyguard
- Bodyguard receives saturation effect for 15 minutes
- Bodyguard gets a compass to track the runner
- Time alerts sent every 5 minutes to bodyguard and runner
- Automatically returns bodyguard to spectator mode after 15 minutes
- Broadcasts "Bodyguard Summoned" message to all players

## Commands

### Role Assignment Commands (No OP Required)
- `/hunter` - Assigns yourself as a Hunter and gives you a tracking compass
- `/runner` - Assigns yourself as the Runner (only if no runner exists)
- `/unassign` - Removes your Hunter/Runner role (Requires OP)

### Senses Commands (Requires OP Level 2)
- `/senses start` - Starts automatic cycling of random senses (every 3 minutes)
- `/senses stop` - Stops the automatic cycling and clears all effects
- `/senses sight` - Manually applies the Sight sense (invisibility for hunters)
- `/senses taste` - Manually applies the Taste sense (hunger for runner)
- `/senses hearing` - Manually applies the Hearing sense (sound muting for runner)
- `/senses feel` - Manually applies the Feel sense (mining fatigue for runner)
- `/senses smell` - Manually applies the Smell sense (nausea for runner)

### Bodyguard Commands (No OP Required)
- `/bodyguard summon` - Summons a random spectator as bodyguard (Runner only)
- `/bodyguard end` - Ends bodyguard duty early (Bodyguard or Runner only)
- `/bodyguard equip` - Makes the command user the bodyguard (Anyone)

## How to Play

1. **Setup**: Players assign themselves roles using `/hunter` and `/runner`
2. **Start the Game**: An OP uses `/senses start` to begin the automatic sense cycling
3. **Gameplay**: 
   - Hunters track the runner using their compasses (right-click to update)
   - Every 3 minutes, a random sense is lost, applying effects to players
   - Runner can summon a bodyguard for protection using `/bodyguard summon`
   - Title messages appear showing game events
4. **Stop**: An OP uses `/senses stop` to end the game and clear effects

## Installation

1. Install NeoForge for Minecraft 1.21.4
2. Place the mod JAR file in your server's `mods` folder
3. Start your server
4. Grant OP permissions to players who will manage senses

## Building from Source

```bash
./gradlew build
```

The compiled mod will be in `build/libs/`

## Technical Details

- **Minecraft Version**: 1.21.4
- **Mod Loader**: NeoForge 21.4.156
- **Type**: Server-side only
- **Permissions**: Mixed (some commands require OP, some don't)

## Features Implementation

### Compass Tracking
- Uses Minecraft's LodestoneTracker data component
- Updates on right-click interaction (prevents glitching)
- Works in both main hand and offhand
- Automatically given to hunters on respawn

### Senses Effects
- All effects last exactly 3 minutes (3600 ticks)
- Title messages display for ~5 seconds
- 5-second cooldown between sense changes with transition message
- Automatic random cycling every 3 minutes
- No duplicate senses in consecutive cycles
- Can be manually triggered individually

### Bodyguard System
- 15-minute duration with automatic timer
- Alerts every 5 minutes about remaining time
- Teleports bodyguard to runner on summon
- Saturation effect prevents hunger
- Tracking compass provided
- Broadcasts to all hunters and runners

### Thread-Safe Design
- Uses ScheduledExecutorService for timing
- Singleton pattern for managers
- Safe concurrent access to game state

## Credits

Created for a custom Minecraft server game mode.

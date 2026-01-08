package com.mega.customcommands.manager;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class GameManager {
    private static GameManager instance;
    private MinecraftServer server;
    
    private UUID runner;
    private Set<UUID> hunters = new HashSet<>();
    
    private GameManager() {}
    
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    
    public void setServer(MinecraftServer server) {
        this.server = server;
    }
    
    public MinecraftServer getServer() {
        return server;
    }
    
    public boolean setRunner(UUID playerUUID) {
        if (runner != null && !runner.equals(playerUUID)) {
            return false; // Only one runner allowed
        }
        // Remove from hunters if they were a hunter
        hunters.remove(playerUUID);
        runner = playerUUID;
        return true;
    }
    
    public void addHunter(UUID playerUUID) {
        // Remove from runner if they were the runner
        if (runner != null && runner.equals(playerUUID)) {
            runner = null;
        }
        hunters.add(playerUUID);
    }
    
    public void unassign(UUID playerUUID) {
        if (runner != null && runner.equals(playerUUID)) {
            runner = null;
        }
        hunters.remove(playerUUID);
    }
    
    public boolean isRunner(UUID playerUUID) {
        return runner != null && runner.equals(playerUUID);
    }
    
    public boolean isHunter(UUID playerUUID) {
        return hunters.contains(playerUUID);
    }
    
    public UUID getRunner() {
        return runner;
    }
    
    public ServerPlayer getRunnerPlayer() {
        if (runner == null || server == null) return null;
        return server.getPlayerList().getPlayer(runner);
    }
    
    public Set<UUID> getHunters() {
        return new HashSet<>(hunters);
    }
    
    public List<ServerPlayer> getHunterPlayers() {
        List<ServerPlayer> hunterPlayers = new ArrayList<>();
        if (server == null) return hunterPlayers;
        
        for (UUID hunterUUID : hunters) {
            ServerPlayer player = server.getPlayerList().getPlayer(hunterUUID);
            if (player != null) {
                hunterPlayers.add(player);
            }
        }
        return hunterPlayers;
    }
    
    public boolean hasRunner() {
        return runner != null;
    }
}

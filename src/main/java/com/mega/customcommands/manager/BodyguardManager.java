package com.mega.customcommands.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BodyguardManager {
    private static BodyguardManager instance;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> timerTask;
    private UUID bodyguardUUID;
    private int remainingMinutes = 0;
    
    private static final int DURATION_MINUTES = 15;
    private static final int ALERT_INTERVAL = 5; // Alert every 5 minutes
    
    private BodyguardManager() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    public static BodyguardManager getInstance() {
        if (instance == null) {
            instance = new BodyguardManager();
        }
        return instance;
    }
    
    public boolean hasActiveBodyguard() {
        return bodyguardUUID != null;
    }
    
    public UUID getBodyguardUUID() {
        return bodyguardUUID;
    }
    
    @SuppressWarnings("null")
    public void setBodyguard(ServerPlayer player) {
        // Clear any existing bodyguard first
        end();
        
        bodyguardUUID = player.getUUID();
        remainingMinutes = DURATION_MINUTES;
        
        // Set to survival mode
        player.setGameMode(GameType.SURVIVAL);
        
        // Teleport to runner
        GameManager gm = GameManager.getInstance();
        ServerPlayer runner = gm.getRunnerPlayer();
        if (runner != null) {
            player.teleportTo(runner.serverLevel(), runner.getX(), runner.getY(), runner.getZ(), 
                java.util.Set.of(), runner.getYRot(), runner.getXRot(), true);
        }
        
        // Give saturation effect for 15 minutes (18000 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 18000, 0, false, false));
        
        // Give compass
        player.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COMPASS));
        
        // Broadcast message
        broadcastMessage(Component.literal("Bodyguard Summoned"));
        
        // Start timer
        startTimer();
    }
    
    private void startTimer() {
        // Cancel any existing timer
        if (timerTask != null) {
            timerTask.cancel(false);
        }
        
        // Schedule alerts every 5 minutes
        timerTask = scheduler.scheduleAtFixedRate(() -> {
            remainingMinutes -= ALERT_INTERVAL;
            
            if (remainingMinutes > 0) {
                // Send alert to bodyguard and runner
                sendTimeAlert(remainingMinutes);
            } else {
                // Time's up - set bodyguard back to spectator
                endBodyguardDuty();
            }
        }, ALERT_INTERVAL * 60, ALERT_INTERVAL * 60, TimeUnit.SECONDS);
    }
    
    @SuppressWarnings("null")
    private void sendTimeAlert(int minutes) {
        GameManager gm = GameManager.getInstance();
        Component message = Component.literal("Bodyguard Time Remaining: " + minutes + " minutes");
        
        // Send to runner
        ServerPlayer runner = gm.getRunnerPlayer();
        if (runner != null) {
            runner.sendSystemMessage(message);
        }
        
        // Send to bodyguard
        ServerPlayer bodyguard = getBodyguardPlayer();
        if (bodyguard != null) {
            bodyguard.sendSystemMessage(message);
        }
    }
    
    @SuppressWarnings("null")
    private void endBodyguardDuty() {
        ServerPlayer bodyguard = getBodyguardPlayer();
        if (bodyguard != null) {
            bodyguard.setGameMode(GameType.SPECTATOR);
            bodyguard.sendSystemMessage(Component.literal("Your bodyguard duty has ended!"));
        }
        
        // Clear the bodyguard
        bodyguardUUID = null;
        
        // Cancel timer
        if (timerTask != null) {
            timerTask.cancel(false);
            timerTask = null;
        }
    }
    
    @SuppressWarnings("null")
    public void end() {
        if (bodyguardUUID == null) {
            return;
        }
        
        ServerPlayer bodyguard = getBodyguardPlayer();
        if (bodyguard != null) {
            bodyguard.setGameMode(GameType.SPECTATOR);
            bodyguard.sendSystemMessage(Component.literal("Bodyguard duty ended by command."));
        }
        
        bodyguardUUID = null;
        
        // Cancel timer
        if (timerTask != null) {
            timerTask.cancel(false);
            timerTask = null;
        }
    }
    
    @SuppressWarnings("null")
    private ServerPlayer getBodyguardPlayer() {
        if (bodyguardUUID == null) return null;
        
        GameManager gm = GameManager.getInstance();
        if (gm.getServer() == null) return null;
        
        return gm.getServer().getPlayerList().getPlayer(bodyguardUUID);
    }
    
    @SuppressWarnings("null")
    private void broadcastMessage(Component message) {
        GameManager gm = GameManager.getInstance();
        
        // Send to runner
        ServerPlayer runner = gm.getRunnerPlayer();
        if (runner != null) {
            runner.sendSystemMessage(message);
        }
        
        // Send to all hunters
        for (ServerPlayer hunter : gm.getHunterPlayers()) {
            hunter.sendSystemMessage(message);
        }
        
        // Send to bodyguard
        ServerPlayer bodyguard = getBodyguardPlayer();
        if (bodyguard != null) {
            bodyguard.sendSystemMessage(message);
        }
    }
}

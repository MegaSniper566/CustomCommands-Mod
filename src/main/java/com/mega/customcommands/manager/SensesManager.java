package com.mega.customcommands.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class SensesManager {
    private static SensesManager instance;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> cycleTask;
    private boolean isActive = false;
    private Random random = new Random();
    private Sense lastSense = null; // Track the last sense applied
    
    private static final int EFFECT_DURATION = 3 * 60 * 20; // 3 minutes in ticks
    private static final int CYCLE_INTERVAL = 3 * 60; // 3 minutes in seconds
    
    public enum Sense {
        SIGHT("Sight"),
        TASTE("Taste"),
        HEARING("Hearing"),
        FEEL("Feel"),
        SMELL("Smell");
        
        private final String name;
        
        Sense(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private SensesManager() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    public static SensesManager getInstance() {
        if (instance == null) {
            instance = new SensesManager();
        }
        return instance;
    }
    
    public void start() {
        if (isActive) {
            return;
        }
        
        isActive = true;
        
        // Apply first random sense immediately
        applyRandomSense();
        
        // Schedule cycling every 3 minutes
        cycleTask = scheduler.scheduleAtFixedRate(() -> {
            applyRandomSense();
        }, CYCLE_INTERVAL, CYCLE_INTERVAL, TimeUnit.SECONDS);
    }
    
    public void stop() {
        if (!isActive) {
            return;
        }
        
        isActive = false;
        
        if (cycleTask != null) {
            cycleTask.cancel(false);
            cycleTask = null;
        }
        
        // Clear all effects
        clearAllEffects();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    private void applyRandomSense() {
        // Clear all effects first
        clearAllEffects();
        
        // Send cooldown message to all players
        sendCooldownMessage();
        
        // Wait 5 seconds before applying new effect to avoid conflicts
        scheduler.schedule(() -> {
            Sense[] senses = Sense.values();
            Sense randomSense;
            
            // Keep picking until we get a different sense than the last one
            do {
                randomSense = senses[random.nextInt(senses.length)];
            } while (randomSense == lastSense && senses.length > 1);
            
            applySense(randomSense);
        }, 5, TimeUnit.SECONDS);
    }
    
    @SuppressWarnings("null")
    public void applySense(Sense sense) {
        lastSense = sense; // Remember this sense for next time
        GameManager gm = GameManager.getInstance();
        
        switch (sense) {
            case SIGHT:
                // Invisibility for hunters
                for (ServerPlayer hunter : gm.getHunterPlayers()) {
                    hunter.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION, 0, false, false));
                }
                break;
                
            case TASTE:
                // Hunger for runner
                ServerPlayer runner = gm.getRunnerPlayer();
                if (runner != null) {
                    runner.addEffect(new MobEffectInstance(MobEffects.HUNGER, EFFECT_DURATION, 0, false, false));
                }
                break;
                
            case HEARING:
                // Mute sounds for runner (handled client-side via message)
                // We'll send a title message that lasts for 5 seconds and the effect lasts 3 minutes
                break;
                
            case FEEL:
                // Mining fatigue for runner
                ServerPlayer runnerFeel = gm.getRunnerPlayer();
                if (runnerFeel != null) {
                    runnerFeel.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, EFFECT_DURATION, 0, false, false));
                }
                break;
                
            case SMELL:
                // Nausea for runner
                ServerPlayer runnerSmell = gm.getRunnerPlayer();
                if (runnerSmell != null) {
                    runnerSmell.addEffect(new MobEffectInstance(MobEffects.CONFUSION, EFFECT_DURATION, 0, false, false));
                }
                break;
        }
        
        // Send message to all players
        sendSenseMessage(sense);
    }
    
    private void sendSenseMessage(Sense sense) {
        Component message = Component.literal("Sense Lost: " + sense.getName());
        GameManager gm = GameManager.getInstance();
        
        // Send to runner
        ServerPlayer runner = gm.getRunnerPlayer();
        if (runner != null) {
            sendTitleMessage(runner, message);
        }
        
        // Send to all hunters
        for (ServerPlayer hunter : gm.getHunterPlayers()) {
            sendTitleMessage(hunter, message);
        }
    }
    
    private void sendTitleMessage(ServerPlayer player, Component message) {
        // Set animation timings: fadeIn=10 ticks, stay=100 ticks (~5 seconds), fadeOut=10 ticks
        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(10, 100, 10);
        player.connection.send(animationPacket);
        
        // Send the title message
        @SuppressWarnings("null")
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(message);
        player.connection.send(titlePacket);
    }
    
    private void sendCooldownMessage() {
        Component message = Component.literal("CHOOSING NEW SENSE TO BE LOST...");
        GameManager gm = GameManager.getInstance();
        
        // Send to runner
        ServerPlayer runner = gm.getRunnerPlayer();
        if (runner != null) {
            sendTitleMessage(runner, message);
        }
        
        // Send to all hunters
        for (ServerPlayer hunter : gm.getHunterPlayers()) {
            sendTitleMessage(hunter, message);
        }
    }
    
    @SuppressWarnings("null")
    private void clearAllEffects() {
        GameManager gm = GameManager.getInstance();
        
        // Clear effects from runner
        ServerPlayer runner = gm.getRunnerPlayer();
        if (runner != null) {
            runner.removeEffect(MobEffects.HUNGER);
            runner.removeEffect(MobEffects.DIG_SLOWDOWN);
            runner.removeEffect(MobEffects.CONFUSION);
        }
        
        // Clear effects from hunters
        for (ServerPlayer hunter : gm.getHunterPlayers()) {
            hunter.removeEffect(MobEffects.INVISIBILITY);
        }
    }
}

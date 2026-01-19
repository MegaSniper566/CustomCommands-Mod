package com.mega.customcommands.command;

import com.mega.customcommands.manager.GameManager;
import com.mega.customcommands.manager.SensesManager;
import com.mega.customcommands.manager.BodyguardManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ModCommands {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register /hunter command
        dispatcher.register(Commands.literal("hunter")
            .executes(ModCommands::hunterCommand)
        );
        
        // Register /runner command
        dispatcher.register(Commands.literal("runner")
            .executes(ModCommands::runnerCommand)
        );
        
        // Register /unassign command
        dispatcher.register(Commands.literal("unassign")
            .requires(source -> source.hasPermission(2)) // Requires OP
            .executes(ModCommands::unassignCommand)
        );
        
        // Register /senses command with subcommands
        dispatcher.register(Commands.literal("senses")
            .requires(source -> source.hasPermission(2)) // Requires OP
            .then(Commands.literal("start")
                .executes(ModCommands::sensesStartCommand)
            )
            .then(Commands.literal("stop")
                .executes(ModCommands::sensesStopCommand)
            )
            .then(Commands.literal("sight")
                .executes(ModCommands::sensesSightCommand)
            )
            .then(Commands.literal("taste")
                .executes(ModCommands::sensesTasteCommand)
            )
            .then(Commands.literal("hearing")
                .executes(ModCommands::sensesHearingCommand)
            )
            .then(Commands.literal("feel")
                .executes(ModCommands::sensesFeelCommand)
            )
            .then(Commands.literal("smell")
                .executes(ModCommands::sensesSmellCommand)
            )
        );
        
        // Register /bodyguard command with subcommands
        dispatcher.register(Commands.literal("bodyguard")
            .then(Commands.literal("summon")
                .executes(ModCommands::bodyguardSummonCommand)
            )
            .then(Commands.literal("end")
                .executes(ModCommands::bodyguardEndCommand)
            )
            .then(Commands.literal("equip")
                .executes(ModCommands::bodyguardEquipCommand)
            )
        );
    }
    
    private static int hunterCommand(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            GameManager gm = GameManager.getInstance();
            gm.addHunter(player.getUUID());
            
            // Give them a compass immediately
            @SuppressWarnings("null")
            ItemStack compass = new ItemStack(Items.COMPASS);
            player.getInventory().add(compass);
            
            context.getSource().sendSuccess(() -> 
                Component.literal("You have been assigned as a Hunter!"), false);
            return 1;
        }
        return 0;
    }
    
    @SuppressWarnings("null")
    private static int runnerCommand(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            GameManager gm = GameManager.getInstance();
            
            if (gm.hasRunner() && !gm.isRunner(player.getUUID())) {
                context.getSource().sendFailure(
                    Component.literal("There is already a runner! Only one runner is allowed."));
                return 0;
            }
            
            gm.setRunner(player.getUUID());
            context.getSource().sendSuccess(() -> 
                Component.literal("You have been assigned as the Runner!"), false);
            return 1;
        }
        return 0;
    }
    
    private static int unassignCommand(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            GameManager gm = GameManager.getInstance();
            gm.unassign(player.getUUID());
            
            context.getSource().sendSuccess(() -> 
                Component.literal("You have been unassigned from your role."), false);
            return 1;
        }
        return 0;
    }
    
    @SuppressWarnings("null")
    private static int sensesStartCommand(CommandContext<CommandSourceStack> context) {
        SensesManager sm = SensesManager.getInstance();
        
        if (sm.isActive()) {
            context.getSource().sendFailure(
                Component.literal("Senses cycle is already running!"));
            return 0;
        }
        
        sm.start();
        context.getSource().sendSuccess(() -> 
            Component.literal("Senses cycle has been started!"), true);
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int sensesStopCommand(CommandContext<CommandSourceStack> context) {
        SensesManager sm = SensesManager.getInstance();
        
        if (!sm.isActive()) {
            context.getSource().sendFailure(
                Component.literal("Senses cycle is not running!"));
            return 0;
        }
        
        sm.stop();
        context.getSource().sendSuccess(() -> 
            Component.literal("Senses cycle has been stopped!"), true);
        return 1;
    }
    
    private static int sensesSightCommand(CommandContext<CommandSourceStack> context) {
        SensesManager sm = SensesManager.getInstance();
        sm.applySense(SensesManager.Sense.SIGHT);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Applied Sight sense!"), true);
        return 1;
    }
    
    private static int sensesTasteCommand(CommandContext<CommandSourceStack> context) {
        SensesManager sm = SensesManager.getInstance();
        sm.applySense(SensesManager.Sense.TASTE);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Applied Taste sense!"), true);
        return 1;
    }
    
    private static int sensesHearingCommand(CommandContext<CommandSourceStack> context) {
        SensesManager sm = SensesManager.getInstance();
        sm.applySense(SensesManager.Sense.HEARING);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Applied Hearing sense!"), true);
        return 1;
    }
    
    private static int sensesFeelCommand(CommandContext<CommandSourceStack> context) {
        SensesManager sm = SensesManager.getInstance();
        sm.applySense(SensesManager.Sense.FEEL);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Applied Feel sense!"), true);
        return 1;
    }
    
    private static int sensesSmellCommand(CommandContext<CommandSourceStack> context) {
        SensesManager sm = SensesManager.getInstance();
        sm.applySense(SensesManager.Sense.SMELL);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Applied Smell sense!"), true);
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int bodyguardSummonCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(
                Component.literal("This command must be run by a player!"));
            return 0;
        }
        
        BodyguardManager bm = BodyguardManager.getInstance();
        GameManager gm = GameManager.getInstance();
        
        // Check if the player is the runner
        ServerPlayer runner = gm.getRunnerPlayer();
        if (runner == null) {
            context.getSource().sendFailure(
                Component.literal("No runner assigned!"));
            return 0;
        }
        
        if (!player.getUUID().equals(runner.getUUID())) {
            context.getSource().sendFailure(
                Component.literal("Only the runner can summon a bodyguard!"));
            return 0;
        }
        
        // Check if there's already a bodyguard
        if (bm.hasActiveBodyguard()) {
            context.getSource().sendFailure(
                Component.literal("A bodyguard is already active! Use /bodyguard end first."));
            return 0;
        }
        
        // Get all spectators
        List<ServerPlayer> spectators = context.getSource().getServer().getPlayerList().getPlayers().stream()
            .filter(p -> p.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)
            .collect(Collectors.toList());
        
        if (spectators.isEmpty()) {
            runner.sendSystemMessage(Component.literal("No available bodyguards (no spectators online)."));
            context.getSource().sendFailure(
                Component.literal("No spectators available to become bodyguard!"));
            return 0;
        }
        
        // Pick random spectator
        Random random = new Random();
        ServerPlayer bodyguard = spectators.get(random.nextInt(spectators.size()));
        
        // Set as bodyguard
        bm.setBodyguard(bodyguard);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Bodyguard summoned: " + bodyguard.getName().getString()), true);
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int bodyguardEndCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(
                Component.literal("This command must be run by a player!"));
            return 0;
        }
        
        BodyguardManager bm = BodyguardManager.getInstance();
        GameManager gm = GameManager.getInstance();
        
        if (!bm.hasActiveBodyguard()) {
            context.getSource().sendFailure(
                Component.literal("No active bodyguard to end!"));
            return 0;
        }
        
        // Check if player is either the runner or the bodyguard
        ServerPlayer runner = gm.getRunnerPlayer();
        boolean isRunner = runner != null && player.getUUID().equals(runner.getUUID());
        boolean isBodyguard = player.getUUID().equals(bm.getBodyguardUUID());
        
        if (!isRunner && !isBodyguard) {
            context.getSource().sendFailure(
                Component.literal("Only the runner or bodyguard can end bodyguard duty!"));
            return 0;
        }
        
        bm.end();
        
        context.getSource().sendSuccess(() -> 
            Component.literal("Bodyguard duty ended."), true);
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int bodyguardEquipCommand(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
            context.getSource().sendFailure(
                Component.literal("This command must be run by a player!"));
            return 0;
        }
        
        BodyguardManager bm = BodyguardManager.getInstance();
        
        // Check if there's already a bodyguard
        if (bm.hasActiveBodyguard()) {
            context.getSource().sendFailure(
                Component.literal("A bodyguard is already active! Use /bodyguard end first."));
            return 0;
        }
        
        // Check if there's a runner
        GameManager gm = GameManager.getInstance();
        if (gm.getRunnerPlayer() == null) {
            context.getSource().sendFailure(
                Component.literal("No runner assigned!"));
            return 0;
        }
        
        // Set command user as bodyguard
        bm.setBodyguard(player);
        
        context.getSource().sendSuccess(() -> 
            Component.literal("You are now the bodyguard!"), true);
        return 1;
    }
}


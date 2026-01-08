package com.mega.customcommands.command;

import com.mega.customcommands.manager.GameManager;
import com.mega.customcommands.manager.SensesManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
    }
    
    private static int hunterCommand(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            GameManager gm = GameManager.getInstance();
            gm.addHunter(player.getUUID());
            
            // Give them a compass immediately
            ItemStack compass = new ItemStack(Items.COMPASS);
            player.getInventory().add(compass);
            
            context.getSource().sendSuccess(() -> 
                Component.literal("You have been assigned as a Hunter!"), false);
            return 1;
        }
        return 0;
    }
    
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
}

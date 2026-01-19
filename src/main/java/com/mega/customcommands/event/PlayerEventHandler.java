package com.mega.customcommands.event;

import com.mega.customcommands.manager.GameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@SuppressWarnings("unused")
@EventBusSubscriber
public class PlayerEventHandler {
    
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GameManager gm = GameManager.getInstance();
            
            // If the player is a hunter or bodyguard, give them a compass
            if (gm.isHunter(player.getUUID()) || gm.isBodyguard(player.getUUID())) {
                giveTrackingCompass(player);
            }
        }
    }
    
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GameManager gm = GameManager.getInstance();
            
            // Check if player is a hunter or bodyguard and holding a compass
            if (gm.isHunter(player.getUUID()) || gm.isBodyguard(player.getUUID())) {
                ItemStack heldItem = event.getItemStack();
                
                if (heldItem.getItem() instanceof CompassItem) {
                    updateCompassTracking(player, heldItem);
                }
            }
        }
    }
    
    public static void giveTrackingCompass(ServerPlayer player) {
        @SuppressWarnings("null")
        ItemStack compass = new ItemStack(Items.COMPASS);
        player.getInventory().add(compass);
    }
    
    @SuppressWarnings("null")
    public static void updateCompassTracking(ServerPlayer player, ItemStack compass) {
        GameManager gm = GameManager.getInstance();
        ServerPlayer runner = gm.getRunnerPlayer();
        
        if (runner == null) {
            return;
        }
        
        // Use the LodestoneTracker component to point the compass at the runner
        var pos = new net.minecraft.core.GlobalPos(
            runner.level().dimension(),
            runner.blockPosition()
        );
        compass.set(net.minecraft.core.component.DataComponents.LODESTONE_TRACKER, 
            new net.minecraft.world.item.component.LodestoneTracker(
                java.util.Optional.of(pos), 
                false
            )
        );
    }
}
package net.artmaster.xaero_colonies.event;

import net.artmaster.xaero_colonies.ModMain;
import net.artmaster.xaero_colonies.utils.ColonyTools;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = ModMain.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onUpdateColony(EntityEvent.EnteringSection event) {
        if (event.getEntity() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
            ColonyTools.updateColonyCash(player, level);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
            ColonyTools.updateColonyCash(player, level, true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ColonyTools.forget(player);
        }
    }
}

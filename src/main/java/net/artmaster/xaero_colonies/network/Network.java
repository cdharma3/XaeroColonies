package net.artmaster.xaero_colonies.network;

import net.artmaster.xaero_colonies.ModMain;
import net.artmaster.xaero_colonies.utils.ColonyClaimCache;
import net.artmaster.xaero_colonies.utils.ColonyInfo;
import net.artmaster.xaero_colonies.utils.ColonyTools;
import net.artmaster.xaero_colonies.utils.ServerScheduler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.*;


@Mod("xaero_colonies")
@EventBusSubscriber(modid = ModMain.MODID)
public class Network {


    public static PayloadRegistrar registrar;

    // Packet registering
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        registrar = event.registrar("xaero_colonies");


        registrar.playToClient(
                SyncColoniesPacket.TYPE,
                SyncColoniesPacket.CODEC,
                (packet, ctx) -> ctx.enqueueWork(() -> {
                    ColonyClaimCache.setClaims(packet.level(), packet.chunks());

                })
        );




    }


    //Sync info about all of colonies
    public static void syncColonies(ServerPlayer player, Map<Long, ColonyInfo> chunks) {
        PacketDistributor.sendToPlayer(player,
                new SyncColoniesPacket(player.serverLevel().dimension(), chunks)
        );
    }
}

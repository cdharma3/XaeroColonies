package net.artmaster.xaero_colonies.utils;

import com.minecolonies.api.MinecoloniesAPIProxy;
import com.minecolonies.core.colony.Colony;
import net.artmaster.xaero_colonies.network.Network;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ColonyTools {

    // Last claim map sent to each player, so section crossings only resend on a real change.
    private static final Map<UUID, Map<Long, ColonyInfo>> LAST_SENT = new HashMap<>();

    public static void updateColonyCash(ServerPlayer player, ServerLevel level) {
        updateColonyCash(player, level, false);
    }

    public static void updateColonyCash(ServerPlayer player, ServerLevel level, boolean force) {
        Map<Long, ColonyInfo> chunks = new HashMap<>();

        MinecoloniesAPIProxy.getInstance()
                .getColonyManager()
                .getAllColonies()
                .forEach(colony -> {
                    if (colony.getWorld().equals(level)) {
                        int color = colony.getTeamColonyColor().getColor() & 0xFFFFFF;
                        ColonyInfo info = new ColonyInfo(color, colony.getName(), colony.getID());
                        Colony colonyImpl = (Colony) colony;
                        colonyImpl.getClaimData().keySet().forEach(packed -> {
                            ChunkPos pos = new ChunkPos(packed);
                            chunks.put(ChunkPos.asLong(pos.x, pos.z), info);
                        });
                    }
                });

        if (!force && chunks.equals(LAST_SENT.get(player.getUUID()))) {
            return;
        }
        LAST_SENT.put(player.getUUID(), chunks);

        // 10 ticks delay so MineColonies has finished its own claim bookkeeping.
        ServerScheduler.schedule(10, () -> Network.syncColonies(player, chunks));
    }

    public static void forget(ServerPlayer player) {
        LAST_SENT.remove(player.getUUID());
    }
}

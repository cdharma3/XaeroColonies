package net.artmaster.xaero_colonies.utils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.region.LayeredRegionManager;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapRegion;
import xaero.map.world.MapDimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class ColonyClaimCache {

    private static boolean disconnecting = false;

    @SubscribeEvent
    public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        disconnecting = true;
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        disconnecting = false;
    }

    private static final Map<ResourceKey<Level>, Map<Long, ColonyInfo>> CLAIMS = new HashMap<>();

    public static void setClaims(ResourceKey<Level> level, Map<Long, ColonyInfo> chunks) {
        Map<Long, ColonyInfo> previous = CLAIMS.put(level, chunks);
        // The server resends on every chunk-section crossing; an unchanged map needs no redraw.
        if (disconnecting || chunks.equals(previous)) {
            return;
        }

        WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session == null) {
            return;
        }
        MapProcessor processor = session.getMapProcessor();
        // Xaero's World Map 1.45 throws if a region is left refreshing while the session
        // finalizes, so never touch regions once teardown has started.
        if (processor.isFinalizing() || !processor.isMapWorldUsable()) {
            return;
        }
        MapDimension dimension = processor.getMapWorld().getDimension(level);
        if (dimension == null) {
            return;
        }

        LayeredRegionManager regions = dimension.getLayeredMapRegions();
        List<LeveledRegion<?>> loadedRegions = new ArrayList<>(regions.getLoadedListUnsynced());

        for (LeveledRegion<?> leveledRegion : loadedRegions) {
            // Only fully loaded (load state 2), idle regions may be asked to refresh.
            if (leveledRegion instanceof MapRegion region
                    && region.getLoadState() == 2
                    && !region.isBeingWritten()) {
                processor.getMapRegionHighlightsPreparer().prepare(region, false);
                region.requestRefresh(processor, true);
            }
        }
    }

    public static boolean isClaimed(ResourceKey<Level> level, int chunkX, int chunkZ) {
        Map<Long, ColonyInfo> map = CLAIMS.get(level);
        if (map == null) return false;
        return map.containsKey(ChunkPos.asLong(chunkX, chunkZ));
    }

    public static ColonyInfo get(ResourceKey<Level> level, int chunkX, int chunkZ) {
        Map<Long, ColonyInfo> map = CLAIMS.get(level);
        if (map == null) return null;
        return map.get(ChunkPos.asLong(chunkX, chunkZ));
    }

    public static Map<ResourceKey<Level>, Map<Long, ColonyInfo>> getClaims() {
        return CLAIMS;
    }

    public static void clear() {
        CLAIMS.clear();
    }
}

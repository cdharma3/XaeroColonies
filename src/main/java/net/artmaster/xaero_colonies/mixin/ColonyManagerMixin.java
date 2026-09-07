package net.artmaster.xaero_colonies.mixin;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.ColonyManager;
import net.artmaster.xaero_colonies.utils.ColonyTools;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;




//Mixin for syncing data about colony on it's deleting or creating
@Mixin(value = ColonyManager.class, remap = false)
public class ColonyManagerMixin {

    @Inject(
            method = "createColony",
            at = @At(
                    value = "RETURN"
            ),
            remap = false
    )
    private void createColony(ServerLevel w, BlockPos pos, Player player, String colonyName, String pack, CallbackInfoReturnable<IColony> cir) {
        if (player.level() instanceof ServerLevel level && player instanceof ServerPlayer serverPlayer) {
            ColonyTools.updateColonyCash(serverPlayer, level);
        }
    }

    @Inject(
            method = "deleteColony",
            at = @At(
                    value = "RETURN"
            ),
            remap = false
    )
    private void deleteColony(IColony iColony, boolean canDestroy, CallbackInfo ci) {

        var ownerId = iColony.getPermissions().getOwner();
        Player owner = iColony.getWorld().getPlayerByUUID(ownerId);

        if (owner != null && owner.level() instanceof ServerLevel level && owner instanceof ServerPlayer serverPlayer) {
            ColonyTools.updateColonyCash(serverPlayer, level);
        }
    }
}


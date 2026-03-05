package inovation_and_control.inovation_and_control.mixin;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import inovation_and_control.inovation_and_control.registry.ItemRegistry;

import javax.annotation.Nullable;

@Mixin(AbstractSpell.class)
public abstract class MixinAbstractSpell {

    @Shadow(remap = false)
    public abstract CastType getCastType();
    @Inject(method = "canBeCastedBy", at = @At("HEAD"), cancellable = true, remap = false)
    private void calamity$bypassCastChecks(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player, CallbackInfoReturnable<CastResult> cir) {
        try {
            if (CuriosApi.getCuriosHelper().findFirstCurio(player, ItemRegistry.RING_OF_CALAMITY.get()).isPresent()) {
                cir.setReturnValue(new CastResult(CastResult.Type.SUCCESS));
            }
        } catch (Exception e) {
        }
    }
    @Inject(method = "getEffectiveCastTime", at = @At("HEAD"), cancellable = true, remap = false)
    private void calamity$forceInstantCast(int spellLevel, @Nullable LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof Player player) {
            try {
                if (CuriosApi.getCuriosHelper().findFirstCurio(player, ItemRegistry.RING_OF_CALAMITY.get()).isPresent()) {
                    if (this.getCastType() == CastType.CONTINUOUS) {
                        return;
                    }
                    cir.setReturnValue(0);
                }
            } catch (Exception e) {
            }
        }
    }
}
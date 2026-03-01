package inovation_and_control.inovation_and_control.mixin;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import inovation_and_control.inovation_and_control.registry.ItemRegistry;

@Mixin(AbstractSpell.class)
public abstract class MixinAbstractSpell {

    @Inject(method = "canBeCastedBy", at = @At("HEAD"), cancellable = true, remap = false)
    private void canality$bypassCastChecks(int spellLevel, CastSource castSource, MagicData playerMagicData, Player player, CallbackInfoReturnable<CastResult> cir) {
        // 指輪を装備しているかチェック
        // ItemRegistry.RING_OF_CALAMITY.get() を使って判定
        if (CuriosApi.getCuriosHelper().findFirstCurio(player, ItemRegistry.RING_OF_CALAMITY.get()).isPresent()) {
            // 全てのチェックをスルーして SUCCESS を返す
            cir.setReturnValue(new CastResult(CastResult.Type.SUCCESS));
        }
    }
}
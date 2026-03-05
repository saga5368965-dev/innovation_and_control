package inovation_and_control.inovation_and_control.mixin;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilMenu;
import io.redspace.ironsspellbooks.item.Scroll;
import inovation_and_control.inovation_and_control.item.PrimitiveBookItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ArcaneAnvilMenu.class, remap = false) // クラス全体で難読化解決を無効化（外部MOD対策）
public abstract class MixinArcaneAnvilMenu extends ItemCombinerMenu {

    public MixinArcaneAnvilMenu(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }
    @Inject(
            method = "createResult",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void calamity$addPrimitiveBookLogic(CallbackInfo ci) {
        ItemStack baseItemStack = this.inputSlots.getItem(0);
        ItemStack modifierItemStack = this.inputSlots.getItem(1);
        if (baseItemStack.getItem() instanceof PrimitiveBookItem && modifierItemStack.getItem() instanceof Scroll) {
            ISpellContainer bookContainer = ISpellContainer.get(baseItemStack);
            ISpellContainer scrollContainer = ISpellContainer.get(modifierItemStack);

            if (bookContainer != null && scrollContainer != null && !scrollContainer.isEmpty()) {
                SpellData scrollSpell = scrollContainer.getSpellAtIndex(0);
                if (scrollSpell == null || scrollSpell.getSpell() == null) return;

                for (int i = 0; i < bookContainer.getMaxSpellCount(); i++) {
                    SpellData existing = bookContainer.getSpellAtIndex(i);
                    if (existing != null && existing.getSpell() != null &&
                            existing.getSpell().equals(scrollSpell.getSpell())) {
                        return;
                    }
                }
                ItemStack result = baseItemStack.copy();
                ISpellContainerMutable mutable = ISpellContainer.get(result).mutableCopy();
                mutable.setMaxSpellCount(100);
                int nextIndex = mutable.getNextAvailableIndex();

                if (nextIndex != -1 && nextIndex < 100) {
                    mutable.addSpellAtIndex(scrollSpell.getSpell(), scrollSpell.getLevel(), nextIndex, true);
                    ISpellContainer.set(result, mutable.toImmutable());
                    this.resultSlots.setItem(0, result);
                    ci.cancel();
                }
            }
        }
    }
}
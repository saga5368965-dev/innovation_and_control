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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = ArcaneAnvilMenu.class, remap = false)
public abstract class MixinArcaneAnvilMenu extends ItemCombinerMenu {

    public MixinArcaneAnvilMenu(@Nullable MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(pType, pContainerId, pPlayerInventory, pAccess);
    }

    @Inject(
            method = "createResult",
            at = @At("HEAD"),
            cancellable = true
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

                // 既に同じ魔法が入っていないかチェック
                for (int i = 0; i < bookContainer.getMaxSpellCount(); i++) {
                    SpellData existing = bookContainer.getSpellAtIndex(i);
                    if (existing != null && existing.getSpell() != null &&
                            existing.getSpell().equals(scrollSpell.getSpell())) {
                        return;
                    }
                }

                // 結果用アイテムの作成
                ItemStack result = baseItemStack.copy();

                // 重要な修正: NBTを直接操作して強制的に100スロットに
                CompoundTag tag = result.getOrCreateTag();
                CompoundTag spellContainerTag = new CompoundTag();
                ListTag spellsList = new ListTag();

                // 既存のスペルをすべてコピー
                for (int i = 0; i < bookContainer.getMaxSpellCount(); i++) {
                    SpellData existingSpell = bookContainer.getSpellAtIndex(i);
                    if (existingSpell != null && existingSpell.getSpell() != null) {
                        CompoundTag spellEntry = new CompoundTag();
                        spellEntry.putInt("slot", i);
                        spellEntry.putString("spell_id", existingSpell.getSpell().getSpellId());
                        spellEntry.putInt("level", existingSpell.getLevel());
                        spellsList.add(spellEntry);
                    }
                }

                // 新しいスペルを追加
                int nextIndex = bookContainer.getNextAvailableIndex();
                if (nextIndex == -1) {
                    // 詰まっている場合は最初の空きを探す
                    for (int i = 0; i < 100; i++) {
                        boolean occupied = false;
                        for (int j = 0; j < spellsList.size(); j++) {
                            if (spellsList.getCompound(j).getInt("slot") == i) {
                                occupied = true;
                                break;
                            }
                        }
                        if (!occupied) {
                            nextIndex = i;
                            break;
                        }
                    }
                }

                if (nextIndex != -1 && nextIndex < 100) {
                    CompoundTag newSpellEntry = new CompoundTag();
                    newSpellEntry.putInt("slot", nextIndex);
                    newSpellEntry.putString("spell_id", scrollSpell.getSpell().getSpellId());
                    newSpellEntry.putInt("level", scrollSpell.getLevel());
                    spellsList.add(newSpellEntry);

                    spellContainerTag.putInt("max_spell_count", 100);
                    spellContainerTag.put("spells", spellsList);
                    tag.put("spell_container", spellContainerTag);

                    // 念のためAPIでも設定
                    ISpellContainerMutable mutable = ISpellContainer.get(result).mutableCopy();
                    mutable.setMaxSpellCount(100);
                    for (int i = 0; i < spellsList.size(); i++) {
                        CompoundTag entry = spellsList.getCompound(i);
                        // ここでスペルを再設定
                    }
                    ISpellContainer.set(result, mutable.toImmutable());

                    this.resultSlots.setItem(0, result);
                    ci.cancel();
                }
            }
        }
    }
}
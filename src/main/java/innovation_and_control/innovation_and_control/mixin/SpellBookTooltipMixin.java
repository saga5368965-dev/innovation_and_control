package inovation_and_control.inovation_and_control.mixin;

import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpellBook.class)
public class SpellBookTooltipMixin {

    /**
     * スペルブックのツールチップ表示時に、魔法名に登録済みMob名を付け加える
     */
    @Redirect(
            method = "appendHoverText",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/network/chat/Style;)Lnet/minecraft/network/chat/MutableComponent;"),
            remap = false
    )
    private MutableComponent appendMobNameWithStyle(MutableComponent instance, net.minecraft.network.chat.Style style, ItemStack itemStack) {
        // 元々のスタイル適用を行う
        MutableComponent original = instance.withStyle(style);

        // NBTをチェックして、もしMobIDがあれば魔法名にくっつける
        CompoundTag tag = itemStack.getTag();
        if (tag != null && tag.contains("MobID") && original.getString().contains(Component.translatable("spell.inovation_and_control.summon_copy").getString())) {

            // 例: "写身召喚" -> "写身召喚 [ゾンビ]"
            // 文字列比較で判定しているのは、この時点のinstanceが既に加工されたComponentである可能性があるため
            String mobName = tag.getString("MobID").replace("minecraft:", "");
            return original.append(Component.literal(" [" + mobName + "]").withStyle(ChatFormatting.DARK_PURPLE));
        }

        return original;
    }
}
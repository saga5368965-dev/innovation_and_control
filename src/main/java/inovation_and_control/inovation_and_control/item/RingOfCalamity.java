package inovation_and_control.inovation_and_control.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class RingOfCalamity extends Item implements ICurioItem {

    // 属性操作用の固定UUID
    private static final UUID CALAMITY_CAST_UUID = UUID.fromString("c226e9b2-326b-4e1c-b5f7-879685e92134");
    private static final UUID CALAMITY_COOLDOWN_UUID = UUID.fromString("d326e9b2-326b-4e1c-b5f7-879685e92134");

    public RingOfCalamity(Properties properties) {
        super(properties);
        // Forgeのイベントバスに登録（TickEventとSpellOnCastEventを受け取るため）
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // サーバー側かつ開始フェーズのみ処理
        if (event.phase != TickEvent.Phase.START || event.player.level().isClientSide) return;

        Player player = event.player;
        // Curiosスロットにこのアイテムがあるかチェック
        boolean isEquipped = CuriosApi.getCuriosHelper().findFirstCurio(player, this).isPresent();

        // クールタイムと詠唱時間を極限まで（1000%）短縮するモディファイアを操作
        updateAttribute(player, AttributeRegistry.CAST_TIME_REDUCTION.get(), CALAMITY_CAST_UUID, isEquipped);
        updateAttribute(player, AttributeRegistry.COOLDOWN_REDUCTION.get(), CALAMITY_COOLDOWN_UUID, isEquipped);
    }

    private void updateAttribute(Player player, Attribute attribute, UUID uuid, boolean isEquipped) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        if (isEquipped) {
            // 装備中なら 10.0 (1000%加算) を Transient(一時的) に付与
            if (instance.getModifier(uuid) == null) {
                instance.addTransientModifier(new AttributeModifier(uuid, "Calamity Overload", 10.0, AttributeModifier.Operation.ADDITION));
            }
        } else {
            // 外したら削除
            if (instance.getModifier(uuid) != null) {
                instance.removeModifier(uuid);
            }
        }
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.2").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.ability1"));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.ability2"));
        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.warning").withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
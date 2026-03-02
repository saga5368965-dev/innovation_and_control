package inovation_and_control.inovation_and_control.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class RingOfCalamity extends Item implements ICurioItem {

    public RingOfCalamity(Properties properties) {
        super(properties.rarity(Rarity.EPIC).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.2").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.ability1").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.ability2").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.warning").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }
}
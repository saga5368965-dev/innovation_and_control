package Innovation_and_Control.innovation_and_control.item;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

import java.util.List;
import java.util.UUID;

public class RingOfCalamity extends Item implements ICurioItem {

    private static final UUID CALAMITY_CAST_UUID = UUID.fromString("c226e9b2-326b-4e1c-b5f7-879685e92134");
    private static final UUID CALAMITY_COOLDOWN_UUID = UUID.fromString("d326e9b2-326b-4e1c-b5f7-879685e92134");

    private static final Component TOOLTIP_1 = Component.translatable("tooltip.innovation_and_control.ring_of_calamity.1").withStyle(ChatFormatting.GOLD);
    private static final Component TOOLTIP_2 = Component.translatable("tooltip.innovation_and_control.ring_of_calamity.2").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC);
    private static final Component TOOLTIP_WARNING = Component.translatable("tooltip.innovation_and_control.ring_of_calamity.warning").withStyle(ChatFormatting.RED);

    public RingOfCalamity(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(TOOLTIP_1);
        tooltip.add(TOOLTIP_2);
        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.ability1"));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.ring_of_calamity.ability2"));
        tooltip.add(Component.literal(" "));
        tooltip.add(TOOLTIP_WARNING);
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        modifiers.put(AttributeRegistry.CAST_TIME_REDUCTION.get(),
                new AttributeModifier(CALAMITY_CAST_UUID, "Calamity Cast", 0.999, AttributeModifier.Operation.MULTIPLY_TOTAL));
        modifiers.put(AttributeRegistry.COOLDOWN_REDUCTION.get(),
                new AttributeModifier(CALAMITY_COOLDOWN_UUID, "Calamity Cooldown", 0.999, AttributeModifier.Operation.MULTIPLY_TOTAL));

        return modifiers;
    }
    @SubscribeEvent
    public void onSpellCast(SpellOnCastEvent event) {
        if (event.getEntity() != null) {
            CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), this)
                    .ifPresent(slotResult -> {
                        event.setManaCost(0);
                    });
        }
    }
}
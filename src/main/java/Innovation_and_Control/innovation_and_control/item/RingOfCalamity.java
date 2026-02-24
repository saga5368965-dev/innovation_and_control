package Innovation_and_Control.item;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;

import java.util.UUID;

public class RingOfCalamity extends Item implements ICurioItem {

    private static final UUID CALAMITY_CAST_UUID = UUID.fromString("c226e9b2-326b-4e1c-b5f7-879685e92134");
    private static final UUID CALAMITY_COOLDOWN_UUID = UUID.fromString("d326e9b2-326b-4e1c-b5f7-879685e92134");

    public RingOfCalamity(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();

        // 1. 詠唱時間の短縮 (Innovation)
        modifiers.put(AttributeRegistry.CAST_TIME_REDUCTION.get(),
                new AttributeModifier(CALAMITY_CAST_UUID, "Calamity Cast", 10.0, AttributeModifier.Operation.ADDITION));

        // 2. クールタイムの短縮 (Control)
        modifiers.put(AttributeRegistry.COOLDOWN_REDUCTION.get(),
                new AttributeModifier(CALAMITY_COOLDOWN_UUID, "Calamity Cooldown", 10.0, AttributeModifier.Operation.ADDITION));

        return modifiers;
    }

    // 3. マナ消費をゼロにするロジック
    @SubscribeEvent
    public static void onSpellCast(SpellOnCastEvent event) {
        if (event.getEntity() != null) {
            CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), itemStack -> itemStack.getItem() instanceof RingOfCalamity)
                    .ifPresent(slotResult -> {
                        event.setManaCost(0);
                    });
        }
    }
}
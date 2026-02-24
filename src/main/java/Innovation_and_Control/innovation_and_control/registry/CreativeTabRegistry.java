package Innovation_and_Control.innovation_and_control.registry;

import Innovation_and_Control.innovation_and_control.InnovationAndControl;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InnovationAndControl.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + InnovationAndControl.MODID))
            .icon(() -> new ItemStack(ItemRegistry.JAGAN_EYE.get()))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.RING_OF_CALAMITY.get());
                output.accept(ItemRegistry.JAGAN_EYE.get());
            }).build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
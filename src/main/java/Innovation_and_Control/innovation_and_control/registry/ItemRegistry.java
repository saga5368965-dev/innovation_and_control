package Innovation_and_Control.innovation_and_control.registry;

import Innovation_and_Control.innovation_and_control.InnovationAndControl;
import Innovation_and_Control.innovation_and_control.item.JaganEyeItem;
import Innovation_and_Control.innovation_and_control.item.RingOfCalamity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, InnovationAndControl.MODID);

    // 厄災の指輪
    public static final RegistryObject<RingOfCalamity> RING_OF_CALAMITY = ITEMS.register("ring_of_calamity",
            () -> new RingOfCalamity(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // ジャガンアイ（限界突破の邪眼）
    public static final RegistryObject<JaganEyeItem> JAGAN_EYE = ITEMS.register("jagan_eye",
            () -> new JaganEyeItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
package Innovation_and_Control.innovation_and_control;

import Innovation_and_Control.innovation_and_control.item.RingOfCalamity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

@Mod(InnovationAndControl.MODID)
public class InnovationAndControl {
    public static final String MODID = "innovation_and_control";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Registries
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // --- Items ---
    // 厄災の指輪 (Ring of Calamity)
    public static final RegistryObject<Item> RING_OF_CALAMITY = ITEMS.register("ring_of_calamity",
            () -> new RingOfCalamity(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // --- Creative Tab ---
    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RING_OF_CALAMITY.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(RING_OF_CALAMITY.get());
            }).build());

    public InnovationAndControl() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Deferred Registers
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Forge Event Bus への登録 (マナ消費ゼロのイベント判定などで使用)
        MinecraftForge.EVENT_BUS.register(this);

        // RingOfCalamity 内の static なイベントリスナーを登録
        MinecraftForge.EVENT_BUS.register(RingOfCalamity.class);

        LOGGER.info("Innovation and Control: Initialization started.");
    }
}
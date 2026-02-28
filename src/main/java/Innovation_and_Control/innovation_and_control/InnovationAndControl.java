package Innovation_and_Control.innovation_and_control;

import Innovation_and_Control.innovation_and_control.registry.ItemRegistry;
import Innovation_and_Control.innovation_and_control.registry.CreativeTabRegistry;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(InnovationAndControl.MODID)
public class InnovationAndControl {
    public static final String MODID = "innovation_and_control";
    private static final Logger LOGGER = LogUtils.getLogger();

    public InnovationAndControl() {
        IEventBus modEventBus = FMLJavaModLoadingContext
                .get().getModEventBus();
        ItemRegistry.register(modEventBus);
        CreativeTabRegistry.register(modEventBus);

        }
    }

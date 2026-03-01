package inovation_and_control.inovation_and_control.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties; // FoodPropertiesのインポート
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.UUID; // UUIDのインポート

public class GNDrinkItem extends Item {
    // スロット識別のための固定UUID（これが無いとエラーになる場合があります）
    private static final UUID GN_UUID = UUID.fromString("d0000000-0000-0000-0000-000000000000");
    private static final String MODIFIER_NAME = "gn_particle_expansion";

    public GNDrinkItem(Properties properties) {
        // FoodPropertiesのエラーを解決：正しいビルダーを使用
        super(properties.rarity(Rarity.EPIC).food(new FoodProperties.Builder().alwaysEat().build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {

            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                inventory.getCurios().forEach((slotId, handler) -> {
                    double currentAmount = inventory.getModifiers().get(slotId).stream()
                            .filter(mod -> mod.getName().equals(MODIFIER_NAME))
                            .mapToDouble(AttributeModifier::getAmount)
                            .sum();
                    inventory.removeSlotModifier(slotId, GN_UUID);
                    inventory.addPermanentSlotModifier(slotId, GN_UUID, MODIFIER_NAME, currentAmount + 1.0, AttributeModifier.Operation.ADDITION);
                });
            });

            player.displayClientMessage(Component.translatable("msg.innovation_and_control.gn_drink.success")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD), true);

            return stack;
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.innovation_and_control.gn_drink.1").withStyle(ChatFormatting.DARK_GREEN));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.gn_drink.2").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
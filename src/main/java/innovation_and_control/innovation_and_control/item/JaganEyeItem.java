package inovation_and_control.inovation_and_control.item;

import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public class JaganEyeItem extends Item {

    public JaganEyeItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();

        int mode = tag.getInt("EditMode");
        int currentBoost = tag.getInt("BoostLevel");

        if (player.isShiftKeyDown()) {
            mode = (mode + 1) % 4;
            tag.putInt("EditMode", mode);

            if (!level.isClientSide) {
                String modeName = getModeName(mode);
                player.displayClientMessage(Component.translatable("msg.innovation_and_control.jagan_eye.mode_switched")
                        .append(Component.literal(" " + modeName).withStyle(ChatFormatting.AQUA)), true);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
        } else {
            if (currentBoost == Integer.MAX_VALUE) {
                tag.putInt("BoostLevel", 0);
                if (!level.isClientSide) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 0.5f);
                }
            } else {
                int amount = getAmountByMode(mode);
                if (amount == -1) {
                    tag.putInt("BoostLevel", Integer.MAX_VALUE);
                } else {
                    long next = (long) currentBoost + amount;
                    tag.putInt("BoostLevel", (int) Math.min(next, (long) Integer.MAX_VALUE));
                }

                if (!level.isClientSide) {
                    playBoostSound(level, player, tag.getInt("BoostLevel"));
                }
            }
        }

        if (!level.isClientSide) {
            sendLevelStatus(player, tag.getInt("BoostLevel"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private int getAmountByMode(int mode) {
        return switch (mode) {
            case 0 -> 10;
            case 1 -> 100;
            case 2 -> 1000;
            default -> -1;
        };
    }

    private String getModeName(int mode) {
        return switch (mode) {
            case 0 -> "x10";
            case 1 -> "x100";
            case 2 -> "x1000";
            default -> "INFINITY";
        };
    }

    private void sendLevelStatus(Player player, int boost) {
        String display = (boost == Integer.MAX_VALUE) ? "∞" : "+" + boost;
        player.displayClientMessage(Component.translatable("msg.innovation_and_control.jagan_eye.boost_status")
                .append(Component.literal(" " + display).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)), true);
    }

    private void playBoostSound(Level level, Player player, int boost) {
        float pitch = (boost == Integer.MAX_VALUE) ? 2.0f : 0.5f + (Math.min((float)boost, 10000f) / 10000f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 0.8F, pitch);
    }

    @SubscribeEvent
    public void onModifySpellLevel(ModifySpellLevelEvent event) {
        if (event.getEntity() == null) return;

        CuriosApi.getCuriosHelper().findFirstCurio(event.getEntity(), stack -> stack.getItem() instanceof JaganEyeItem)
                .ifPresent(slotResult -> {
                    int boost = slotResult.stack().getOrCreateTag().getInt("BoostLevel");
                    if (boost > 0) {
                        long targetLevel = (long) event.getLevel() + boost;
                        event.setLevel((int) Math.min(targetLevel, (long) Integer.MAX_VALUE));
                    }
                });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        int boost = tag.getInt("BoostLevel");
        int mode = tag.getInt("EditMode");

        tooltip.add(Component.translatable("item.innovation_and_control.jagan_eye").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        tooltip.add(Component.translatable("tooltip.innovation_and_control.jagan_eye.mode")
                .append(Component.literal(": " + getModeName(mode)).withStyle(ChatFormatting.AQUA)));

        String boostDisplay = (boost == Integer.MAX_VALUE) ? "∞" : String.valueOf(boost);
        tooltip.add(Component.translatable("tooltip.innovation_and_control.jagan_eye.current_boost")
                .append(Component.literal(": +" + boostDisplay).withStyle(ChatFormatting.YELLOW)));

        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable("tooltip.innovation_and_control.jagan_eye.usage_complex").withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
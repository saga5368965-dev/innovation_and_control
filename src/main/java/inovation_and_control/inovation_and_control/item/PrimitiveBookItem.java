package inovation_and_control.inovation_and_control.item;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class PrimitiveBookItem extends SpellBook {

    public PrimitiveBookItem(Properties properties) {
        super(100, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack mainStack = player.getItemInHand(hand);
        ItemStack offStack = player.getOffhandItem();
        if (hand == InteractionHand.MAIN_HAND && offStack.getItem() instanceof Scroll) {
            if (!level.isClientSide) {
                absorbScroll(player, mainStack, offStack);
            }
            return InteractionResultHolder.sidedSuccess(mainStack, level.isClientSide());
        }

        return super.use(level, player, hand);
    }

    private void absorbScroll(Player player, ItemStack book, ItemStack scrollStack) {
        ISpellContainer container = ISpellContainer.get(book);
        ISpellContainer scrollContainer = ISpellContainer.get(scrollStack);

        if (container != null && scrollContainer != null && !scrollContainer.isEmpty()) {
            SpellData spellData = scrollContainer.getSpellAtIndex(0);
            if (spellData == null || spellData.getSpell() == null) return;

            // 重複チェック
            for (int j = 0; j < container.getMaxSpellCount(); j++) {
                SpellData existing = container.getSpellAtIndex(j);
                if (existing != null && existing.getSpell() != null && existing.getSpell().equals(spellData.getSpell())) {
                    player.displayClientMessage(Component.literal("▷ その物語は既に綴られている...").withStyle(ChatFormatting.YELLOW), true);
                    return;
                }
            }

            ISpellContainerMutable mutable = container.mutableCopy();
            mutable.setMaxSpellCount(100);

            for (int i = 0; i < mutable.getMaxSpellCount(); i++) {
                SpellData current = mutable.getSpellAtIndex(i);
                if (current == null || current.getSpell() == null || current.equals(SpellData.EMPTY)) {
                    mutable.addSpellAtIndex(spellData.getSpell(), spellData.getLevel(), i, true);
                    ISpellContainer.set(book, mutable.toImmutable());

                    player.displayClientMessage(Component.literal("▷ プリミティブ・バイティング: ")
                            .append(spellData.getSpell().getDisplayName(player))
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);

                    scrollStack.shrink(1);
                    return;
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.literal("【 古の龍が綴る、終わりの物語 】").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        lines.add(Component.literal("「その本は、全ての物語を食らい尽くす」").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));

        lines.add(Component.literal(" "));

        // 追加：金床での合成に関する説明
        lines.add(Component.literal("▷ 原始の収束: 100のスロットに無限の魔導を封印する").withStyle(ChatFormatting.GOLD));
        lines.add(Component.literal("▷ 秘術の金床でスクロールと合成することで記録可能").withStyle(ChatFormatting.AQUA));

        super.appendHoverText(itemStack, level, lines, flag);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
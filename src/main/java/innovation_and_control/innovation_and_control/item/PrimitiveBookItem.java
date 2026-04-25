package innovation_and_control.innovation_and_control.item;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrimitiveBookItem extends SpellBook {

    private static final int MAX_SLOTS = 1000;  // ほぼ無限に対応

    public PrimitiveBookItem(Properties properties) {
        super(MAX_SLOTS, properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotIndex, boolean isSelected) {
        if (!level.isClientSide && level.getGameTime() % 20 == 0 && entity instanceof Player) {
            syncCuriosSpellBooks(stack, (Player) entity);
        }
        super.inventoryTick(stack, level, entity, slotIndex, isSelected);
    }

    /**
     * Scans Curios slots for other SpellBooks and copies their spells into this book.
     * Spells from unequipped books are retained for persistent access.
     */
    private void syncCuriosSpellBooks(ItemStack mainBook, Player player) {
        if (!canAcceptSpells(mainBook)) return;

        ISpellContainer mainContainer = ISpellContainer.get(mainBook);
        if (mainContainer == null) return;

        ISpellContainerMutable mutable = mainContainer.mutableCopy();
        Set<Object> currentSpells = new HashSet<>();
        AtomicBoolean changed = new AtomicBoolean(false);

        // Collect all spells from equipped Curios slots
        try {
            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> 
                inventory.getCurios().forEach((slotId, handler) -> {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack equippedStack = handler.getStacks().getStackInSlot(i);

                        if (equippedStack.isEmpty()) continue;

                        if (equippedStack.getItem() instanceof SpellBook && equippedStack != mainBook) {
                            ISpellContainer otherContainer = ISpellContainer.get(equippedStack);
                            if (otherContainer != null) {
                                for (int spellSlot = 0; spellSlot < otherContainer.getMaxSpellCount(); spellSlot++) {
                                    SpellData otherSpell = otherContainer.getSpellAtIndex(spellSlot);
                                    if (otherSpell == null || otherSpell.getSpell() == null) continue;

                                    // Track this spell as currently equipped
                                    currentSpells.add(otherSpell.getSpell());

                                    // Add spell if not already in this container
                                    if (!containsSpellInMutable(mutable, otherSpell)) {
                                        int nextIndex = mutable.getNextAvailableIndex();
                                        if (nextIndex != -1 && nextIndex < MAX_SLOTS) {
                                            mutable.addSpellAtIndex(otherSpell.getSpell(), otherSpell.getLevel(), nextIndex, false);
                                            changed.set(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
            );
        } catch (Exception e) {
            // Fallback if Curios API fails
        }

        // Optional: Remove spells that are no longer in Curios slots
        // Uncomment the following lines to enable automatic removal:
        /*
        for (int i = 0; i < mutable.getMaxSpellCount(); i++) {
            SpellData spellData = mutable.getSpellAtIndex(i);
            if (spellData != null && spellData.getSpell() != null) {
                if (!currentSpells.contains(spellData.getSpell())) {
                    mutable.removeSpell(i);
                    changed.set(true);
                }
            }
        }
        */

        if (changed.get()) {
            ISpellContainer newContainer = mutable.toImmutable();
            ISpellContainer.set(mainBook, newContainer);
        }
    }

    private boolean containsSpellInMutable(ISpellContainerMutable container, SpellData data) {
        if (data == null || data.getSpell() == null) return false;

        for (int i = 0; i < container.getMaxSpellCount(); i++) {
            SpellData spellData = container.getSpellAtIndex(i);
            if (spellData != null && spellData.getSpell().equals(data.getSpell())) {
                return true;
            }
        }
        return false;
    }

    private boolean canAcceptSpells(ItemStack stack) {
        ISpellContainer container = ISpellContainer.get(stack);
        return container != null && container.getMaxSpellCount() > 0;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
        // Main description
        lines.add(Component.translatable("item.innovation_and_control.primitive_book.desc_1")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        lines.add(Component.translatable("item.innovation_and_control.primitive_book.desc_2")
                .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
        lines.add(Component.literal(" "));
        lines.add(Component.translatable("item.innovation_and_control.primitive_book.ability_sync")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.add(Component.literal(" "));

        ISpellContainer container = ISpellContainer.get(itemStack);
        if (container != null && container.getMaxSpellCount() > 0) {
            // Spell count display with translation support
            int count = container.getActiveSpellCount();
            Component countText = Component.translatable(
                "item.innovation_and_control.primitive_book.spell_count",
                count,
                MAX_SLOTS
            ).withStyle(ChatFormatting.GOLD);
            lines.add(countText);

            int shownSpells = 0;
            for (int i = 0; i < container.getMaxSpellCount() && shownSpells < 5; i++) {
                SpellData spell = container.getSpellAtIndex(i);
                if (spell != null && spell.getSpell() != null) {
                    Component spellName = Component.nullToEmpty(spell.getSpell().getSpellName());
                    lines.add(Component.literal("  §7- ")
                            .append(spellName)
                            .append(Component.literal(" Lv." + spell.getLevel()))
                            .withStyle(ChatFormatting.GRAY));
                    shownSpells++;
                }
            }
            if (container.getActiveSpellCount() > 5) {
                Component moreSpells = Component.translatable(
                    "item.innovation_and_control.primitive_book.more_spells",
                    (container.getActiveSpellCount() - 5)
                ).withStyle(ChatFormatting.GRAY);
                lines.add(Component.literal("  §7").append(moreSpells));
            }
        }

        super.appendHoverText(itemStack, level, lines, flag);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}
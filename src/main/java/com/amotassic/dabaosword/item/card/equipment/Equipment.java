package com.amotassic.dabaosword.item.card.equipment;

import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.item.card.CardItem;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.ChatFormatting.BOLD;

public class Equipment extends CardItem implements ISkill {
    public Equipment(Properties settings) {super(settings);
        TrinketsApi.registerTrinket(this, this);
    }

    public final int getType() {return Card.EQUIPMENT;}

    public final boolean lockOn() {return true;}

    @Override
    public void inventoryTick(@NonNull ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (!world.isClientSide() && equipped(stack)) setEquipped(stack, false);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context, @NonNull TooltipDisplay displayComponent, @NonNull Consumer<Component> textConsumer, @NonNull TooltipFlag type) {
        List<Component> tooltip = new ArrayList<>();
        addSRTip(c(stack), tooltip); addTip(s(stack), tooltip);

        if (hasShiftDown()) {
            tooltip.add(Component.translatable("equipment.tip1").withStyle(BOLD));
            tooltip.add(Component.translatable("equipment.tip2").withStyle(BOLD));
        } else tooltip.add(Component.translatable("dabaosword.shift_tip", Component.keybind("key.sneak")));
        tooltip.forEach(textConsumer);
    }
    public void addTip(Skill skill, List<Component> tooltip) {}
    /**防止重写错方法*/
    public final void addTip(ItemStack stack, List<Component> tooltip) {}

    @Override
    public final boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof Player player && !player.isCreative()) return false;
        return ISkill.super.canUnequip(stack, slot, entity);
    }

    public final void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ISkill.super.tick(stack, slot, entity);
    }

    @Override
    public @NonNull InteractionResult use(Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        if (!world.isClientSide()) {
            onUse(user, stack, hand, user);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public final void effect(LivingEntity user, ItemStack card, LivingEntity target) {
        useOrReplaceEquip(target, card);
    }

    public static void useOrReplaceEquip(LivingEntity user, ItemStack stack) {
        final SlotReference[] firstSlot = {null};
        TrinketsApi.getTrinketComponent(user).ifPresent(comp -> {
            comp.getInventory().values().forEach(group ->
                    group.values().forEach(inv -> {
                                for (int i = 0; i < inv.getContainerSize(); i++) {
                                    ItemStack s = inv.getItem(i);
                                    SlotReference ref = new SlotReference(inv, i);
                                    if (TrinketSlot.canInsert(stack, ref, user)) {
                                        if (s.isEmpty()) { //如果这个槽位没有物品，则直接放入
                                            inv.setItem(i, stack.copy());
                                            return;
                                        } else if (firstSlot[0] == null) firstSlot[0] = ref;
                                        //记录第一个有物品的槽位（也就是说，只能替换同类槽位的第一个物品）
                                    }
                                }
                            }
                    ));

            if (firstSlot[0] != null) { //替换原有装备
                ItemStack preStack = firstSlot[0].inventory().getItem(firstSlot[0].index());
                cardDiscard(user, d().cards(preStack, preStack.getCount(), true));
                firstSlot[0].inventory().setItem(firstSlot[0].index(), stack.copy());
            }
        });
    }
}

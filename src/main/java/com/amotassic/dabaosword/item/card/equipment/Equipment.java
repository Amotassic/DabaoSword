package com.amotassic.dabaosword.item.card.equipment;

import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.api.skill.Skill;
import com.amotassic.dabaosword.item.card.CardItem;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.BOLD;

public class Equipment extends CardItem implements ISkill {
    public Equipment(Settings settings) {super(settings);
        TrinketsApi.registerTrinket(this, this);
    }

    public final int getType() {return Card.EQUIPMENT;}

    public final boolean lockOn() {return true;}

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (!world.isClient && equipped(stack)) setEquipped(stack, false);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        List<Text> tooltip = new ArrayList<>();
        addSRTip(c(stack), tooltip); addTip(s(stack), tooltip);

        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("equipment.tip1").formatted(BOLD));
            tooltip.add(Text.translatable("equipment.tip2").formatted(BOLD));
        } else tooltip.add(Text.translatable("dabaosword.shift_tip", Text.keybind("key.sneak")));
        tooltip.forEach(textConsumer);
    }
    public void addTip(Skill skill, List<Text> tooltip) {}
    /**防止重写错方法*/
    public final void addTip(ItemStack stack, List<Text> tooltip) {}

    @Override
    public final boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof PlayerEntity player && !player.isCreative()) return false;
        return ISkill.super.canUnequip(stack, slot, entity);
    }

    public final void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ISkill.super.tick(stack, slot, entity);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            onUse(user, stack, hand, user);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
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
                                for (int i = 0; i < inv.size(); i++) {
                                    ItemStack s = inv.getStack(i);
                                    SlotReference ref = new SlotReference(inv, i);
                                    if (TrinketSlot.canInsert(stack, ref, user)) {
                                        if (s.isEmpty()) { //如果这个槽位没有物品，则直接放入
                                            inv.setStack(i, stack.copy());
                                            return;
                                        } else if (firstSlot[0] == null) firstSlot[0] = ref;
                                        //记录第一个有物品的槽位（也就是说，只能替换同类槽位的第一个物品）
                                    }
                                }
                            }
                    ));

            if (firstSlot[0] != null) { //替换原有装备
                ItemStack preStack = firstSlot[0].inventory().getStack(firstSlot[0].index());
                cardDiscard(user, d().cards(preStack, preStack.getCount(), true));
                firstSlot[0].inventory().setStack(firstSlot[0].index(), stack.copy());
            }
        });
    }
}

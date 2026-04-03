package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.api.skill.Skill;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.*;

public class SkillItem extends Item implements ISkill {
    public SkillItem(Properties settings) {super(settings);
        TrinketsApi.registerTrinket(this, this);
    }

    @Override
    public void inventoryTick(@NonNull ItemStack stack, ServerLevel world, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        if (!world.isClientSide() && equipped(stack)) setEquipped(stack, false);
    }

    @Override @SuppressWarnings("deprecation")
    public void appendHoverText(@NonNull ItemStack s, @NonNull TooltipContext c, @NonNull TooltipDisplay display, @NonNull Consumer<Component> tc, @NonNull TooltipFlag tooltipFlag) {
        List<Component> t = new ArrayList<>();
        addTip(s(s), t);
        t.forEach(tc);
    }
    public void addTip(Skill skill, List<Component> tooltip) {}
    public MutableComponent getTip(ChatFormatting... format) {return getTip("", format);}
    public MutableComponent getTip(String suffix, ChatFormatting... format) {
        return Component.translatable(getDescriptionId().replace("skill.", "") + ".tooltip" + suffix).withStyle(format);
    }

    public final void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ISkill.super.tick(stack, slot, entity);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide() && user.entityTags().contains("change_skill") && hand == InteractionHand.OFF_HAND && user.isShiftKeyDown()) {
            ItemStack stack = user.getOffhandItem();
            if (stack.getItem() instanceof SkillItem) {
                stack.setCount(0);
                changeSkill(user);
                user.entityTags().remove("change_skill");
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        ItemStack stack = user.getItemInHand(hand);
        if (TrinketItem.equipItem(user, stack)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    public static void changeSkill(Player player) {
        ItemStack stack = customLoot(player, "draw_skill");
        if (!stack.isEmpty()) voice(player, "giftbox",3);
        give(player, stack);
    }

    public Component activeSkillText(Player user, Skill skill) {
        return Component.translatable("active_skill.select_target").withStyle(ChatFormatting.AQUA).withStyle(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/dabaosword " + user.getName().getString() + " " + BuiltInRegistries.ITEM.getId(skill.stack.getItem()) + " ")));
    }
}

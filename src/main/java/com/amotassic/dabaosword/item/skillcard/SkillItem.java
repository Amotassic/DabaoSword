package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.api.skill.ISkill;
import com.amotassic.dabaosword.api.skill.Skill;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.amotassic.dabaosword.util.ModTools.*;

public class SkillItem extends Item implements ISkill {
    public SkillItem(Settings settings) {super(settings);
        TrinketsApi.registerTrinket(this, this);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (!world.isClient && equipped(stack)) setEquipped(stack, false);
    }

    @Override @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack s, TooltipContext c, TooltipDisplayComponent d, Consumer<Text> tc, TooltipType type) {
        List<Text> t = new ArrayList<>();
        addTip(s(s), t);
        t.forEach(tc);
    }
    public void addTip(Skill skill, List<Text> tooltip) {}
    public MutableText getTip(Formatting... format) {return getTip("", format);}
    public MutableText getTip(String suffix, Formatting... format) {
        return Text.translatable(getTranslationKey() + ".tooltip" + suffix).formatted(format);
    }

    public final void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        ISkill.super.tick(stack, slot, entity);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient && user.getCommandTags().contains("change_skill") && hand == Hand.OFF_HAND && user.isSneaking()) {
            ItemStack stack = user.getOffHandStack();
            if (stack.getItem() instanceof SkillItem) {
                stack.setCount(0);
                changeSkill(user);
                user.getCommandTags().remove("change_skill");
                return ActionResult.SUCCESS_SERVER;
            }
        }
        ItemStack stack = user.getStackInHand(hand);
        if (TrinketItem.equipItem(user, stack)) {
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    public static void changeSkill(PlayerEntity player) {
        ItemStack stack = customLoot(player, "draw_skill");
        if (!stack.isEmpty()) voice(player, "giftbox",3);
        give(player, stack);
    }

    public Text activeSkillText(PlayerEntity user, Skill skill) {
        return Text.translatable("active_skill.select_target").formatted(Formatting.AQUA).styled(style -> style.withClickEvent(new ClickEvent.SuggestCommand("/dabaosword " + user.getName().getString() + " " + Registries.ITEM.getId(skill.stack.getItem()) + " ")));
    }
}

package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.api.Skill;
import com.amotassic.dabaosword.util.Sounds;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Predicate;

import static com.amotassic.dabaosword.util.ModTools.*;

public class SkillItem extends AccessoryItem implements Skill {
    public SkillItem(Settings settings) {super(settings);}

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        LivingEntity entity = reference.entity();
        if (entity.getWorld() instanceof ServerWorld world && !equipped(stack)) {
            world.getPlayers().forEach(player -> player.sendMessage(
                    Text.translatable("dabaosword.entity.equip", entity.getDisplayName(), stack.toHoverableText())
            ));
            setEquipped(stack, true);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && equipped(stack)) setEquipped(stack, false);
    }

    public static boolean equipped(ItemStack stack) {return getOrCreateNbt(stack).contains("equipped");}

    public static void setEquipped(ItemStack stack, boolean equipped) {
        NbtCompound nbt = getOrCreateNbt(stack);
        if (equipped) nbt.putBoolean("equipped", true);
        else nbt.remove("equipped");
        setNbt(stack, nbt);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient && user.getCommandTags().contains("change_skill") && hand == Hand.OFF_HAND && user.isSneaking()) {
            ItemStack stack = user.getStackInHand(hand);
            if (stack.getItem() instanceof SkillItem) {
                stack.setCount(0);
                changeSkill(user);
                user.getCommandTags().remove("change_skill");
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void tick(ItemStack stack, SlotReference reference) {
        LivingEntity entity = reference.entity();
        if (entity.getWorld() instanceof ServerWorld world) {
            int cd = getCD(stack); //世界时间除以20取余为0时，技能内置CD减一秒
            if (cd > 0 && world.getTime() % 20 == 0) setCD(stack, cd - 1);
        }
    }

    public static void changeSkill(PlayerEntity player) {
        var selectedId = parseLootTable(Identifier.of("dabaosword", "loot_tables/draw_skill.json"));
        ItemStack stack = new ItemStack(Registries.ITEM.get(selectedId));
        if (stack.getItem() != Items.AIR) voice(player, Sounds.GIFTBOX,3);
        give(player, stack);
    }

    /**转化卡牌技能通用方法*/
    public static void viewAs(LivingEntity entity, ItemStack skill, int CD, Predicate<ItemStack> predicate, ItemStack result) {
        if (!entity.getWorld().isClient && noTieji(entity) && getCD(skill) == 0) {
            ItemStack stack = entity.getOffHandStack();
            if (predicate.test(stack)) {
                setCD(skill, CD);
                stack.decrement(1);
                give(entity, result);
                voice(entity, skill);
            }
        }
    }

    public static class ActiveSkill extends SkillItem {
        public ActiveSkill(Settings settings) {super(settings);}
    }

    public static class ActiveSkillWithTarget extends SkillItem {
        public ActiveSkillWithTarget(Settings settings) {super(settings);}
    }
}

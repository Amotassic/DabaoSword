package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;

import static com.amotassic.dabaosword.util.ModTools.*;

public class HuojiSkill extends SkillItem {
    public HuojiSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getOffHandStack();
            int cd = getCD(stack);
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.BASIC_CARD)) {
                setCD(stack, 15);
                viewAs(player, Tags.Items.BASIC_CARD, ModItems.FIRE_ATTACK, Sounds.HUOJI);
            }
        }
        super.tick(stack, slot, entity);
    }

    public static void viewAs(PlayerEntity player, TagKey<Item> tag, Item item, SoundEvent sound) {
        ItemStack stack = player.getOffHandStack();
        if (!stack.isEmpty() && stack.isIn(tag)) {
            stack.decrement(1);
            give(player, item.getDefaultStack());
            voice(player, sound);
        }
    }
}

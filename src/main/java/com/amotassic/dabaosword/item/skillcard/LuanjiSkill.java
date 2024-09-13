package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static com.amotassic.dabaosword.util.ModTools.*;

public class LuanjiSkill extends SkillItem{
    public LuanjiSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player && noTieji(entity)) {
            ItemStack stack1 = player.getOffHandStack();
            int cd = getCD(stack);
            if (cd == 0 && !stack1.isEmpty() && stack1.isIn(Tags.Items.CARD) && stack1.getCount() > 1) {
                setCD(stack, 15);
                stack1.decrement(2);
                give(player, ModItems.WANJIAN.getDefaultStack());
                voice(player, Sounds.LUANJI);
            }
        }
        super.tick(stack, slot, entity);
    }
}

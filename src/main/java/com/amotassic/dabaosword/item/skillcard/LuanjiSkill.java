package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Random;

import static com.amotassic.dabaosword.util.ModTools.*;

public class LuanjiSkill extends SkillItem {
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
                float i = new Random().nextFloat();
                if (i < 0.25) {voice(player, Sounds.LUANJI1);
                } else if (0.25 <= i && i < 0.5) {voice(player, Sounds.LUANJI2);
                } else if (0.5 <= i && i < 0.75) {voice(player, Sounds.LUANJI3);
                } else {voice(player, Sounds.LUANJI4);}
            }
        }
        super.tick(stack, slot, entity);
    }
}

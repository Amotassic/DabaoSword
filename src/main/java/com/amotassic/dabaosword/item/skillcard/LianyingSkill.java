package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.Random;

import static com.amotassic.dabaosword.item.card.GainCardItem.draw;
import static com.amotassic.dabaosword.util.ModTools.*;

public class LianyingSkill extends SkillItem {
    public LianyingSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player) {
            int cd = getCD(stack);
            if (world.getTime() % 20 == 0 && cd == 1) { //确保一秒内只触发一次
                draw(player, 1);
                if (new Random().nextFloat() < 0.5) {voice(player, Sounds.LIANYING1);} else {voice(player, Sounds.LIANYING2);}
            }
        }
        super.tick(stack, slot, entity);
    }
}

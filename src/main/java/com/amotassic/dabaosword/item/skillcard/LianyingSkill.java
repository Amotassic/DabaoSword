package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import static com.amotassic.dabaosword.util.ModTools.*;

public class LianyingSkill extends SkillItem {
    public LianyingSkill(Settings settings) {super(settings);}

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world && entity instanceof PlayerEntity player) {
            int cd = getCD(stack);
            if (world.getTime() % 20 == 0 && cd == 1) { //确保一秒内只触发一次
                draw(player);
                voice(player, Sounds.LIANYING);
            }
        }
        super.tick(stack, slot, entity);
    }
}

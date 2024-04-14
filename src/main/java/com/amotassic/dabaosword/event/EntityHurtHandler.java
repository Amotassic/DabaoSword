package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;

import java.util.Random;

public class EntityHurtHandler implements EntityHurtCallback, ModTools {
    NbtCompound quanji = new NbtCompound();

    @Override
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        if (!entity.getWorld().isClient) {
            if (entity instanceof PlayerEntity player && hasItem(player, SkillCards.QUANJI)) {
                int i = player.getInventory().getSlotWithStack(SkillCards.QUANJI.getDefaultStack());
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getNbt() == null) {
                    quanji.putInt("quanji",0);
                    stack.setNbt(quanji);
                } else {
                    int quan = quanji.getInt("quanji");
                    quan++; quanji.putInt("quanji", quan); stack.setNbt(quanji);
                }
                if (new Random().nextFloat() < 0.5) {
                    voice(player, Sounds.QUANJI1);
                } else {voice(player, Sounds.QUANJI2);}
            }

            if (source.getAttacker() instanceof PlayerEntity player && hasItem(player, SkillCards.QUANJI)) {
                int i = player.getInventory().getSlotWithStack(SkillCards.QUANJI.getDefaultStack());
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.getNbt() != null) {int quan = stack.getNbt().getInt("quanji");
                    if (quan > 0) {
                        entity.damage(player.getWorld().getDamageSources().playerAttack(player), quan);
                        int quan1 = quan/2;
                        quanji.putInt("quanji", quan1); stack.setNbt(quanji);
                    }
                }
                float j = new Random().nextFloat();
                if (j < 0.25) {voice(player, Sounds.PAIYI1);
                } else if (0.25 <= j && j < 0.5) {voice(player, Sounds.PAIYI2);
                } else if (0.5 <= j && j < 0.75) {voice(player, Sounds.PAIYI3);
                } else {voice(player, Sounds.PAIYI4);}
            }
        }
        return null;
    }
}

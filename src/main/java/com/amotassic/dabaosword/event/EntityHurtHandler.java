package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import java.util.Random;

public class EntityHurtHandler implements EntityHurtCallback, ModTools {
    TagKey<Item> tag = Tags.Items.QUANJI;
    NbtCompound quanji = new NbtCompound();

    @Override
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        if (entity.getWorld() instanceof ServerWorld world) {

            if (entity instanceof PlayerEntity player) {
                if (hasItemInTag(tag, player) && source.getAttacker() instanceof LivingEntity) {//权计技能：受到生物伤获得权
                    ItemStack stack = stackInTag(tag, player);
                    if (stack.getNbt() == null) {
                        quanji.putInt("quanji",1);
                        stack.setNbt(quanji);
                    } else {
                        int quan = stack.getNbt().getInt("quanji");
                        quan++; quanji.putInt("quanji", quan); stack.setNbt(quanji);
                    }
                    if (new Random().nextFloat() < 0.5) {
                        voice(player, Sounds.QUANJI1);
                    } else {voice(player, Sounds.QUANJI2);}
                }
            }
        }
        return ActionResult.PASS;
    }
}

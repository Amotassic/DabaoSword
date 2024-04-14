package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AttackEntityHandler implements ModTools, AttackEntityCallback {
    //监听事件：若玩家攻击玩家或敌对生物，有概率摸牌
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world instanceof ServerWorld && !player.isSpectator()) {
            if (entity instanceof HostileEntity) {
                if (new Random().nextFloat() < 0.05) player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));
            }
            if (entity instanceof PlayerEntity) {
                if (new Random().nextFloat() < 0.05) player.giveItemStack(new ItemStack(ModItems.WUZHONG));
            }

            if (entity instanceof LivingEntity && hasItem(player, SkillCards.QUANJI)) {
                player.giveItemStack(ModItems.PEACH.getDefaultStack());
                float j = new Random().nextFloat();
                if (j < 0.25) {voice(player, Sounds.PAIYI1);
                } else if (0.25 <= j && j < 0.5) {voice(player, Sounds.PAIYI2);
                } else if (0.5 <= j && j < 0.75) {voice(player, Sounds.PAIYI3);
                } else {voice(player, Sounds.PAIYI4);}
                NbtCompound quanji = new NbtCompound();
                ItemStack stack = stackWith(SkillCards.QUANJI, player);
                if (stack.getNbt() != null && stack.getNbt().contains("quanji")) {
                    int quan = stack.getNbt().getInt("quanji");
                    if (quan > 0) {
                        entity.damage(world.getDamageSources().playerAttack(player), quan);
                        int quan1 = quan / 2;
                        quanji.putInt("quanji", quan1);
                        stack.setNbt(quanji);
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}

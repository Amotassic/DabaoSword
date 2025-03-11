package com.amotassic.dabaosword.item.card;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.api.event.CardEvents.*;
import static com.amotassic.dabaosword.util.ModTools.*;

public class JuedouItem extends CardItem {
    public JuedouItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && entity.isAlive()) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        user.addCommandTag("juedou"); entity.addCommandTag("juedou"); //防止决斗触发杀
        if (user instanceof PlayerEntity player && entity instanceof PlayerEntity target) {
            int playerSha = countCard(player, isSha);
            int targetSha = countCard(target, isSha);
            if (playerSha >= targetSha) {
                juedou(player, target);
                target.sendMessage(Text.translatable("dabaosword.juedou2", player.getDisplayName()), false);
            } else {
                juedou(target, player);
                player.sendMessage(Text.translatable("dabaosword.juedou1"), false);
                //如果目标的杀比使用者的杀多，反击使用者，则目标减少一张杀
                if (targetSha != 0) cardUsePost(target, getCard(target, isSha).getRight(), player);
            }
        } else juedou(user, entity);
    }

    private void juedou(LivingEntity attacker, LivingEntity target) {
        ItemStack juedou = new ItemStack(this);
        DamageSource source = damageSource(attacker, DamageTypes.GENERIC_KILL);
        if (canHurtByCard(target, juedou)) {
            target.timeUntilRegen = 0;
            if (target.damage(world(attacker), source, 5f)) hurtByCard(target, juedou);
        }
    }
}

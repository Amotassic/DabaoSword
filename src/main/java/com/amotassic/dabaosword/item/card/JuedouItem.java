package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.damage_type.ModDT;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import static com.amotassic.dabaosword.util.ModTools.*;

public class JuedouItem extends CardItem.Armoury {
    public JuedouItem(Settings settings) {super(settings);}

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getEntityWorld().isClient() && entity.isAlive()) {
            onUse(user, user.getStackInHand(hand), hand, entity);
            return ActionResult.SUCCESS_SERVER;
        }
        return ActionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        user.addCommandTag("juedou"); entity.addCommandTag("juedou"); //防止决斗触发杀
        if (user instanceof PlayerEntity player && entity instanceof PlayerEntity target) {
            int playerSha = countCard(player, isSha);
            int targetSha = countCard(target, isSha);
            if (playerSha >= targetSha) {
                juedou(player, card, target);
                target.sendMessage(Text.translatable("dabaosword.juedou2", player.getDisplayName()), false);
            } else {
                juedou(target, card, player);
                player.sendMessage(Text.translatable("dabaosword.juedou1"), false);
                //如果目标的杀比使用者的杀多，反击使用者，则目标减少一张杀
                if (targetSha != 0) {
                    ItemStack sha = getCard(target, isSha);
                    onUse(target, sha, null, true, true);
                }
            }
        } else juedou(user, card, entity);
    }

    private void juedou(LivingEntity attacker, ItemStack card, LivingEntity target) {
        target.timeUntilRegen = 0;
        target.damage(world(attacker), ModDT.juedou(attacker), 5f);
    }

    @Override public boolean rangedUse() {return true;}

    @Override public boolean askForWuxie() {return true;}
}

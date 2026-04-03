package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.damage_type.ModDT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import static com.amotassic.dabaosword.util.ModTools.*;

public class JuedouItem extends CardItem.Armoury {
    public JuedouItem(Properties settings) {super(settings);}

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (!user.level().isClientSide() && entity.isAlive()) {
            onUse(user, user.getItemInHand(hand), hand, entity);
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void effect(LivingEntity user, ItemStack card, LivingEntity entity) {
        user.addTag("juedou"); entity.addTag("juedou"); //防止决斗触发杀
        if (user instanceof Player player && entity instanceof Player target) {
            int playerSha = countCard(player, isSha);
            int targetSha = countCard(target, isSha);
            if (playerSha >= targetSha) {
                juedou(player, card, target);
                target.sendSystemMessage(Component.translatable("dabaosword.juedou2", player.getDisplayName()));
            } else {
                juedou(target, card, player);
                player.sendSystemMessage(Component.translatable("dabaosword.juedou1"));
                //如果目标的杀比使用者的杀多，反击使用者，则目标减少一张杀
                if (targetSha != 0) {
                    ItemStack sha = getCard(target, isSha);
                    onUse(target, sha, null, true, true);
                }
            }
        } else juedou(user, card, entity);
    }

    private void juedou(LivingEntity attacker, ItemStack card, LivingEntity target) {
        target.invulnerableTime = 0;
        target.hurtServer(world(attacker), ModDT.juedou(attacker), 5f);
    }

    @Override public boolean rangedUse() {return true;}

    @Override public boolean askForWuxie() {return true;}
}

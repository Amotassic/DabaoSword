package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.*;

public class JuedouItem extends CardItem {
    public JuedouItem(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && selected && entity instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,114,false,false,false));
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && entity.isAlive()) {
            if (cardUsePre(user, user.getMainHandStack(), entity)) return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity entity) {
        user.addCommandTag("juedou");
        if (user instanceof PlayerEntity player && entity instanceof PlayerEntity target) {
            int playerSha = countCard(player, isSha);
            int targetSha = countCard(target, isSha);
            if (playerSha >= targetSha) {
                target.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
                target.timeUntilRegen = 0;
                target.damage(player.getDamageSources().sonicBoom(player),5f);
                target.sendMessage(Text.literal(player.getEntityName()).append(Text.translatable("dabaosword.juedou2")));
            } else {
                target.addCommandTag("juedou"); //防止决斗触发杀、闪
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2,2,0,false,false,false));
                player.timeUntilRegen = 0;
                player.damage(player.getDamageSources().sonicBoom(target),5f);
                player.sendMessage(Text.translatable("dabaosword.juedou1"));
                if (targetSha != 0) { //如果目标的杀比使用者的杀多，反击使用者，则目标减少一张杀
                    cardUsePost(target, getCard(target, isSha).getRight(), player);
                }
            }
        } else {
            entity.addCommandTag("juedou");
            entity.timeUntilRegen = 0;
            entity.damage(user.getDamageSources().sonicBoom(user),5f);
        }
        voice(user, Sounds.JUEDOU);
        super.cardUse(user, stack, entity);
    }
}

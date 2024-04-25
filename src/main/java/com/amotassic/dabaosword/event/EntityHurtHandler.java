package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Box;

import java.util.Random;

public class EntityHurtHandler implements EntityHurtCallback, ModTools {
    NbtCompound quanji = new NbtCompound();

    @Override
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        if (entity.getWorld() instanceof ServerWorld world) {
            //监听事件：若玩家杀死敌对生物，有概率摸牌，若杀死玩家，摸两张牌
            if (source.getAttacker() instanceof PlayerEntity player && entity.getHealth() <= 0) {
                if (entity instanceof HostileEntity) {
                    if (new Random().nextFloat() < 0.1) {
                        player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));
                        player.sendMessage(Text.translatable("dabaosword.draw.monster"),true);
                    }
                }
                if (entity instanceof PlayerEntity) {
                    player.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                    player.sendMessage(Text.translatable("dabaosword.draw.player"),true);
                }
            }

            if (source.getAttacker() instanceof PlayerEntity player) {
                //狂骨：攻击命中敌人时，如果受伤超过5则回血，否则摸一张牌
                if (hasTrinket(SkillCards.KUANGGU, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    if (player.getMaxHealth()-player.getHealth()>=5) {player.heal(5);}
                    else {player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));}
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.KUANGGU1);} else {voice(player, Sounds.KUANGGU2);}
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,true,true));
                }
            }

            if (entity instanceof PlayerEntity player) {
                //权计技能：受到生物伤害获得权
                if (hasTrinket(SkillCards.QUANJI, player) && source.getAttacker() instanceof LivingEntity) {
                    ItemStack stack = trinketItem(SkillCards.QUANJI, player);
                    if (stack.getNbt() == null) {
                        quanji.putInt("quanji",1);
                        stack.setNbt(quanji);
                    } else {
                        int quan = stack.getNbt().getInt("quanji");
                        quan++; quanji.putInt("quanji", quan); stack.setNbt(quanji);
                    }
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.QUANJI1);} else {voice(player, Sounds.QUANJI2);}
                }
                //遗计
                if (hasTrinket(SkillCards.YIJI, player) && !player.hasStatusEffect(ModItems.COOLDOWN) && player.getHealth() <= 12) {
                    player.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 20, 0, false, true, true));
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.YIJI1);} else {voice(player, Sounds.YIJI2);}
                }
                //流离
                if (hasTrinket(SkillCards.LIULI, player) && source.getAttacker() instanceof LivingEntity attacker && hasItemInTag(Tags.Items.CARD, player)) {
                    ItemStack stack = stackInTag(Tags.Items.CARD, player);
                    Box box = new Box(player.getBlockPos()).expand(5);
                    for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity -> LivingEntity != attacker && LivingEntity != player)) {
                        if (nearbyEntity != null) {
                            player.heal(amount);
                            stack.decrement(1);
                            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.LIULI1);} else {voice(player, Sounds.LIULI2);}
                            nearbyEntity.damage(source, amount);break;
                        }
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
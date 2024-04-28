package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
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

            if (source.getSource() instanceof PlayerEntity player) {

                ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
                ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
                ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
                ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
                boolean noArmor = head.isEmpty() && chest.isEmpty() && legs.isEmpty() && feet.isEmpty();
                //古锭刀对没有装备的生物伤害加50%
                if (player.getMainHandStack().getItem() == ModItems.GUDINGDAO && !player.getCommandTags().contains("guding") && !hasTrinket(SkillCards.QUANJI, player)) {
                    if (noArmor || hasTrinket(SkillCards.POJUN, player)) {
                        player.addCommandTag("guding");
                        entity.damage(source, 1.5f * amount);
                        player.getCommandTags().remove("guding");
                    }
                }

                //排异技能：攻击伤害增加
                if (hasTrinket(SkillCards.QUANJI, player) && !player.getCommandTags().contains("quanji")) {
                    ItemStack stack = trinketItem(SkillCards.QUANJI, player);
                    if (stack.getNbt() != null) {
                        int quan = stack.getNbt().getInt("quanji");
                        if (quan > 0) {
                            player.addCommandTag("quanji");
                            //处理古锭刀伤害
                            if (player.getMainHandStack().getItem() == ModItems.GUDINGDAO && noArmor) {
                                entity.damage(source, quan + 1.5f * amount);
                            } else {entity.damage(source, quan + amount);}
                            player.getCommandTags().remove("quanji");
                            if (quan > 4 && entity instanceof PlayerEntity) {
                                ((PlayerEntity) entity).giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                            }
                            int quan1 = quan/2;
                            quanji.putInt("quanji", quan1); stack.setNbt(quanji);
                            float j = new Random().nextFloat();
                            if (j < 0.25) {voice(player, Sounds.PAIYI1);
                            } else if (0.25 <= j && j < 0.5) {voice(player, Sounds.PAIYI2,3);
                            } else if (0.5 <= j && j < 0.75) {voice(player, Sounds.PAIYI3);
                            } else {voice(player, Sounds.PAIYI4,3);}
                        }
                    }
                }

                //杀的相关结算
                if (getShaSlot(player) != -1 && !player.getCommandTags().contains("sha")) {
                    ItemStack stack = shaStack(player);
                    if (entity instanceof PlayerEntity target && hasItem(target, ModItems.SHAN)) {
                        voice(target, Sounds.SHAN);
                        if (stack.getItem() == ModItems.SHA) voice(player, Sounds.SHA);
                        if (stack.getItem() == ModItems.FIRE_SHA) voice(player, Sounds.SHA_FIRE);
                        if (stack.getItem() == ModItems.THUNDER_SHA) voice(player, Sounds.SHA_THUNDER);
                        if (!player.isCreative()) {stack.decrement(1);}
                        removeItem(target, ModItems.SHAN);
                    } else {
                        player.addCommandTag("sha");
                        if (stack.getItem() == ModItems.SHA) {
                            voice(player, Sounds.SHA);
                            entity.damage(source, 5 + amount);
                        }
                        if (stack.getItem() == ModItems.FIRE_SHA) {
                            voice(player, Sounds.SHA_FIRE);
                            entity.damage(player.getDamageSources().inFire(), 5 + amount);
                            entity.setOnFireFor(5);
                        }
                        if (stack.getItem() == ModItems.THUNDER_SHA) {
                            voice(player, Sounds.SHA_THUNDER);
                            entity.damage(player.getDamageSources().lightningBolt(),amount);
//                            EntityType.LIGHTNING_BOLT.spawn(world, new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ()),null);
                            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200,0,false,false,false));
                        }
                        player.getCommandTags().remove("sha");
                        if (!player.isCreative()) {stack.decrement(1);}
                    }
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

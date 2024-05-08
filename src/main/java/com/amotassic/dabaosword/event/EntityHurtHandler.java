package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.Random;

public class EntityHurtHandler implements EntityHurtCallback, ModTools {
    NbtCompound quanji = new NbtCompound();

    @Override
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        boolean noArmor = head.isEmpty() && chest.isEmpty() && legs.isEmpty() && feet.isEmpty();
        boolean armor2 = chest.getItem() == ModItems.RATTAN_CHESTPLATE;
        boolean armor3 = legs.getItem() == ModItems.RATTAN_LEGGINGS;
        boolean inrattan = armor2 || armor3;

        if (entity.getWorld() instanceof ServerWorld world) {

            if (entity instanceof PlayerEntity player && player.isDead() && hasItemInTag(Tags.Items.RECOVER, player)) {
                //濒死自动使用酒、桃结算：首先计算需要回复的体力为(受到的伤害amount - 玩家当前生命值）
                float recover = amount - player.getHealth();
                int tao = count(player, Tags.Items.RECOVER);//数玩家背包中回血卡牌的数量（只包含酒、桃）
                if (5 * tao > recover) {//如果剩余回血牌的回复量大于需要回复的值，则进行下一步，否则直接趋势
                    for (int i = 1; i < recover/5; i++) {//循环移除背包中的酒、桃
                        ItemStack stack = stackInTag(Tags.Items.RECOVER, player);
                        if (stack.getItem() == ModItems.PEACH) voice(player, Sounds.RECOVER);
                        if (stack.getItem() == ModItems.JIU) voice(player, Sounds.JIU);
                        stack.decrement(1);
                    }//最后将玩家的体力设置为 受伤前生命值 - 伤害值 + 回复量
                    float result = recover % 5 == 0 ? 5 * (int)(recover/5) : 5 * ((int)(recover/5) + 1);
                    player.setHealth(player.getHealth() - amount + result);
                }
            }

            //监听事件：若玩家杀死敌对生物，有概率摸牌，若杀死玩家，摸两张牌
            if (source.getAttacker() instanceof PlayerEntity player && entity.getHealth() <= 0) {
                if (entity instanceof HostileEntity) {
                    if (new Random().nextFloat() < 0.1) {
                        player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));
                        player.sendMessage(Text.translatable("dabaosword.draw.monster"),true);
                    }
                    //功獒技能触发
                    if (hasTrinket(SkillCards.GONGAO, player)) {
                        ItemStack stack = trinketItem(SkillCards.GONGAO, player);
                        NbtCompound nbt = new NbtCompound();
                        int extraHP = stack.getNbt() != null ? stack.getNbt().getInt("extraHP") : 0;
                        nbt.putInt("extraHP", extraHP + 1); stack.setNbt(nbt);
                        player.setHealth(player.getHealth() + 1);
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.GONGAO1);} else {voice(player, Sounds.GONGAO2);}
                    }
                }
                if (entity instanceof PlayerEntity) {
                    player.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                    player.sendMessage(Text.translatable("dabaosword.draw.player"),true);
                    //功獒技能触发
                    if (hasTrinket(SkillCards.GONGAO, player)) {
                        ItemStack stack = trinketItem(SkillCards.GONGAO, player);
                        NbtCompound nbt = new NbtCompound();
                        int extraHP = stack.getNbt() != null ? stack.getNbt().getInt("extraHP") : 0;
                        nbt.putInt("extraHP", extraHP + 5); stack.setNbt(nbt);
                        player.setHealth(player.getHealth() + 5);
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.GONGAO1);} else {voice(player, Sounds.GONGAO2);}
                    }
                }
            }

            //穿藤甲时，若承受火焰伤害，则 战火燃尽，嘤熊胆！
            if (source.isIn(DamageTypeTags.IS_FIRE) && inrattan && !entity.getCommandTags().contains("rattan")) {
                entity.addCommandTag("rattan");
                entity.damage(source, 2 * amount);
                entity.getCommandTags().remove("rattan");
            }

            if (source.getAttacker() instanceof PlayerEntity player) {
                //狂骨：攻击命中敌人时，如果受伤超过5则回血，否则摸一张牌
                if (hasTrinket(SkillCards.KUANGGU, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    if (player.getMaxHealth()-player.getHealth()>=5) {player.heal(5);}
                    else {player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));}
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.KUANGGU1);} else {voice(player, Sounds.KUANGGU2);}
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,true,true));
                }

                if (player.getCommandTags().contains("px")) {
                    entity.timeUntilRegen = 0;
                }
            }

            if (source.getSource() instanceof LivingEntity attacker) {

                //古锭刀对没有装备的生物伤害加50%
                if (attacker.getMainHandStack().getItem() == ModItems.GUDINGDAO && !attacker.getCommandTags().contains("guding")) {
                    if (noArmor || hasTrinket(SkillCards.POJUN, (PlayerEntity) attacker)) {
                        attacker.addCommandTag("guding");
                        entity.damage(source,1.5f * amount);
                        attacker.getCommandTags().remove("guding");
                    }
                }

                //青釭剑额外伤害
                if (attacker.getMainHandStack().getItem() == ModItems.QINGGANG && !attacker.getCommandTags().contains("guding") && !attacker.getCommandTags().contains("sha")) {
                    attacker.addCommandTag("guding");
                    float extraDamage = Math.min(20, 0.2f * entity.getMaxHealth());
                    entity.timeUntilRegen = 0;
                    entity.damage(attacker.getDamageSources().genericKill(), extraDamage);
                    attacker.getCommandTags().remove("guding");
                }
            }

            if (source.getSource() instanceof PlayerEntity player) {

                //杀的相关结算
                if (shoudSha(player)) {
                    ItemStack stack = shaStack(player);
                    player.addCommandTag("sha");
                    if (stack.getItem() == ModItems.SHA) {
                        voice(player, Sounds.SHA);
                        if (!inrattan) {entity.timeUntilRegen = 0; entity.damage(source,5);}
                    }
                    if (stack.getItem() == ModItems.FIRE_SHA) {
                        voice(player, Sounds.SHA_FIRE);
                        entity.timeUntilRegen = 0; entity.setOnFireFor(5);
                    }
                    if (stack.getItem() == ModItems.THUNDER_SHA) {
                        voice(player, Sounds.SHA_THUNDER);
                        entity.timeUntilRegen = 0; entity.damage(player.getDamageSources().magic(),5);
                        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                        if (lightningEntity != null) {
                            lightningEntity.refreshPositionAfterTeleport(entity.getX(), entity.getY(), entity.getZ());
                            lightningEntity.setCosmetic(true);
                        }
                        world.spawnEntity(lightningEntity);
                    }
                    benxi(player);
                    if (!player.isCreative()) {stack.decrement(1);}
                }

                //排异技能：攻击伤害增加
                if (hasTrinket(SkillCards.QUANJI, player) && !player.getCommandTags().contains("quanji")) {
                    ItemStack stack = trinketItem(SkillCards.QUANJI, player);
                    if (stack.getNbt() != null) {
                        int quan = stack.getNbt().getInt("quanji");
                        if (quan > 0) {
                            player.addCommandTag("quanji");
                            entity.timeUntilRegen = 0; entity.damage(source, quan);
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

                //奔袭：命中后减少2手长，摸一张牌
                if (hasTrinket(SkillCards.BENXI, player) && !player.getCommandTags().contains("benxi")) {
                    ItemStack stack = trinketItem(SkillCards.BENXI, player);
                    if (stack.getNbt() != null) {
                        NbtCompound benxi = new NbtCompound();
                        int ben = stack.getNbt().getInt("benxi");
                        if (ben > 1) {
                            player.addCommandTag("benxi");
                            benxi.putInt("benxi", ben - 2); stack.setNbt(benxi);
                            player.giveItemStack(new ItemStack(ModItems.GAIN_CARD));
                            if (new Random().nextFloat() < 0.5) {voice(player, Sounds.BENXI1);} else {voice(player, Sounds.BENXI2);}
                        }
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

            }
        }
        return ActionResult.PASS;
    }

    boolean shoudSha(PlayerEntity player) {
        return getShaSlot(player) != -1 && !player.getCommandTags().contains("sha") && !player.getCommandTags().contains("juedou") && !player.getCommandTags().contains("wanjian");
    }
}

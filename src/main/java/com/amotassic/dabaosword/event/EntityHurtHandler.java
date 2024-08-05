package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.network.ServerNetworking.openInv;
import static com.amotassic.dabaosword.network.ServerNetworking.targetInv;
import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityHurtHandler implements EntityHurtCallback {

    private static void save(PlayerEntity player, float amount) {
        if (hasItemInTag(Tags.Items.RECOVER, player)) {
            //濒死自动使用酒、桃结算：首先计算需要回复的体力为(受到的伤害amount - 玩家当前生命值）
            float recover = amount - player.getHealth(); int need = (int) (recover/5) + 1;
            int tao = count(player, Tags.Items.RECOVER);//数玩家背包中回血卡牌的数量（只包含酒、桃）
            if (tao >= need) {//如果剩余回血牌大于需要的桃的数量，则进行下一步，否则直接趋势
                for (int i = 0; i < need; i++) {//循环移除背包中的酒、桃
                    ItemStack stack = stackInTag(Tags.Items.RECOVER, player);
                    if (stack.getItem() == ModItems.PEACH) voice(player, Sounds.RECOVER);
                    if (stack.getItem() == ModItems.JIU) voice(player, Sounds.JIU);
                    stack.decrement(1);
                }
                //最后将玩家的体力设置为 受伤前生命值 - 伤害值 + 回复量
                player.setHealth(player.getHealth() - amount + 5 * need);
            }
        }
    }

    @Override
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        ItemStack head = entity.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = entity.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = entity.getEquippedStack(EquipmentSlot.FEET);
        boolean noArmor = head.isEmpty() && chest.isEmpty() && legs.isEmpty() && feet.isEmpty();

        if (entity.getWorld() instanceof ServerWorld world) {

            if (entity instanceof PlayerEntity player) {
                if (player.isDead()) {
                    if (hasTrinket(SkillCards.BUQU, player)) {
                        ItemStack stack = trinketItem(SkillCards.BUQU, player);
                        int c = getTag(stack);
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.BUQU1);} else {voice(player, Sounds.BUQU2);}
                        if (new Random().nextFloat() >= (float) c /13) {
                            player.sendMessage(Text.translatable("buqu.tip1").formatted(Formatting.GREEN).append(String.valueOf(c + 1)));
                            setTag(stack, c + 1);
                            player.setHealth(5);
                        } else {
                            player.sendMessage(Text.translatable("buqu.tip2").formatted(Formatting.RED));
                            save(player, amount);
                        }
                    } else save(player, amount);
                }

                //穿藤甲时，若承受火焰伤害，则 战火燃尽，嘤熊胆！
                if (source.isIn(DamageTypeTags.IS_FIRE) && hasTrinket(ModItems.RATTAN_ARMOR, player) && !player.getCommandTags().contains("rattan")) {
                    player.addCommandTag("rattan");
                    player.timeUntilRegen = 0; player.damage(source, amount > 5 ? 5 : amount);
                    player.getCommandTags().remove("rattan");
                }

                //权计技能：受到生物伤害获得权
                if (hasTrinket(SkillCards.QUANJI, player) && source.getAttacker() instanceof LivingEntity) {
                    ItemStack stack = trinketItem(SkillCards.QUANJI, player);
                    int quan = getTag(stack);
                    quan++; setTag(stack, quan);
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.QUANJI1);} else {voice(player, Sounds.QUANJI2);}
                }

                //遗计
                if (hasTrinket(SkillCards.YIJI, player) && !player.hasStatusEffect(ModItems.COOLDOWN) && player.getHealth() <= 12) {
                    give(player, new ItemStack(ModItems.GAIN_CARD, 2));
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 20, 0, false, false, true));
                    trinketItem(SkillCards.YIJI, player).set(ModItems.TAGS, 2);
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.YIJI1);} else {voice(player, Sounds.YIJI2);}
                }

                //放逐
                if (hasTrinket(SkillCards.FANGZHU, player) && source.getAttacker() instanceof LivingEntity attacker && player != attacker) {
                    int i = attacker instanceof PlayerEntity ? (int) (20 * amount + 60) : 300;
                    attacker.addStatusEffect(new StatusEffectInstance(ModItems.TURNOVER, i));
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.FANGZHU1);} else {voice(player, Sounds.FANGZHU2);}
                }

                //刚烈
                if (hasTrinket(SkillCards.GANGLIE, player) && source.getAttacker() instanceof LivingEntity attacker && player != attacker) {
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.GANGLIE1);} else {voice(player, Sounds.GANGLIE2);}
                    for (int i = 0; i < amount; i += 5) {//造成伤害
                        if (new Random().nextFloat() < 0.5) {
                            player.addCommandTag("sha");//以此造成伤害不自动触发杀
                            float f = i + 5 < amount ? 5 : amount - i;
                            attacker.timeUntilRegen = 0; attacker.damage(player.getDamageSources().playerAttack(player), f);
                        } else {//弃牌
                            if (attacker instanceof PlayerEntity target) {//如果来源是玩家则弃牌
                                List<ItemStack> candidate = new ArrayList<>();
                                //把背包中的卡牌添加到待选物品中
                                DefaultedList<ItemStack> inventory = target.getInventory().main;
                                List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(j -> inventory.get(j).isIn(Tags.Items.CARD) || inventory.get(j).getItem() == ModItems.GAIN_CARD).boxed().toList();
                                for (Integer slot : cardSlots) {candidate.add(inventory.get(slot));}
                                //把饰品栏的卡牌添加到待选物品中
                                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(target);
                                if(component.isPresent()) {
                                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                                        ItemStack stack1 = entry.getRight(); if(stack1.isIn(Tags.Items.CARD)) candidate.add(stack1);
                                    }
                                }
                                if(!candidate.isEmpty()) {
                                    Random r = new Random(); int index = r.nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                                    target.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("dabaosword.discard")).append(chosen.getName()));
                                    chosen.decrement(1);
                                }
                            } else {//如果来源不是玩家则随机弃置它的主副手物品和装备
                                List<ItemStack> candidate = new ArrayList<>();
                                if (!attacker.getMainHandStack().isEmpty()) candidate.add(attacker.getMainHandStack());
                                if (!attacker.getOffHandStack().isEmpty()) candidate.add(attacker.getOffHandStack());
                                for (ItemStack armor : attacker.getArmorItems()) {if (!armor.isEmpty()) candidate.add(armor);}
                                if(!candidate.isEmpty()) {
                                    Random r = new Random(); int index = r.nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                                    chosen.decrement(1);
                                }
                            }
                        }
                    }
                }

            }

            //监听事件：若玩家杀死敌对生物，有概率摸牌，若杀死玩家，摸两张牌
            if (source.getAttacker() instanceof PlayerEntity player && entity.getHealth() <= 0) {
                if (entity instanceof HostileEntity) {
                    if (new Random().nextFloat() < 0.1) {
                        give(player, new ItemStack(ModItems.GAIN_CARD));
                        player.sendMessage(Text.translatable("dabaosword.draw.monster"),true);
                    }
                    //功獒技能触发
                    if (hasTrinket(SkillCards.GONGAO, player)) {
                        ItemStack stack = trinketItem(SkillCards.GONGAO, player);
                        int extraHP = getTag(stack);
                        stack.set(ModItems.TAGS, extraHP + 1);
                        player.setHealth(player.getHealth() + 1);
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.GONGAO1);} else {voice(player, Sounds.GONGAO2);}
                    }
                }
                if (entity instanceof PlayerEntity) {
                    give(player, new ItemStack(ModItems.GAIN_CARD, 2));
                    player.sendMessage(Text.translatable("dabaosword.draw.player"),true);
                    //功獒技能触发
                    if (hasTrinket(SkillCards.GONGAO, player)) {
                        ItemStack stack = trinketItem(SkillCards.GONGAO, player);
                        int extraHP = getTag(stack);
                        stack.set(ModItems.TAGS, extraHP + 5);
                        player.setHealth(player.getHealth() + 5);
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.GONGAO1);} else {voice(player, Sounds.GONGAO2);}
                    }
                }
            }

            if (source.getAttacker() instanceof PlayerEntity player) {
                //狂骨：攻击命中敌人时，如果受伤超过5则回血，否则摸一张牌
                if (hasTrinket(SkillCards.KUANGGU, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    if (player.getMaxHealth()-player.getHealth()>=5) {player.heal(5);}
                    else give(player, new ItemStack(ModItems.GAIN_CARD, 1));
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.KUANGGU1);} else {voice(player, Sounds.KUANGGU2);}
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,false,true));
                }

                //擅专：我言既出，谁敢不从！
                if (hasTrinket(SkillCards.SHANZHUAN, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    var stack = trinketItem(SkillCards.SHANZHUAN, player);
                    if (entity instanceof PlayerEntity target) openInv(player, target, Text.translatable("dabaosword.discard.title", stack.getName()), targetInv(target, true, false, 1, stack));
                    else {
                        if (new Random().nextFloat() < 0.5) {
                            voice(player, Sounds.SHANZHUAN1);
                            entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
                        } else {
                            voice(player, Sounds.SHANZHUAN2);
                            entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                        }
                        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 5,0,false,false,true));
                    }
                }

                if (player.getCommandTags().contains("px")) {
                    entity.timeUntilRegen = 0;
                }
            }

            if (source.getSource() instanceof LivingEntity attacker) {
                //古锭刀对没有装备的生物伤害增加 限定版翻倍
                if (attacker.getMainHandStack().getItem() == ModItems.GUDINGDAO && !attacker.getCommandTags().contains("guding")) {
                    if (noArmor || hasTrinket(SkillCards.POJUN, (PlayerEntity) attacker)) {
                        attacker.addCommandTag("guding");
                        entity.timeUntilRegen = 0; entity.damage(source, amount);
                        attacker.getCommandTags().remove("guding");
                    }
                }
            }

            if (source.getSource() instanceof PlayerEntity player) {
                //古锭刀对没有装备的生物伤害增加 卡牌版加5
                if (hasTrinket(ModItems.GUDING_WEAPON, player) && !player.getCommandTags().contains("guding")) {
                    if (noArmor || hasTrinket(SkillCards.POJUN, player)) {
                        player.addCommandTag("guding");
                        entity.timeUntilRegen = 0; entity.damage(source,5);
                        player.getCommandTags().remove("guding");
                    }
                }

                //寒冰剑冻伤
                if (hasTrinket(ModItems.HANBING, player)) {entity.timeUntilRegen = 0; entity.setFrozenTicks(500);}

                //杀的相关结算
                if (shouldSha(player) && !entity.isGlowing()) {
                    ItemStack stack = player.getMainHandStack().isIn(Tags.Items.SHA) ? player.getMainHandStack() : shaStack(player);
                    player.addCommandTag("sha");
                    if (stack.getItem() == ModItems.SHA) {
                        voice(player, Sounds.SHA);
                        if (!(entity instanceof PlayerEntity && hasTrinket(ModItems.RATTAN_ARMOR, (PlayerEntity) entity))) {
                            entity.timeUntilRegen = 0; entity.damage(source, 5);
                        }
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
                    int quan = getTag(stack);
                    if (quan > 0) {
                        player.addCommandTag("quanji");
                        entity.timeUntilRegen = 0; entity.damage(source, quan);
                        if (quan > 4 && entity instanceof PlayerEntity) {
                            give((PlayerEntity) entity, new ItemStack(ModItems.GAIN_CARD, 2));
                        }
                        int quan1 = quan/2; stack.set(ModItems.TAGS, quan1);
                        float j = new Random().nextFloat();
                        if (j < 0.25) {voice(player, Sounds.PAIYI1);
                        } else if (0.25 <= j && j < 0.5) {voice(player, Sounds.PAIYI2,3);
                        } else if (0.5 <= j && j < 0.75) {voice(player, Sounds.PAIYI3);
                        } else {voice(player, Sounds.PAIYI4,3);}
                    }
                }

                //奔袭：命中后减少2手长，摸一张牌
                if (hasTrinket(SkillCards.BENXI, player) && !player.getCommandTags().contains("benxi")) {
                    ItemStack stack = trinketItem(SkillCards.BENXI, player);
                    int ben = getTag(stack);
                    if (ben > 1) {
                        player.addCommandTag("benxi"); stack.set(ModItems.TAGS, ben - 2);
                        give(player, new ItemStack(ModItems.GAIN_CARD));
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.BENXI1);} else {voice(player, Sounds.BENXI2);}
                    }
                }

            }

        }
        return ActionResult.PASS;
    }

    boolean shouldSha(PlayerEntity player) {
        return getShaSlot(player) != -1 && !player.getCommandTags().contains("sha") && !player.getCommandTags().contains("juedou") && !player.getCommandTags().contains("wanjian");
    }
}

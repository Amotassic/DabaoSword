package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.event.callback.EntityHurtCallback;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;

import java.util.*;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.util.ModTools.*;

public class EntityHurtHandler implements EntityHurtCallback {

    private static void save(PlayerEntity player, float amount) {
        if (hasItemInTag(Tags.Items.RECOVER, player)) {
            //濒死自动使用酒、桃结算：首先计算需要回复的体力为(受到的伤害amount - 玩家当前生命值）
            float recover = amount - player.getHealth(); int need = (int) (recover/5) + 1;
            int tao = count(player, Tags.Items.RECOVER);//数玩家背包中回血卡牌的数量（只包含酒、桃）
            if (tao >= need) {//如果剩余回血牌大于需要的桃的数量，则进行下一步，否则直接趋势
                for (int i = 0; i < need; i++) {//循环移除背包中的酒、桃
                    if (player.timeUntilRegen > 9) {
                        ItemStack stack = stackInTag(Tags.Items.RECOVER, player);
                        if (stack.getItem() == ModItems.PEACH) voice(player, Sounds.RECOVER);
                        if (stack.getItem() == ModItems.JIU) voice(player, Sounds.JIU);
                        CardUsePostCallback.EVENT.invoker().cardUsePost(player, stack, player);
                    }
                }
                //最后将玩家的体力设置为 受伤前生命值 - 伤害值 + 回复量
                player.setHealth(player.getHealth() - amount + 5 * need);
            }
        }
    }

    @Override
    public ActionResult hurtEntity(LivingEntity entity, DamageSource source, float amount) {
        if (entity.getWorld() instanceof ServerWorld world) {

            if (entity instanceof PlayerEntity player) {
                if (player.isDead()) {
                    if (hasTrinket(SkillCards.BUQU, player)) {
                        ItemStack stack = trinketItem(SkillCards.BUQU, player);
                        int c = getTag(stack);
                        voice(player, Sounds.BUQU);
                        if (new Random().nextFloat() >= (float) c /13) {
                            player.sendMessage(Text.translatable("buqu.tip1").formatted(Formatting.GREEN).append(String.valueOf(c + 1)));
                            setTag(stack, c + 1);
                            player.setHealth(1);
                        } else {
                            player.sendMessage(Text.translatable("buqu.tip2").formatted(Formatting.RED));
                            save(player, amount);
                        }
                    } else save(player, amount);
                }

                //权计技能：受到生物伤害获得权
                if (hasTrinket(SkillCards.QUANJI, player) && source.getAttacker() instanceof LivingEntity) {
                    ItemStack stack = trinketItem(SkillCards.QUANJI, player);
                    int quan = getTag(stack);
                    setTag(stack, quan + 1);
                    voice(player, Sounds.QUANJI);
                }

                //遗计
                if (hasTrinket(SkillCards.YIJI, player) && !player.hasStatusEffect(ModItems.COOLDOWN) && player.getHealth() <= 12) {
                    draw(player, 2);
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 20, 0, false, false, true));
                    trinketItem(SkillCards.YIJI, player).set(ModItems.TAGS, 2);
                    voice(player, Sounds.YIJI);
                }

                //放逐
                if (hasTrinket(SkillCards.FANGZHU, player) && source.getAttacker() instanceof LivingEntity attacker && player != attacker) {
                    int i = attacker instanceof PlayerEntity ? (int) (20 * amount + 60) : 300;
                    attacker.addStatusEffect(new StatusEffectInstance(ModItems.TURNOVER, i));
                    voice(player, Sounds.FANGZHU);
                }

                //刚烈
                if (hasTrinket(SkillCards.GANGLIE, player) && source.getAttacker() instanceof LivingEntity attacker && player != attacker) {
                    voice(player, Sounds.GANGLIE);
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
                                List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(j -> isCard(inventory.get(j))).boxed().toList();
                                for (Integer slot : cardSlots) {candidate.add(inventory.get(slot));}
                                //把饰品栏的卡牌添加到待选物品中
                                int equip = 0; //用于标记装备区牌的数量
                                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(target);
                                if(component.isPresent()) {
                                    List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                                    for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                                        ItemStack stack1 = entry.getRight();
                                        if(stack1.isIn(Tags.Items.CARD)) candidate.add(stack1); equip++;
                                    }
                                }
                                if(!candidate.isEmpty()) {
                                    int index = new Random().nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                                    target.sendMessage(Text.literal(player.getNameForScoreboard()).append(Text.translatable("dabaosword.discard")).append(chosen.toHoverableText()));
                                    CardDiscardCallback.EVENT.invoker().cardDiscard(target, chosen, 1, index > candidate.size() - equip);
                                }
                            } else {//如果来源不是玩家则随机弃置它的主副手物品和装备
                                List<ItemStack> candidate = new ArrayList<>();
                                if (!attacker.getMainHandStack().isEmpty()) candidate.add(attacker.getMainHandStack());
                                if (!attacker.getOffHandStack().isEmpty()) candidate.add(attacker.getOffHandStack());
                                for (ItemStack armor : attacker.getArmorItems()) {if (!armor.isEmpty()) candidate.add(armor);}
                                if(!candidate.isEmpty()) {
                                    int index = new Random().nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                                    chosen.decrement(1);
                                }
                            }
                        }
                    }
                }

            }

            if (source.getAttacker() instanceof LivingEntity living) {
                if (living.getCommandTags().contains("px")) entity.timeUntilRegen = 0;
            }

            //监听事件：若玩家杀死敌对生物，有概率摸牌，若杀死玩家，摸两张牌
            if (source.getAttacker() instanceof PlayerEntity player && entity.getHealth() <= 0) {
                if (entity instanceof HostileEntity) {
                    if (new Random().nextFloat() < 0.1) {
                        draw(player);
                        player.sendMessage(Text.translatable("dabaosword.draw.monster"),true);
                    }
                    //功獒技能触发
                    if (hasTrinket(SkillCards.GONGAO, player)) {
                        ItemStack stack = trinketItem(SkillCards.GONGAO, player);
                        int extraHP = getTag(stack);
                        stack.set(ModItems.TAGS, extraHP + 1);
                        player.heal(1);
                        voice(player, Sounds.GONGAO);
                    }
                }
                if (entity instanceof PlayerEntity) {
                    draw(player, 2);
                    player.sendMessage(Text.translatable("dabaosword.draw.player"),true);
                    //功獒技能触发
                    if (hasTrinket(SkillCards.GONGAO, player)) {
                        ItemStack stack = trinketItem(SkillCards.GONGAO, player);
                        int extraHP = getTag(stack);
                        stack.set(ModItems.TAGS, extraHP + 5);
                        player.heal(5);
                        voice(player, Sounds.GONGAO);
                    }
                }
            }

            if (source.getAttacker() instanceof PlayerEntity player) {
                //狂骨：攻击命中敌人时，如果受伤超过5则回血，否则摸一张牌
                if (hasTrinket(SkillCards.KUANGGU, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    if (player.getMaxHealth()-player.getHealth()>=5) {player.heal(5);}
                    else draw(player);
                    voice(player, Sounds.KUANGGU);
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 8,0,false,false,true));
                }

                //擅专：我言既出，谁敢不从！
                if (hasTrinket(SkillCards.SHANZHUAN, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    var stack = trinketItem(SkillCards.SHANZHUAN, player);
                    if (entity instanceof PlayerEntity target) ActiveSkillHandler.openInv(player, target, Text.translatable("dabaosword.discard.title", stack.getName()), ActiveSkillHandler.targetInv(target, true, false, 1, stack));
                    else {
                        voice(player, Sounds.SHANZHUAN);
                        if (new Random().nextFloat() < 0.5) {
                            entity.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
                        } else {
                            entity.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
                        }
                        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 20 * 5,0,false,false,true));
                    }
                }
            }

            if (source.getSource() instanceof PlayerEntity player) {
                //寒冰剑冻伤
                if (hasTrinket(ModItems.HANBING, player)) {
                    voice(player, Sounds.HANBING);
                    entity.timeUntilRegen = 0;
                    entity.setFrozenTicks(500);
                }

                //杀的相关结算
                if (shouldSha(player)) {
                    ItemStack stack = player.getMainHandStack().isIn(Tags.Items.SHA) ? player.getMainHandStack() : shaStack(player);
                    player.addCommandTag("sha");
                    if (stack.getItem() == ModItems.SHA) {
                        voice(player, Sounds.SHA);
                        if (!hasTrinket(ModItems.RATTAN_ARMOR, entity)) {
                            entity.timeUntilRegen = 0; entity.damage(source, 5);
                        } else voice(entity, Sounds.TENGJIA1);
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
                    if (entity.isGlowing()) { //处理铁索连环的效果 铁索传导过去的伤害会触发2次加伤，这符合三国杀的逻辑，所以不改了
                        if (stack.getItem() != ModItems.SHA) entity.removeStatusEffect(StatusEffects.GLOWING);
                        Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
                        for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, entities -> entities.isGlowing() && entities != entity)) {
                            if (stack.getItem() == ModItems.FIRE_SHA) {
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING); nearbyEntity.damage(source, amount);
                                nearbyEntity.timeUntilRegen = 0; nearbyEntity.setOnFireFor(5);
                            }
                            if (stack.getItem() == ModItems.THUNDER_SHA) {
                                nearbyEntity.removeStatusEffect(StatusEffects.GLOWING); nearbyEntity.damage(source, amount);
                                nearbyEntity.timeUntilRegen = 0; nearbyEntity.damage(player.getDamageSources().magic(), 5);
                                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                                if (lightningEntity != null) {
                                    lightningEntity.refreshPositionAfterTeleport(nearbyEntity.getX(), nearbyEntity.getY(), nearbyEntity.getZ());
                                    lightningEntity.setCosmetic(true);
                                }
                                world.spawnEntity(lightningEntity);
                            }
                        }
                    }
                    CardUsePostCallback.EVENT.invoker().cardUsePost(player, stack, entity);
                }

                //奔袭：命中后减少2手长，摸一张牌
                if (hasTrinket(SkillCards.BENXI, player) && !player.getCommandTags().contains("benxi")) {
                    ItemStack stack = trinketItem(SkillCards.BENXI, player);
                    int ben = getTag(stack);
                    if (ben > 1) {
                        player.addCommandTag("benxi"); stack.set(ModItems.TAGS, ben - 2);
                        draw(player);
                        voice(player, Sounds.BENXI);
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

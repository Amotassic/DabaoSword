package com.amotassic.dabaosword.item.skillcard.skills;

import com.amotassic.dabaosword.api.ICardEvent;
import com.amotassic.dabaosword.item.LetMeCCItem;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillItem;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.event.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.api.event.CardEvents.cardMove;
import static com.amotassic.dabaosword.util.ModTools.*;

public class Wu {

    public static class Buqu extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int c = getTag(stack);
            if(Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip1").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip2").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip3").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip4").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip5").formatted(Formatting.GREEN));
            } else {
                tooltip.add(Text.literal("创：" + c));
                tooltip.add(Text.translatable("item.dabaosword.buqu.tooltip").formatted(Formatting.GREEN));
                tooltip.add(Text.translatable("dabaosword.shift_tip", Text.keybind("key.sneak")));
            }
        }

        @Override
        public void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount) {
            if (entity instanceof PlayerEntity player && player.isDead()) {
                int c = getTag(stack);
                voice(player, Sounds.BUQU);
                if (new Random().nextFloat() >= (float) c /13) {
                    player.sendMessage(Text.translatable("buqu.tip1", c + 1).formatted(Formatting.GREEN));
                    setTag(stack, c + 1);
                    player.setHealth(1);
                } else player.sendMessage(Text.translatable("buqu.tip2").formatted(Formatting.RED));
            }
        }
    }

    public static class Fenyin extends SkillItem implements ICardEvent {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.dabaosword.fenyin.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void postCardUse(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {
            var nbt = skill.getOrCreateNbt();
            int last = nbt.contains("fenyin") ? nbt.getInt("fenyin") : 0;
            int current = isRedCard.test(card) ? 1 : isBlackCard.test(card) ? 2 : 0;
            if (last != 0 && current != 0 && current != last) {draw(user); voice(user, skill);}
            nbt.putInt("fenyin", current);
            skill.setNbt(nbt);
        }
    }

    public static class Gongxin extends SkillItem.ActiveSkillWithTarget {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 30s" : "CD: 30s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.gongxin.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            int cd = getCD(stack);
            if (cd > 0) user.sendMessage(Text.translatable("dabaosword.cooldown").formatted(Formatting.RED), true);
            else {
                voice(user, Sounds.GONGXIN);
                openInv(user, target, Text.translatable("gongxin.title"), stack, false, false, false, 2);
                setCD(stack, 30);
            }
        }

        @Override
        public void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slot) {
            target.sendMessage(Text.translatable("dabaosword.discard", player.getDisplayName(), target.getDisplayName(), selected.toHoverableText()));
            cardDiscard(target, selected, 1, false);
            closeGUI(player);
        }
    }

    public static class Guose extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int cd = getCD(stack);
            tooltip.add(Text.literal(cd == 0 ? "CD: 15s" : "CD: 15s   left: "+ cd +"s"));
            tooltip.add(Text.translatable("item.dabaosword.guose.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 15, isDiamondCard, new ItemStack(ModItems.TOO_HAPPY_ITEM));
            super.tick(stack, slot, entity);
        }
    }

    public static class Kurou extends SkillItem.ActiveSkill {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.kurou.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            if (user.getHealth() + 5 * countCard(user, canSaveDying) > 4.99) {
                draw(user, 2);
                if (!user.isCreative()) {
                    user.timeUntilRegen = 0;
                    user.damage(user.getDamageSources().genericKill(), 4.99f);
                }
                voice(user, Sounds.KUROU);
            } else {user.sendMessage(Text.translatable("item.dabaosword.kurou.tip").formatted(Formatting.RED), true);}
        }
    }

    public static class Lianying extends SkillItem implements ICardEvent {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.lianying.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world) {
                int cd = getCD(stack);
                if (world.getTime() % 20 == 0 && cd == 1) { //确保一秒内只触发一次
                    draw(entity);
                    voice(entity, stack);
                }
            }
            super.tick(stack, slot, entity);
        }

        @Override
        public void postCardUse(LivingEntity user, ItemStack card, LivingEntity target, ItemStack skill) {
            if (countCards(user) == 0) setCD(skill, 5);
        }

        @Override
        public void onCardDiscard(LivingEntity entity, ItemStack card, int count, boolean fromEquip, ItemStack skill) {
            if (entity.isAlive() && !fromEquip && countCards(entity) == 0) setCD(skill, 5);
        }

        @Override
        public void onCardMove(LivingEntity from, ItemStack skill, LivingEntity to, ItemStack card, int count, boolean fromEquip, boolean toEquip) {
            if (!fromEquip && countCards(from) == 0) setCD(skill, 5);
        }
    }

    public static class Liuli extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.liuli.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public CancelDamageData cancelDamage() {
            return new CancelDamageData(Priority.NORMAL, (target, source, amount) -> {
                if (source.getAttacker() instanceof LivingEntity attacker && target instanceof PlayerEntity player) {
                    if (hasTrinket(this, player) && hasCard(player, isCard) && !player.hasStatusEffect(ModItems.INVULNERABLE)) {
                        LivingEntity nearEntity = LetMeCCItem.getClosestEntity(player, LivingEntity.class, 10, entity -> entity != player && entity != attacker);
                        if (nearEntity != null) {
                            player.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 15,0,false,false,false));
                            voice(player, Sounds.LIULI);
                            cardDiscard(player, getCard(player, isCard), 1, false);
                            nearEntity.timeUntilRegen = 0; nearEntity.damage(source, amount);
                            return true;
                        }
                    }
                }
                return false;
            });
        }
    }

    public static class Pojun extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 10s"));
            tooltip.add(Text.translatable("item.dabaosword.pojun.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void preAttack(ItemStack stack, LivingEntity target, PlayerEntity player) {
            //破军：攻击命中盔甲槽有物品的生物后，会让其所有盔甲掉落，配合古锭刀特效使用，pvp神器
            if (!player.hasStatusEffect(ModItems.COOLDOWN)) {
                for (var armor : target.getArmorItems()) {
                    if (armor.isEmpty()) continue;
                    if (target instanceof PlayerEntity pl) {give(pl, armor.copy()); armor.setCount(0);}
                    else {target.dropStack(armor.copy()); armor.setCount(0);}
                }
                voice(player, Sounds.POJUN);
                int i = target instanceof PlayerEntity ? 200 : 40;
                player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, i,0, false,false,true));
            }
        }
    }

    public static class Qixi extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.literal("CD: 5s"));
            tooltip.add(Text.translatable("item.dabaosword.qixi.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            viewAs(entity, stack, 5, isBlackCard, new ItemStack(ModItems.DISCARD));
            super.tick(stack, slot, entity);
        }
    }

    public static class Xiaoji extends SkillItem implements ICardEvent {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.xiaoji.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public void onCardDiscard(LivingEntity entity, ItemStack card, int count, boolean fromEquip, ItemStack skill) {
            if (entity.isAlive() && fromEquip) {draw(entity, 2); voice(entity, skill);}
        }

        @Override
        public void onCardMove(LivingEntity from, ItemStack skill, LivingEntity to, ItemStack card, int count, boolean fromEquip, boolean toEquip) {
            if (fromEquip) {draw(from, 2); voice(from, skill);}
        }
    }

    public static class Yingzi extends SkillItem {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.yingzi.tooltip").formatted(Formatting.GREEN));
        }

        @Override
        public int onDrawPhase(PlayerEntity player, ItemStack stack) {
            voice(player, stack);
            return 1;
        }
    }

    public static class Zhiheng extends SkillItem.ActiveSkill {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            int z = getTag(stack);
            tooltip.add(Text.literal("可用次数：" + z));
            tooltip.add(Text.translatable("item.dabaosword.zhiheng.tooltip1").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.zhiheng.tooltip2").formatted(Formatting.GREEN));
        }

        @Override
        public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (entity.getWorld() instanceof ServerWorld world) {
                int z = getTag(stack);
                if (z < 10 && world.getTime() % 100 == 0) setTag(stack, z + 1);
            }
            super.tick(stack, slot, entity);
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            int z = getTag(stack);
            if (z > 0) openInv(user, user, Text.translatable("zhiheng.title"), stack, true, true, false, 2);
            else user.sendMessage(Text.translatable("zhiheng.fail").formatted(Formatting.RED), true);
        }

        @Override
        public void onClickGUISlot(PlayerEntity player, ItemStack stack, PlayerEntity target, ItemStack selected, int slotIndex) {
            int z = getTag(stack);
            voice(player, Sounds.ZHIHENG);
            cardDiscard(player, selected, 1, slotIndex < 4);
            if (new Random().nextFloat() < 0.1) {
                draw(player, 2);
                player.sendMessage(Text.translatable("zhiheng.extra").formatted(Formatting.GREEN), true);
            } else draw(player);
            setTag(stack, z - 1);
            if (z - 1 == 0) closeGUI(player);
        }
    }

    public static class Zhijian extends SkillItem.ActiveSkillWithTarget {
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
            tooltip.add(Text.translatable("item.dabaosword.zhijian.tooltip1").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.zhijian.tooltip2").formatted(Formatting.GREEN));
        }

        @Override
        public void activeSkill(PlayerEntity user, ItemStack stack, PlayerEntity target) {
            ItemStack itemStack = user.getMainHandStack();
            if (isEquipment.test(itemStack)) {
                cardMove(user, target, itemStack, itemStack.getCount(), false, true);
                voice(user, Sounds.ZHIJIAN);
                draw(user);
            } else user.sendMessage(Text.translatable("zhijian.fail").formatted(Formatting.RED), true);
        }
    }
}

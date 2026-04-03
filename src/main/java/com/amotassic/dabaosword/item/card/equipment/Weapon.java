package com.amotassic.dabaosword.item.card.equipment;

import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.ChatFormatting.*;

public class Weapon extends Equipment {
    public Weapon(Properties settings) {super(settings);}

    public static class Cixiong extends Weapon  {
        public Cixiong(Properties settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @SkillInfo(trigger = Trigger.SELECT_TARGET, relation = Relation.NOT_SELF)
        public int onSha(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            Card card = data.getFirst();
            if (isSha.test(card.toStack()) && target != null && new Random().nextFloat() < 0.5) {
                draw(user); voice(user, this);
            }
            return 0;
        }
    }

    public static class Fangtian extends Weapon {
        public Fangtian(Properties settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(Player player, LivingEntity target, Skill skill) {
            //方天画戟：打中生物后触发特效，给予CD和持续时间
            if (skill.getCD() == 0) {
                skill.setCD(20);
                voice(player, this);
                player.sendOverlayMessage(Component.translatable("dabaosword.fangtian").withStyle(RED));
            }
        }
    }

    public static class Guanshi extends Weapon  {
        public Guanshi(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }
    }

    public static class Guding extends Weapon {
        public Guding(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(Component.translatable("item.dabaosword.gudingdao.tooltip").withStyle(GREEN));
            tooltip.add(Component.translatable("item.dabaosword.gudingdao.tooltip2").withStyle(AQUA));
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.DIRECT_ATTACKER)
        public int addDamage(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var adds = data.adds;
            int i = 0;
            for (var s : getArmorItems(target)) {if (s.isEmpty()) i++;}
            if (i == 4) {
                voice(user, this);
                adds.add(5f);
            }
            return 0;
        }
    }

    public static class Hanbing extends Weapon {
        public Hanbing(Properties settings) {super(settings);}

        public void addTip(Skill skill, List<Component> tooltip) {tooltip.add(getTip(AQUA));}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.DIRECT_ATTACKER)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            voice(user, this);
            target.invulnerableTime = 0;
            target.setTicksFrozen(500);
            return 0;
        }
    }

    public static class Liannu extends Weapon {
        public Liannu(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(Player player, LivingEntity target, Skill skill) {
            int i = 2;
            if (hasTrinket(ModItems.CHITU, player)) i++;
            if (hasTrinket(SkillCards.MASHU, player)) i++;
            if (hasTrinket(ModItems.DILU, target)) i--;
            if (hasTrinket(SkillCards.FEIYING, target)) i--;
            if (player.distanceTo(target) <= i) {
                player.addEffect(new MobEffectInstance(MobEffects.HASTE, 3, 255,false, false, false));
                voice(player, this);
            }
        }
    }

    public static class Qilin extends Weapon {
        public Qilin(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
            tooltip.add(getTip("3", AQUA));
        }

        @Override public int getExtraReach(LivingEntity entity, Skill skill) {return 1;}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.ATTACKER)
        public int nimameile(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (skill.getCD() != 0) return 0;
            ItemStack chitu = trinketItem(ModItems.CHITU, target);
            ItemStack dilu = trinketItem(ModItems.DILU, target);
            List<ItemStack> horse = new ArrayList<>();
            if (!chitu.isEmpty()) horse.add(chitu); if (!dilu.isEmpty()) horse.add(dilu);
            if (horse.isEmpty()) return 0;
            ItemStack selected = horse.get(new Random().nextInt(horse.size()));
            Component message = Component.translatable("dabaosword.discard", user.getDisplayName(), target.getDisplayName(), selected.getDisplayName());
            if (user instanceof Player player) player.sendSystemMessage(message);
            if (target instanceof Player player) player.sendSystemMessage(message);
            cardDiscard(target, d().cards(selected, 1, true));
            voice(user, this);
            skill.setCD(30);
            return 0;
        }
    }

    public static class Qinggang extends Weapon {
        public Qinggang(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(Player player, LivingEntity target, Skill skill) {
            if (player.getAttackStrengthScale(0f) < 1f) return;
            //青釭剑额外伤害
            float extraDamage = Math.min(20, 0.2f * target.getMaxHealth());
            target.hurtServer(world(player), damageSource(player, DamageTypes.GENERIC_KILL), extraDamage);
            target.invulnerableTime = 0;
            voice(player, this);
        }
    }

    public static class Qinglong extends Weapon {
        public Qinglong(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(Player player, LivingEntity target, Skill skill) {
            voice(player, this);
            player.addEffect(new MobEffectInstance(ModItems.INVULNERABLE,10,0,false,false,false));
            player.teleportTo(target.getX(), target.getY(), target.getZ());
            Vec3 momentum = player.getForward().scale(2);
            target.hurtMarked = true; target.setDeltaMovement(momentum.x,0 ,momentum.z);
        }
    }

    public static class Zhangba extends Weapon {
        public Zhangba(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @SkillInfo(trigger = Trigger.LOSE_CARD_USE, relation = Relation.SELF)
        public int useCardGetSha(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            if (skill.getCD() > 0) return 0;
            ItemStack stack = data.getFirst().toStack();
            if (isSha.test(stack)) return 0;
            skill.setTag(skill.getTag() + 1);
            if (skill.getTag() >= 2) {
                skill.setTag(0);
                skill.setCD(1);
                give(user, new Card(ModItems.SHA).toStack());
            }
            return 0;
        }
    }

    public static class Zhuque extends Weapon {
        public Zhuque(Properties settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Component> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.ATTACKER)
        public int fire(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            voice(user, this);
            target.setRemainingFireTicks(80);
            return 0;
        }
    }
}

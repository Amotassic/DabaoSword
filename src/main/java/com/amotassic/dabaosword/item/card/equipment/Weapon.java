package com.amotassic.dabaosword.item.card.equipment;

import com.amotassic.dabaosword.api.card.Card;
import com.amotassic.dabaosword.api.skill.*;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.amotassic.dabaosword.api.CardEvents.cardDiscard;
import static com.amotassic.dabaosword.util.ModTools.*;
import static net.minecraft.util.Formatting.AQUA;

public class Weapon extends Equipment {
    public Weapon(Settings settings) {super(settings);}

    public static class Cixiong extends Weapon  {
        public Cixiong(Settings settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @SkillInfo(trigger = Trigger.SELECT_TARGET, relation = Relation.NOT_SELF)
        public int onSha(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            Card card = data.getCard();
            if (isSha.test(card.toStack()) && target != null && new Random().nextFloat() < 0.5) {
                draw(user); voice(user, this);
            }
            return 0;
        }
    }

    public static class Fangtian extends Weapon {
        public Fangtian(Settings settings) {super(settings);}

        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {
            //方天画戟：打中生物后触发特效，给予CD和持续时间
            if (skill.getCD() == 0) {
                skill.setCD(20);
                voice(player, this);
                player.sendMessage(Text.translatable("dabaosword.fangtian").formatted(Formatting.RED), true);
            }
        }
    }

    public static class Guanshi extends Weapon  {
        public Guanshi(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }
    }

    public static class Guding extends Weapon {
        public Guding(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip").formatted(Formatting.GREEN));
            tooltip.add(Text.translatable("item.dabaosword.gudingdao.tooltip2").formatted(AQUA));
        }

        @SkillInfo(trigger = Trigger.MODIFY_DAMAGE, relation = Relation.DIRECT_ATTACKER)
        public int addDamage(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            var adds = data.adds;
            int i = 0;
            for (var s : target.getArmorItems()) {if (s.isEmpty()) i++;}
            if (i == 4) {
                voice(user, this);
                adds.add(5f);
            }
            return 0;
        }
    }

    public static class Hanbing extends Weapon {
        public Hanbing(Settings settings) {super(settings);}

        public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip(AQUA));}

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.DIRECT_ATTACKER)
        public int onHit(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            voice(user, this);
            target.timeUntilRegen = 0;
            target.setFrozenTicks(500);
            return 0;
        }
    }

    public static class Liannu extends Weapon {
        public Liannu(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {
            int i = 2;
            if (hasTrinket(ModItems.CHITU, player)) i++;
            if (hasTrinket(SkillCards.MASHU, player)) i++;
            if (hasTrinket(ModItems.DILU, target)) i--;
            if (hasTrinket(SkillCards.FEIYING, target)) i--;
            if (player.distanceTo(target) <= i) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 3, 255,false, false, false));
                voice(player, this);
            }
        }
    }

    public static class Qilin extends Weapon {
        public Qilin(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
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
            Text message = Text.translatable("dabaosword.discard", user.getDisplayName(), target.getDisplayName(), selected.toHoverableText());
            if (user instanceof PlayerEntity player) player.sendMessage(message, false);
            if (target instanceof PlayerEntity player) player.sendMessage(message, false);
            cardDiscard(target, d().cards(selected, 1, true));
            voice(user, this);
            skill.setCD(30);
            return 0;
        }
    }

    public static class Qinggang extends Weapon {
        public Qinggang(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {
            if (player.getAttackCooldownProgress(0f) < 1f) return;
            //青釭剑额外伤害
            float extraDamage = Math.min(20, 0.2f * target.getMaxHealth());
            target.damage(world(player), player.getDamageSources().genericKill(), extraDamage); target.timeUntilRegen = 0;
            voice(player, this);
        }
    }

    public static class Qinglong extends Weapon {
        public Qinglong(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void preAttack(PlayerEntity player, LivingEntity target, Skill skill) {
            voice(player, this);
            player.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE,10,0,false,false,false));
            player.teleport(target.getX(), target.getY(), target.getZ(), false);
            Vec3d momentum = player.getRotationVector().multiply(2);
            target.velocityModified = true; target.setVelocity(momentum.getX(),0 ,momentum.getZ());
        }
    }

    public static class Zhangba extends Weapon {
        public Zhangba(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @Override
        public void tickSkill(Skill skill, LivingEntity entity) {
            if (entity.getWorld().isClient) return;
            if (entity instanceof PlayerEntity player && skill.getCD() == 0) {
                ItemStack off = player.getOffHandStack();
                NbtCompound nbt = skill.getNbt();
                boolean one = nbt.contains("has_one");
                if (isCard(off)) {
                    if (one) {
                        nbt.remove("has_one");
                        skill.setCD(5);
                        give(player, newCard(ModItems.SHA));
                        voice(player, this);
                    } else {nbt.putBoolean("has_one", true);}
                    skill.setNbt(nbt);
                    off.decrement(1);
                }
            }
        }
    }

    public static class Zhuque extends Weapon {
        public Zhuque(Settings settings) {super(settings);}
        @Override
        public void addTip(Skill skill, List<Text> tooltip) {
            tooltip.add(getTip("1"));
            tooltip.add(getTip("2", AQUA));
        }

        @SkillInfo(trigger = Trigger.ON_HURT, relation = Relation.ATTACKER)
        public int fire(LivingEntity user, LivingEntity target, Skill skill, ExData data) {
            voice(user, this);
            target.setOnFireFor(4);
            return 0;
        }
    }
}

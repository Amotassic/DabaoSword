package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ModTools {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Unique PlayerEntity player = (PlayerEntity) (Object) this;

    @Shadow public abstract boolean isCreative();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract void sendMessage(Text message, boolean overlay);

    @Inject(method = "damage",at = @At("HEAD"), cancellable = true)
    private void damagemixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        if (this.getWorld() instanceof ServerWorld world) {

            if (source.getSource() instanceof LivingEntity entity) {
                //若攻击者主手没有物品，则无法击穿藤甲
                if (inrattan(player)) {
                    if (entity.getMainHandStack().isEmpty()) cir.setReturnValue(false);
                    else if (getShanSlot(player) != -1 && !this.hasStatusEffect(ModItems.COOLDOWN2)) {
                        cir.setReturnValue(false);
                        shan(player,false);//闪的额外判断
                    }
                }
            }
            //弹射物对藤甲无效
            if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan(player)) {cir.setReturnValue(false);}

            if (source.getSource() instanceof WolfEntity dog && dog.hasStatusEffect(ModItems.INVULNERABLE)) {
                //被南蛮入侵的狗打中可以消耗杀以免疫伤害
                if (dog.getOwner() != this) {
                    if (getShaSlot(player) != -1) {
                        ItemStack stack = shaStack(player);
                        cir.setReturnValue(false);
                        if (stack.getItem() == ModItems.SHA) voice(player, Sounds.SHA);
                        if (stack.getItem() == ModItems.FIRE_SHA) voice(player, Sounds.SHA_FIRE);
                        if (stack.getItem() == ModItems.THUNDER_SHA) voice(player, Sounds.SHA_THUNDER);
                        stack.decrement(1);
                    }
                    dog.setHealth(0);
                }
            }

            if (source.getAttacker() instanceof LivingEntity) {

                final boolean trigger = baguaTrigger(player);
                boolean hasShan = getShanSlot(player) != -1 || trigger;
                boolean shouldShan = !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !getCommandTags().contains("juedou") && hasShan && !isCreative() && !hasStatusEffect(ModItems.COOLDOWN2) && !hasStatusEffect(ModItems.INVULNERABLE) && !hasTrinket(SkillCards.LIULI, player) && !hasTrinket(ModItems.RATTAN_ARMOR, player);

                //闪的被动效果
                if (shouldShan) {
                    cir.setReturnValue(false);
                    shan(player, trigger);
                    //虽然没有因为杀而触发闪，但如果攻击者的杀处于自动触发状态，则仍会消耗
                    if (source.getSource() instanceof PlayerEntity player1 && getShaSlot(player1) != -1) {
                        ItemStack stack = shaStack(player1);
                        if (stack.getItem() == ModItems.SHA) voice(player, Sounds.SHA);
                        if (stack.getItem() == ModItems.FIRE_SHA) voice(player, Sounds.SHA_FIRE);
                        if (stack.getItem() == ModItems.THUNDER_SHA) voice(player, Sounds.SHA_THUNDER);
                        benxi(player1);
                        if (!player1.isCreative()) stack.decrement(1);
                    }
                }

                if (hasTrinket(ModItems.BAIYIN,player) && !this.getCommandTags().contains("baiyin")) {
                    cir.setReturnValue(false);
                    this.addCommandTag("baiyin");
                    this.damage(source, 0.4f * amount);
                    this.getCommandTags().remove("baiyin");
                }
            }

            //流离
            if (hasTrinket(SkillCards.LIULI, player) && source.getAttacker() instanceof LivingEntity attacker && hasItemInTag(Tags.Items.CARD, player) && !this.hasStatusEffect(ModItems.INVULNERABLE) && !this.isCreative()) {
                ItemStack stack = stackInTag(Tags.Items.CARD, player);
                Box box = new Box(this.getBlockPos()).expand(10);
                int i = 0;
                for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity -> LivingEntity != attacker && LivingEntity != this)) {
                    if (nearbyEntity != null) {
                        cir.setReturnValue(false);
                        this.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                        this.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 10,0,false,false,false));
                        stack.decrement(1);
                        if (new Random().nextFloat() < 0.5) {voice(player, Sounds.LIULI1);} else {voice(player, Sounds.LIULI2);}
                        nearbyEntity.timeUntilRegen = 0;
                        nearbyEntity.damage(source, amount); i++; break;
                    }
                }
                //避免闪自动触发，因此在这里额外判断
                if (i == 0 && !this.hasStatusEffect(ModItems.COOLDOWN2)) {
                    final boolean trigger = baguaTrigger(player);
                    boolean hasShan = getShanSlot(player) != -1 || trigger;
                    if (hasShan) {
                        cir.setReturnValue(false);
                        shan(player, trigger);
                    }
                }
            }

        }
    }

    @Unique private int tick = 0;
    @Unique private int tick2 = 0;
    @Unique private int skillChange = 0;

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld world) {
            int giveCard = world.getGameRules().getInt(Gamerule.GIVE_CARD_INTERVAL) * 20;
            int skill = world.getGameRules().getInt(Gamerule.CHANGE_SKILL_INTERVAL) * 20;

            if (++tick >= giveCard) { // 每分钟摸两张牌
                tick = 0;
                if (hasTrinket(ModItems.CARD_PILE, player) && !player.isCreative() && !player.isSpectator()) {
                    player.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                    player.sendMessage(Text.translatable("dabaosword.draw"),true);
                        /*if (count(player, Tags.Items.CARD) + count(player, ModItems.GAIN_CARD) <= 8) {
                        }*/
                }
            }

            if (skill != -20) {
                if (++skillChange >= skill) {//每5分钟可以切换技能
                    skillChange = 0;
                    player.addCommandTag("change_skill");
                    if (skill >= 600) {
                        player.sendMessage(Text.translatable("dabaosword.change_skill").formatted(Formatting.BOLD));
                        player.sendMessage(Text.translatable("dabaosword.change_skill2"));
                    }
                }
            }

            if (++tick2 >= 2) {
                tick2 = 0;
                player.getCommandTags().remove("quanji");
                player.getCommandTags().remove("sha");
                player.getCommandTags().remove("benxi");
                player.getCommandTags().remove("juedou");

                //牌堆恢复饱食度
                boolean food = world.getGameRules().getBoolean(Gamerule.CARD_PILE_HUNGERLESS);
                if (hasTrinket(ModItems.CARD_PILE, player) && food) {player.getHungerManager().setFoodLevel(20);}
            }

            Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
            for (LivingEntity nearbyPlayer : world.getEntitiesByClass(PlayerEntity.class, box, playerEntity -> playerEntity.hasStatusEffect(ModItems.DEFEND))) {
                //实现沈佳宜的效果：若玩家看到的玩家有近战防御效果，则给当前玩家攻击范围缩短效果
                int amplifier = Objects.requireNonNull(nearbyPlayer.getStatusEffect(ModItems.DEFEND)).getAmplifier();
                int attack = (int) (player.getAttributeValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE));
                int defensed = Math.min(amplifier, attack);
                if (player != nearbyPlayer && islooking(player, nearbyPlayer)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.DEFENDED, 1,defensed,false,false,false));
                }
            }

            //马术和飞影的效果
            if (shouldMashu(player)) {
                if (hasTrinket(ModItems.CHITU, player) && hasTrinket(SkillCards.MASHU, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,2,false,false,true));
                } else if (hasTrinket(ModItems.CHITU, player) || hasTrinket(SkillCards.MASHU, player)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,1,false,false,true));
                }
            }
            if (hasTrinket(ModItems.DILU, player) && hasTrinket(SkillCards.FEIYING, player)) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 10,2,false,false,true));
            } else if (hasTrinket(ModItems.DILU, player) || hasTrinket(SkillCards.FEIYING, player)) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 10,1,false,false,true));
            }

            if (this.getCommandTags().contains("px")) {
                this.lastAttackedTicks = 1145;
            }

            //下落攻击触发：脚底下两格是空气，手里拿着有耐久度的物品左键即可触发
            BlockPos blockPos = player.getBlockPos().down(1); BlockPos blockPos2 = player.getBlockPos().down(2);
            boolean falling = world.getGameRules().getBoolean(Gamerule.ENABLE_FALLING_ATTACK);
            if (falling && world.getBlockState(blockPos).getBlock() == Blocks.AIR && world.getBlockState(blockPos2).getBlock() == Blocks.AIR && player.getMainHandStack().isDamageable() && player.handSwingTicks == 1) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.FALLING_ATTACK, StatusEffectInstance.INFINITE,0,false,false,false));
            }

        }
    }

    @Unique
    boolean shouldMashu(PlayerEntity player) {
        return !hasTrinket(SkillCards.BENXI, player) && player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
    }

    @Unique
    boolean islooking(PlayerEntity player, Entity entity) {
        Vec3d vec3d = player.getRotationVec(1.0f).normalize();
        Vec3d vec3d2 = new Vec3d(entity.getX() - player.getX(), entity.getEyeY() - player.getEyeY(), entity.getZ() - player.getZ());
        double d = vec3d2.length();
        double e = vec3d.dotProduct(vec3d2.normalize());
        if (e > 1.0 - 0.25 / d) {
            return player.canSee(entity);
        }
        return false;
    }

    @Unique boolean inrattan(PlayerEntity player) {
        return hasTrinket(ModItems.RATTAN_ARMOR, player);
    }

    @Unique boolean baguaTrigger(PlayerEntity player) {
        return hasTrinket(ModItems.BAGUA, player) && new Random().nextFloat() < 0.5;
    }
    @Unique
    void shan(PlayerEntity player, boolean bl) {
        ItemStack stack = bl ? trinketItem(ModItems.BAGUA, player) : shanStack(player);
        int cd = bl ? 60 : 40;
        player.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20,0,false,false,false));
        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, cd,0,false,false,false));
        voice(player, Sounds.SHAN);
        benxi(player);
        if (bl) player.sendMessage(Text.translatable("dabaosword.bagua"),true);
        else stack.decrement(1);
    }

    @Unique
    int getShanSlot(PlayerEntity player) {
        for (int i = 0; i < 18; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getItem() != ModItems.SHAN) continue;
            return i;
        }
        return -1;
    }

    @Unique
    ItemStack shanStack(PlayerEntity player) {
        return player.getInventory().getStack(getShanSlot(player));
    }

    @Inject(at = @At("TAIL"), method = "applyDamage", cancellable = true)
    private void onEntityHurt(final DamageSource source, final float amount, CallbackInfo ci) {
        ActionResult result = EntityHurtCallback.EVENT.invoker().hurtEntity((PlayerEntity) (Object) this, source, amount);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}

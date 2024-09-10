package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.event.callback.EntityHurtCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.*;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static com.amotassic.dabaosword.util.ModTools.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Unique PlayerEntity player = (PlayerEntity) (Object) this;

    @Unique private int tick = 0;
    @Unique private int tick2 = 0;
    @Unique private int skillChange = 0;

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.getWorld() instanceof ServerWorld world) {
            int giveCard = world.getGameRules().getInt(Gamerule.GIVE_CARD_INTERVAL) * 20;
            int skill = world.getGameRules().getInt(Gamerule.CHANGE_SKILL_INTERVAL) * 20;
            boolean enableLimit = world.getGameRules().getBoolean(Gamerule.ENABLE_CARDS_LIMIT);

            if (++tick >= giveCard) { // 每分钟摸两张牌
                tick = 0;
                if (hasTrinket(ModItems.CARD_PILE, player) && !player.isCreative() && !player.isSpectator()) {
                    if (countCards(player) <= player.getMaxHealth()) {
                        draw(player, 2);
                        player.sendMessage(Text.translatable("dabaosword.draw"),true);
                    } else if (!enableLimit) {//如果不限制摸牌就继续发牌
                        draw(player, 2);
                        player.sendMessage(Text.translatable("dabaosword.draw"),true);
                    }
                }
            }

            if (skill != -20) {
                if (++skillChange >= skill) {//每5分钟可以切换技能
                    skillChange = 0;
                    player.addCommandTag("change_skill");
                    if (skill >= 600 && hasTrinket(ModItems.CARD_PILE, player)) {
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
                player.getCommandTags().remove("xingshang");

                //牌堆恢复饱食度
                boolean food = world.getGameRules().getBoolean(Gamerule.CARD_PILE_HUNGERLESS);
                if (hasTrinket(ModItems.CARD_PILE, player) && food) {player.getHungerManager().setFoodLevel(20);}
            }

            Box box = new Box(player.getBlockPos()).expand(20); // 检测范围，根据需要修改
            for (LivingEntity nearbyPlayer : world.getEntitiesByClass(PlayerEntity.class, box, playerEntity -> playerEntity.hasStatusEffect(ModItems.DEFEND))) {
                //实现沈佳宜的效果：若玩家看到的玩家有近战防御效果，则给当前玩家攻击范围缩短效果
                int amplifier = Objects.requireNonNull(nearbyPlayer.getStatusEffect(ModItems.DEFEND)).getAmplifier();
                int attack = (int) (player.getAttributeValue(ReachEntityAttributes.ATTACK_RANGE) + 3);
                int defended = Math.min(amplifier, attack);
                if (player != nearbyPlayer && isLooking(player, nearbyPlayer)) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.DEFENDED, 1, defended,false,false,false));
                }
            }

            int level1 = 0; int level2 = 0; //马术和飞影的效果
            if (shouldMashu(player)) {
                if (hasTrinket(ModItems.CHITU, player)) level1++;
                if (hasTrinket(SkillCards.MASHU, player)) level1++;
                if (level1 > 0) player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,level1,false,false,true));
            }
            if (hasTrinket(ModItems.DILU, player)) level2++;
            if (hasTrinket(SkillCards.FEIYING, player)) level2++;
            if (level2 > 0) player.addStatusEffect(new StatusEffectInstance(ModItems.DEFEND, 10,level2,false,false,true));

            if (this.getCommandTags().contains("px")) {
                this.lastAttackedTicks = 1145;
            }

            //下落攻击触发：脚底下两格是空气，手里拿着有耐久度的物品左键即可触发
            BlockPos blockPos = player.getBlockPos().down(1); BlockPos blockPos2 = player.getBlockPos().down(2);
            boolean falling = world.getGameRules().getBoolean(Gamerule.ENABLE_FALLING_ATTACK);
            if (falling && world.getBlockState(blockPos).getBlock() == Blocks.AIR && world.getBlockState(blockPos2).getBlock() == Blocks.AIR && player.getMainHandStack().getItem().isDamageable() && player.handSwingTicks == 1) {
                player.addStatusEffect(new StatusEffectInstance(ModItems.FALLING_ATTACK, StatusEffectInstance.INFINITE,0,false,false,false));
            }

        }
    }

    @Unique
    boolean shouldMashu(PlayerEntity player) {
        return !hasTrinket(SkillCards.BENXI, player) && player.getMainHandStack().getItem() != ModItems.JUEDOU && player.getMainHandStack().getItem() != ModItems.DISCARD;
    }

    @Unique
    boolean isLooking(PlayerEntity player, Entity entity) {
        Vec3d vec3d = player.getRotationVec(1.0f).normalize();
        Vec3d vec3d2 = new Vec3d(entity.getX() - player.getX(), entity.getEyeY() - player.getEyeY(), entity.getZ() - player.getZ());
        double d = vec3d2.length();
        double e = vec3d.dotProduct(vec3d2.normalize());
        if (e > 1.0 - 0.25 / d) {
            return player.canSee(entity);
        }
        return false;
    }

    @Inject(at = @At("TAIL"), method = "applyDamage", cancellable = true)
    private void onEntityHurt(final DamageSource source, final float amount, CallbackInfo ci) {
        ActionResult result = EntityHurtCallback.EVENT.invoker().hurtEntity((PlayerEntity) (Object) this, source, amount);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}

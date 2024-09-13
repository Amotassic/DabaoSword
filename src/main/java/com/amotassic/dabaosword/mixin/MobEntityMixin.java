package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.event.callback.CardDiscardCallback;
import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.amotassic.dabaosword.util.ModTools.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Unique MobEntity mob = (MobEntity) (Object) this;

    @Inject(method = "initEquipment", at = @At(value = "TAIL"))
    protected void initEquipment(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        if (!getWorld().isClient && new java.util.Random().nextFloat() < getChance()) initCards();
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if (getOffHandStack().getItem() == ModItems.PEACH && getHealth() <= getMaxHealth() - 5) {
            heal(5);
            voice(mob, Sounds.RECOVER);
            getOffHandStack().decrement(1);
        }
    }

    @Inject(method = "tryAttack", at = @At(value = "HEAD"))
    public void tryAttack(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (getMainHandStack().isIn(Tags.Items.CARD) && target instanceof LivingEntity) tryUseCard(getMainHandStack(), (LivingEntity) target);
    }

    @Unique
    private void tryUseCard(ItemStack stack, LivingEntity target) {
        if (stack.getItem() == ModItems.SHA) {
            if (!hasTrinket(ModItems.RATTAN_ARMOR, target)) {
                target.timeUntilRegen = 0; target.damage(mob.getDamageSources().mobAttack(mob), 5);
            }
            voice(mob, Sounds.SHA); stack.decrement(1);
        }

        if (stack.getItem() == ModItems.BINGLIANG_ITEM) {
            if (target instanceof PlayerEntity player && hasItem(player, ModItems.WUXIE)) {
                CardUsePostCallback.EVENT.invoker().cardUsePost(player, getItem(player, ModItems.WUXIE), null);
                voice(player, Sounds.WUXIE);
            } else target.addStatusEffect(new StatusEffectInstance(ModItems.BINGLIANG, StatusEffectInstance.INFINITE,1));
            voice(mob, Sounds.BINGLIANG); stack.decrement(1);
        }

        if (stack.getItem() == ModItems.TOO_HAPPY_ITEM) {
            if (target instanceof PlayerEntity player) {
                if (hasItem(player, ModItems.WUXIE)) {
                    CardUsePostCallback.EVENT.invoker().cardUsePost(player, getItem(player, ModItems.WUXIE), null);
                    voice(player, Sounds.WUXIE);
                } else player.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 5));
            } else target.addStatusEffect(new StatusEffectInstance(ModItems.TOO_HAPPY, 20 * 15));
            voice(mob, Sounds.LEBU); stack.decrement(1);
        }

        if (stack.getItem() == ModItems.DISCARD) {
            if (target instanceof PlayerEntity player) {//如果是玩家则弃牌
                if (hasItem(player, ModItems.WUXIE)) {
                    CardUsePostCallback.EVENT.invoker().cardUsePost(player, getItem(player, ModItems.WUXIE), null);
                    voice(player, Sounds.WUXIE);
                    voice(mob, Sounds.GUOHE); stack.decrement(1);
                } else {
                    List<ItemStack> candidate = new ArrayList<>();
                    //把背包中的卡牌添加到待选物品中
                    DefaultedList<ItemStack> inventory = player.getInventory().main;
                    List<Integer> cardSlots = IntStream.range(0, inventory.size()).filter(j -> isCard(inventory.get(j))).boxed().toList();
                    for (Integer slot : cardSlots) {candidate.add(inventory.get(slot));}
                    //把饰品栏的卡牌添加到待选物品中
                    int equip = 0; //用于标记装备区牌的数量
                    Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(player);
                    if(component.isPresent()) {
                        List<Pair<SlotReference, ItemStack>> allEquipped = component.get().getAllEquipped();
                        for(Pair<SlotReference, ItemStack> entry : allEquipped) {
                            ItemStack stack1 = entry.getRight();
                            if(stack1.isIn(Tags.Items.CARD)) candidate.add(stack1); equip++;
                        }
                    }
                    if(!candidate.isEmpty()) {
                        int index = new java.util.Random().nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                        player.sendMessage(Text.literal(mob.getNameForScoreboard()).append(Text.translatable("dabaosword.discard")).append(chosen.toHoverableText()));
                        CardDiscardCallback.EVENT.invoker().cardDiscard(player, chosen, 1, index > candidate.size() - equip);
                        voice(mob, Sounds.GUOHE); stack.decrement(1);
                    }
                }
            } else {//如果不是玩家则随机弃置它的主副手物品和装备
                List<ItemStack> candidate = new ArrayList<>();
                if (!target.getMainHandStack().isEmpty()) candidate.add(target.getMainHandStack());
                if (!target.getOffHandStack().isEmpty()) candidate.add(target.getOffHandStack());
                for (ItemStack armor : target.getArmorItems()) {if (!armor.isEmpty()) candidate.add(armor);}
                if(!candidate.isEmpty()) {
                    int index = new java.util.Random().nextInt(candidate.size()); ItemStack chosen = candidate.get(index);
                    chosen.decrement(1);
                    voice(mob, Sounds.GUOHE); stack.decrement(1);
                }
            }
        }

        if (stack.getItem() == ModItems.FIRE_ATTACK) {
            World world = getWorld();
            Vec3d momentum = mob.getRotationVector().multiply(3);
            FireballEntity fireballEntity = new FireballEntity(world, mob, momentum, 3);
            fireballEntity.setPosition(mob.getX(), mob.getBodyY(0.5) + 0.5, mob.getZ());
            world.spawnEntity(fireballEntity);
            voice(mob, Sounds.HUOGONG); stack.decrement(1);
        }

        if (stack.getItem() == ModItems.JIEDAO) {
            ItemStack stack1 = target.getMainHandStack();
            if (!stack1.isEmpty()) {
                if (target instanceof PlayerEntity player && hasItem(player, ModItems.WUXIE)) {
                    CardUsePostCallback.EVENT.invoker().cardUsePost(player, getItem(player, ModItems.WUXIE), null);
                    voice(player, Sounds.WUXIE);
                } else {
                    mob.setStackInHand(Hand.MAIN_HAND, stack1.copy());
                    stack1.setCount(0);
                }
                voice(mob, Sounds.JIEDAO);
            }
        }

        if (stack.getItem() == ModItems.NANMAN) {//暂时不想做，摆————

        }

        if (stack.getItem() == ModItems.WANJIAN) {
            mob.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 15,1,false,false,false));
            voice(mob, Sounds.WANJIAN); stack.decrement(1);
        }

        if (stack.getItem() == ModItems.TIESUO) {
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, true,false));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, true,false));
            voice(mob, Sounds.TIESUO); stack.decrement(1);
        }
    }

    @Unique private float getChance() {
        Difficulty difficulty = getWorld().getDifficulty();
        if (difficulty == Difficulty.EASY) return 0.3f;
        if (difficulty == Difficulty.NORMAL) return 0.6f;
        if (difficulty == Difficulty.HARD) return 0.9f;
        return 0;
    }

    @Unique
    private void initCards() {
        if (getMainHandStack().isEmpty()) setStackInHand(Hand.MAIN_HAND, new ItemStack(getMainCard(), (int)(3 * Math.random()) + 1));
        if (getOffHandStack().isEmpty()) setStackInHand(Hand.OFF_HAND, new ItemStack(getOffCard(), (int)(2 * Math.random()) + 1));
    }

    @Unique private Item getMainCard() {
        if (new java.util.Random().nextFloat() > 0.33) {
            Item[] items = {ModItems.BINGLIANG_ITEM, ModItems.TOO_HAPPY_ITEM, ModItems.DISCARD, ModItems.FIRE_ATTACK, ModItems.JIEDAO, ModItems.WANJIAN, ModItems.TIESUO};
            int index = new java.util.Random().nextInt(items.length);
            return Arrays.stream(items).toList().get(index);
        }
        return ModItems.SHA;
    }

    @Unique private Item getOffCard() {
        if (new java.util.Random().nextFloat() < 0.5) return ModItems.SHAN;
        return ModItems.PEACH;
    }
}

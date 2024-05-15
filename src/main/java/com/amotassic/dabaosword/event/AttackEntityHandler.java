package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AttackEntityHandler implements ModTools, AttackEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world instanceof ServerWorld && !player.isSpectator()) {

            if (entity instanceof LivingEntity target) {

                //破军：攻击命中盔甲槽有物品的生物后，会让其所有盔甲掉落，配合古锭刀特效使用，pvp神器
                if (hasTrinket(SkillCards.POJUN, player) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    ItemStack head = target.getEquippedStack(EquipmentSlot.HEAD);
                    ItemStack chest = target.getEquippedStack(EquipmentSlot.CHEST);
                    ItemStack legs = target.getEquippedStack(EquipmentSlot.LEGS);
                    ItemStack feet = target.getEquippedStack(EquipmentSlot.FEET);
                    if (target instanceof PlayerEntity player1) {
                        if (!head.isEmpty()) {player1.giveItemStack(head.copy());head.setCount(0);}
                        if (!chest.isEmpty()) {player1.giveItemStack(chest.copy());chest.setCount(0);}
                        if (!legs.isEmpty()) {player1.giveItemStack(legs.copy());legs.setCount(0);}
                        if (!feet.isEmpty()) {player1.giveItemStack(feet.copy());feet.setCount(0);}
                    } else {
                        if (!head.isEmpty()) {target.dropStack(head.copy());head.setCount(0);}
                        if (!chest.isEmpty()) {target.dropStack(chest.copy());chest.setCount(0);}
                        if (!legs.isEmpty()) {target.dropStack(legs.copy());legs.setCount(0);}
                        if (!feet.isEmpty()) {target.dropStack(feet.copy());feet.setCount(0);}
                    }
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.POJUN1);} else {voice(player, Sounds.POJUN2);}
                    int i = target instanceof PlayerEntity ? 200 : 40;
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, i));
                }

                if (hasTrinket(ModItems.QINGLONG, player) && player.getAttackCooldownProgress(0) >= 0.9) {
                    player.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                    player.teleport(target.getX(), target.getY(), target.getZ());
                    Vec3d momentum = player.getRotationVector().multiply(2);
                    target.setVelocity(momentum.getX(),0 ,momentum.getZ());
                }


                if (hasTrinket(ModItems.FANGTIAN, player)) {
                    //方天画戟：打中生物后触发特效，给予CD和持续时间
                    ItemStack stack = trinketItem(ModItems.FANGTIAN, player);
                    if (stack.getNbt() != null) {
                        NbtCompound nbt = new NbtCompound();
                        int cd = stack.getNbt().getInt("cd");
                        if (cd == 0) {
                            nbt.putInt("time", 100); nbt.putInt("cd", 400); stack.setNbt(nbt);
                            player.sendMessage(Text.translatable("dabaosword.fangtian").formatted(Formatting.RED), true);
                        }
                    }
                }

            }
        }
        return ActionResult.PASS;
    }
}

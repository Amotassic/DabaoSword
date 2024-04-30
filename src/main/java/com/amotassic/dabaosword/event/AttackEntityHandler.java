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
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AttackEntityHandler implements ModTools, AttackEntityCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world instanceof ServerWorld && !player.isSpectator()) {

            if (entity instanceof LivingEntity target) {

                //如果有杀，攻击南蛮入侵召唤的狗可杀死它
                if (getShaSlot(player) != -1 && target instanceof WolfEntity dog && dog.hasStatusEffect(ModItems.INVULNERABLE)) {
                    if (dog.getOwner() != player) {
                        ItemStack stack = shaStack(player);
                        dog.setHealth(0); benxi(player);
                        if (stack.getItem() == ModItems.SHA) voice(player, Sounds.SHA);
                        if (stack.getItem() == ModItems.FIRE_SHA) voice(player, Sounds.SHA_FIRE);
                        if (stack.getItem() == ModItems.THUNDER_SHA) voice(player, Sounds.SHA_THUNDER);
                        if (!player.isCreative()) {stack.decrement(1);}
                    }
                }

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
                    player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 100));
                }
            }
        }
        return ActionResult.PASS;
    }
}

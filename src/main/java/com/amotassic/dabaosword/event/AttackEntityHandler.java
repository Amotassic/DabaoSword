package com.amotassic.dabaosword.event;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AttackEntityHandler implements ModTools, AttackEntityCallback {
    TagKey<Item> tag = Tags.Items.QUANJI;
    NbtCompound quanji = new NbtCompound();

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (world instanceof ServerWorld && !player.isSpectator()) {

            if (entity instanceof LivingEntity target) {
                //排异技能：攻击伤害增加
                if (hasItemInTag(tag, player)) {
                    ItemStack stack = stackInTag(tag, player);
                    if (stack.getNbt() != null) {
                        int quan = stack.getNbt().getInt("quanji");
                        if (quan > 0) {
                            float i = (float) player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                            entity.damage(world.getDamageSources().playerAttack(player), quan+i);
                            if (quan > 4 && entity instanceof PlayerEntity) {
                                ((PlayerEntity) entity).giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                            }
                            int quan1 = quan/2;
                            quanji.putInt("quanji", quan1); stack.setNbt(quanji);
                            float j = new Random().nextFloat();
                            if (j < 0.25) {voice(player, Sounds.PAIYI1);
                            } else if (0.25 <= j && j < 0.5) {voice(player, Sounds.PAIYI2,3);
                            } else if (0.5 <= j && j < 0.75) {voice(player, Sounds.PAIYI3);
                            } else {voice(player, Sounds.PAIYI4,3);}
                        }
                    }
                }
                //破军：攻击命中盔甲槽有物品的生物后，会让其所有盔甲掉落，配合古锭刀特效使用，pvp神器
                if (hasItem(player, SkillCards.POJUN) && !player.hasStatusEffect(ModItems.COOLDOWN)) {
                    ItemStack pojun = stackWith(SkillCards.POJUN, player);
                    if (pojun.getDamage() == 0) {
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
                        player.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN, 50,0,false,true,true));
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}

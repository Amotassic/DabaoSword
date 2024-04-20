package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class NanmanItem extends CardItem implements ModTools {
    public NanmanItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            Text a = Text.translatable("nanman.dog1");
            Text b = Text.translatable("nanman.dog2");
            Text c = Text.translatable("nanman.dog3");
            BlockPos blockPos = user.getBlockPos();
            //召唤3条狗
            WolfEntity wolf1 = new WolfEntity(EntityType.WOLF, world);
            wolf1.initialize((ServerWorldAccess) world, world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null);
            wolf1.setOwner(user);wolf1.setTamed(true);world.spawnEntity(wolf1);wolf1.setCustomName(a);
            wolf1.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20 * 20,0,false,false,false));
            wolf1.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20,1,false,false,false));
            wolf1.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20,1,false,false,false));

            WolfEntity wolf2 = new WolfEntity(EntityType.WOLF, world);
            wolf2.initialize((ServerWorldAccess) world, world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null);
            wolf2.setOwner(user);wolf2.setTamed(true);world.spawnEntity(wolf2);wolf2.setCustomName(b);
            wolf2.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20 * 20,0,false,false,false));
            wolf2.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20,1,false,false,false));
            wolf2.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20,1,false,false,false));

            WolfEntity wolf3 = new WolfEntity(EntityType.WOLF, world);
            wolf3.initialize((ServerWorldAccess) world, world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null);
            wolf3.setOwner(user);wolf3.setTamed(true);world.spawnEntity(wolf3);wolf3.setCustomName(c);
            wolf3.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20 * 20,0,false,false,false));
            wolf3.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20,1,false,false,false));
            wolf3.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20,1,false,false,false));

            if (!user.isCreative()) {user.getStackInHand(hand).decrement(1);}
            jizhi(user);
            voice(user, Sounds.NANMAN);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

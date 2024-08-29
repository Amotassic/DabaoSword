package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.event.callback.CardUsePostCallback;
import com.amotassic.dabaosword.item.ModItems;
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

import static com.amotassic.dabaosword.util.ModTools.*;

public class NanmanItem extends CardItem {
    public NanmanItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            Text[] names = {
                    Text.translatable("nanman.dog1"),
                    Text.translatable("nanman.dog2"),
                    Text.translatable("nanman.dog3")
            };
            for (Text name : names) {summonDog(world, user, name);}

            CardUsePostCallback.EVENT.invoker().cardUsePost(user, user.getStackInHand(hand), null);
            voice(user, Sounds.NANMAN);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private void summonDog(World world, PlayerEntity player, Text name) {
        BlockPos blockPos = player.getBlockPos();
        WolfEntity wolf = new WolfEntity(EntityType.WOLF, world);
        wolf.initialize((ServerWorldAccess) world, world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null);
        wolf.setOwner(player);
        wolf.setTamed(true);
        world.spawnEntity(wolf);
        wolf.setCustomName(name);
        wolf.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 20 * 20,0,false,false,false));
        wolf.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20,1,false,false,false));
        wolf.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20,1,false,false,false));
    }
}

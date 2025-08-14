package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.api.event.EndEntityTick;
import com.amotassic.dabaosword.api.event.EntityHurtCallback;
import com.amotassic.dabaosword.api.event.PlayerDeathCallback;
import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Unique PlayerEntity player = (PlayerEntity) (Object) this;

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.getCommandTags().contains("px")) this.lastAttackedTicks = 1145;
        EndEntityTick.PLAYER_EVENT.invoker().endPlayerTick(player);
    }

    @Inject(at = @At("TAIL"), method = "applyDamage")
    private void onEntityHurt(final DamageSource source, final float amount, CallbackInfo ci) {
        EntityHurtCallback.EVENT.invoker().hurtEntity(player, source, amount);
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE"), ordinal = 2)
    public boolean attack(boolean bl) {
        boolean crit = false;
        var entry = ModTools.getEntry(ModItems.CRIT, player);
        if (entry != null) crit = EnchantmentHelper.getLevel(entry, getEquippedStack(EquipmentSlot.HEAD)) > 0;
        return bl || crit;
    }

    @Inject(method = "getHurtSound", at = @At("RETURN"), cancellable = true)
    protected void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (source.isOf(ModDT.LOSEHP)) cir.setReturnValue(ModTools.getSound("dabaosword", "losehp"));
    }
}

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {

    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
        PlayerDeathCallback.EVENT.invoker().onDeath(((ServerPlayerEntity) (Object) this), damageSource);
    }

}

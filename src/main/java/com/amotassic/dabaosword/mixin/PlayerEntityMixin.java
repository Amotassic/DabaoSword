package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.api.event.EndEntityTick;
import com.amotassic.dabaosword.api.event.EntityHurtCallback;
import com.amotassic.dabaosword.api.event.PlayerDeathCallback;
import com.amotassic.dabaosword.damage_type.ModDT;
import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level level) {super(type, level);}

    @Unique Player player = (Player) (Object) this;

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.entityTags().contains("px")) this.attackStrengthTicker = 1145;
        EndEntityTick.PLAYER_EVENT.invoker().endPlayerTick(player);
    }

    @Inject(at = @At("TAIL"), method = "actuallyHurt")
    private void onEntityHurt(ServerLevel world, DamageSource source, float amount, CallbackInfo ci) {
        EntityHurtCallback.EVENT.invoker().hurtEntity(player, source, amount);
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE"), name = "criticalAttack")
    public boolean attack(boolean bl) {
        boolean crit = false;
        var entry = ModTools.getEntry(ModItems.CRIT, player);
        if (entry != null) crit =  EnchantmentHelper.getItemEnchantmentLevel(entry, getItemBySlot(EquipmentSlot.HEAD)) > 0;
        return bl || crit;
    }

    @Inject(method = "getHurtSound", at = @At("RETURN"), cancellable = true)
    protected void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (source.is(ModDT.LOSEHP)) cir.setReturnValue(ModTools.getSound("dabaosword", "losehp"));
    }
}

@Mixin(ServerPlayer.class)
abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "die", at = @At("HEAD"))
    public void onDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
        PlayerDeathCallback.EVENT.invoker().onDeath(((ServerPlayer) (Object) this), damageSource);
    }
}

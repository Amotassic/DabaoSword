package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.skillcard.SkillCards;
import com.amotassic.dabaosword.util.EntityHurtCallback;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Random;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ModTools {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Shadow public abstract PlayerInventory getInventory();

    @Shadow public abstract boolean isCreative();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract void sendMessage(Text message, boolean overlay);

    @Inject(method = "damage",at = @At("HEAD"), cancellable = true)
    private void damagemixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        if (this.getWorld() instanceof ServerWorld world) {

            if (source.getSource() instanceof LivingEntity entity) {
                //若攻击者主手没有物品，则无法击穿藤甲
                if (inrattan(this)) {
                    if (entity.getMainHandStack().isEmpty()) cir.setReturnValue(false);
                    else if (getShanSlot(this) != -1 && !this.hasStatusEffect(ModItems.COOLDOWN2)) {
                        cir.setReturnValue(false);
                        shan(this,false);//闪的额外判断
                    }
                }
            }
            //弹射物对藤甲无效
            if (source.isIn(DamageTypeTags.IS_PROJECTILE) && inrattan(this)) {cir.setReturnValue(false);}

            if (source.getSource() instanceof WolfEntity dog && dog.hasStatusEffect(ModItems.INVULNERABLE)) {
                //被南蛮入侵的狗打中可以消耗杀以免疫伤害
                if (dog.getOwner() != this) {
                    if (getShaSlot(this) != -1) {
                        ItemStack stack = shaStack(this);
                        cir.setReturnValue(false);
                        if (stack.getItem() == ModItems.SHA) voice(this, Sounds.SHA);
                        if (stack.getItem() == ModItems.FIRE_SHA) voice(this, Sounds.SHA_FIRE);
                        if (stack.getItem() == ModItems.THUNDER_SHA) voice(this, Sounds.SHA_THUNDER);
                        stack.decrement(1);
                    }
                    dog.setHealth(0);
                }
            }

            if (source.getAttacker() instanceof LivingEntity) {

                final boolean trigger = baguaTrigger(this);
                boolean hasShan = getShanSlot(this) != -1 || trigger;
                boolean shouldShan = !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !getCommandTags().contains("juedou") && hasShan && !isCreative() && !hasStatusEffect(ModItems.COOLDOWN2) && !hasStatusEffect(ModItems.INVULNERABLE) && !hasTrinket(SkillCards.LIULI, this) && !hasTrinket(ModItems.RATTAN_ARMOR, this);

                //闪的被动效果
                if (shouldShan) {
                    cir.setReturnValue(false);
                    shan(this, trigger);
                    //虽然没有因为杀而触发闪，但如果攻击者的杀处于自动触发状态，则仍会消耗
                    if (source.getSource() instanceof PlayerEntity player && getShaSlot(player) != -1) {
                        ItemStack stack = shaStack(player);
                        if (stack.getItem() == ModItems.SHA) voice(this, Sounds.SHA);
                        if (stack.getItem() == ModItems.FIRE_SHA) voice(this, Sounds.SHA_FIRE);
                        if (stack.getItem() == ModItems.THUNDER_SHA) voice(this, Sounds.SHA_THUNDER);
                        benxi(player);
                        if (!player.isCreative()) stack.decrement(1);
                    }
                }

                if (hasTrinket(ModItems.BAIYIN,this) && !this.getCommandTags().contains("baiyin")) {
                    cir.setReturnValue(false);
                    this.addCommandTag("baiyin");
                    this.damage(source, 0.4f * amount);
                    this.getCommandTags().remove("baiyin");
                }
            }

            //流离
            if (hasTrinket(SkillCards.LIULI, this) && source.getAttacker() instanceof LivingEntity attacker && hasItemInTag(Tags.Items.CARD, this) && !this.hasStatusEffect(ModItems.INVULNERABLE) && !this.isCreative()) {
                ItemStack stack = stackInTag(Tags.Items.CARD, this);
                Box box = new Box(this.getBlockPos()).expand(10);
                int i = 0;
                for (LivingEntity nearbyEntity : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity -> LivingEntity != attacker && LivingEntity != this)) {
                    if (nearbyEntity != null) {
                        cir.setReturnValue(false);
                        this.addStatusEffect(new StatusEffectInstance(ModItems.INVULNERABLE, 10,0,false,false,false));
                        this.addStatusEffect(new StatusEffectInstance(ModItems.COOLDOWN2, 10,0,false,false,false));
                        stack.decrement(1);
                        if (new Random().nextFloat() < 0.5) {voice(this, Sounds.LIULI1);} else {voice(this, Sounds.LIULI2);}
                        nearbyEntity.timeUntilRegen = 0;
                        nearbyEntity.damage(source, amount); i++; break;
                    }
                }
                //避免闪自动触发，因此在这里额外判断
                if (i == 0 && !this.hasStatusEffect(ModItems.COOLDOWN2)) {
                    final boolean trigger = baguaTrigger(this);
                    boolean hasShan = getShanSlot(this) != -1 || trigger;
                    if (hasShan) {
                        cir.setReturnValue(false);
                        shan(this, trigger);
                    }
                }
            }

        }
    }

    @Unique boolean inrattan(PlayerEntityMixin player) {
        return hasTrinket(ModItems.RATTAN_ARMOR, player);
    }

    @Unique boolean baguaTrigger(PlayerEntityMixin player) {
        return hasTrinket(ModItems.BAGUA, player) && new Random().nextFloat() < 0.5;
    }
    @Unique
    void shan(PlayerEntityMixin player, boolean bl) {
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
    Boolean hasItemInTag(TagKey<Item> tag, PlayerEntityMixin player) {
        return player.getInventory().contains(tag);
    }

    @Unique
    ItemStack stackInTag(TagKey<Item> tag, PlayerEntityMixin player) {
        PlayerInventory inv = player.getInventory();
        int i = getSlotInTag(tag, player);
        return inv.getStack(i);
    }

    @Unique
    int getSlotInTag(TagKey<Item> tag, PlayerEntityMixin player) {
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isIn(tag)) continue;
            return i;
        }
        return -1;
    }

    @Unique
    boolean hasTrinket(Item item, PlayerEntityMixin player) {
        return trinketItem(item, player) != null;
    }

    @Unique
    ItemStack trinketItem(Item item, PlayerEntityMixin player) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(player);
        if(optionalComponent.isEmpty()) return null;

        TrinketComponent component = optionalComponent.get();
        return component.getEquipped(item).stream().map(Pair::getRight).findFirst().orElse(null);
    }

    @Unique
    int getShaSlot(PlayerEntityMixin player) {
        for (int i = 0; i < 18; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isIn(Tags.Items.SHA)) continue;
            return i;
        }
        return -1;
    }

    @Unique
    ItemStack shaStack(PlayerEntityMixin player) {
        return player.getInventory().getStack(getShaSlot(player));
    }

    @Unique
    int getShanSlot(PlayerEntityMixin player) {
        for (int i = 0; i < 18; ++i) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || stack.getItem() != ModItems.SHAN) continue;
            return i;
        }
        return -1;
    }

    @Unique
    ItemStack shanStack(PlayerEntityMixin player) {
        return player.getInventory().getStack(getShanSlot(player));
    }

    @Unique
    void benxi(PlayerEntityMixin player) {
        if (hasTrinket(SkillCards.BENXI, player)) {
            ItemStack stack = trinketItem(SkillCards.BENXI, player);
            NbtCompound nbt = new NbtCompound();
            if (stack.getNbt() != null) {
                int benxi = stack.getNbt().getInt("benxi");
                if (benxi < 5) {
                    nbt.putInt("benxi", benxi + 1); stack.setNbt(nbt);
                    if (new Random().nextFloat() < 0.5) {voice(player, Sounds.BENXI1);} else {voice(player, Sounds.BENXI2);}
                }
            }
        }
    }

    @Unique
    void voice(PlayerEntityMixin player, SoundEvent sound) {
        if (player.getWorld() instanceof ServerWorld world) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }
    }

    @Inject(at = @At("TAIL"), method = "applyDamage", cancellable = true)
    private void onEntityHurt(final DamageSource source, final float amount, CallbackInfo ci) {
        ActionResult result = EntityHurtCallback.EVENT.invoker().hurtEntity((PlayerEntity) (Object) this, source,
                amount);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}

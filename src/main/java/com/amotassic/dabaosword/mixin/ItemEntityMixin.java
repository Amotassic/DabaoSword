package com.amotassic.dabaosword.mixin;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.item.tool.LetMeCCItem;
import com.amotassic.dabaosword.util.ModTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {super(type, world);}

    @Shadow public abstract ItemStack getStack();

    @Shadow public abstract void setStack(ItemStack stack);

    @Shadow public abstract void resetPickupDelay();

    @Unique ItemEntity thisItem = (ItemEntity) (Object) this;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (ModTools.isCard(this.getStack())) this.resetPickupDelay();

        ItemStack stack = this.getStack();
        var entity = LetMeCCItem.getClosestEntity(thisItem, Entity.class, 0.2, e -> e != thisItem);
        if (stack.isOf(Items.ARROW) && stack.getCount() == 64) {
            if (entity instanceof ItemEntity item && item.getStack().isOf(Items.BOW)) {
                item.setStack(new ItemStack(ModItems.ARROW_RAIN));
                this.discard();
            }
        }

        if (stack.isOf(Items.EMERALD) && stack.getCount() == 64) {
            if (entity instanceof VillagerEntity villager && villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                this.setStack(new ItemStack(ModItems.GIFTBOX, 1));
            }
        }
    }
}

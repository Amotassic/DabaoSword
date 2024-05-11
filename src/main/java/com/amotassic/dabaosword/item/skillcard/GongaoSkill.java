package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class GongaoSkill extends SkillItem implements ModTools {
    public GongaoSkill(Settings settings) {super(settings);}

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) gainMaxHp(entity, 0);
    }

    private int tick = 0;
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient) {
            int extraHP = stack.getNbt() != null ? stack.getNbt().getInt("extraHP") : 0;

            gainMaxHp(entity, extraHP);
            if (++tick >= 600) { // 每30s触发扣体力上限
                tick = 0;
                if (entity instanceof PlayerEntity player) {
                    if (extraHP >= 5 && !player.isCreative() && !player.isSpectator()) {
                        player.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 2));
                        NbtCompound nbt = new NbtCompound();
                        nbt.putInt("extraHP", extraHP - 5); stack.setNbt(nbt);
                        voice(player, Sounds.WEIZHONG);
                    }
                }
            }
        }
        super.tick(stack, slot, entity);
    }
}

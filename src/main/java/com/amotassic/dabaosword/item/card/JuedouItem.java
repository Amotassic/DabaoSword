package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.amotassic.dabaosword.util.Tags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.*;

public class JuedouItem extends CardItem {
    public JuedouItem(Settings settings) {super(settings);}

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && selected && entity instanceof PlayerEntity player) {
            player.addStatusEffect(new StatusEffectInstance(ModItems.REACH, 10,114,false,false,false));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && hand == Hand.MAIN_HAND && !entity.isDead()) {
            user.addCommandTag("juedou");
            if (entity instanceof PlayerEntity player && hasItem(player, ModItems.WUXIE)) {
                removeItem(player, ModItems.WUXIE);
                jizhi(player); benxi(player);
                voice(player, Sounds.WUXIE);
            } else {
                if (entity instanceof PlayerEntity target) {
                    TagKey<Item> tag = Tags.Items.SHA;
                    int userSha = count(user, tag);
                    int targetSha = count(target, tag);
                    if (userSha >= targetSha) { target.timeUntilRegen = 0;
                        target.damage(user.getDamageSources().sonicBoom(user),5f);
                        target.sendMessage(Text.literal(user.getNameForScoreboard()).append(Text.translatable("dabaosword.juedou2")));
                    } else { user.timeUntilRegen = 0;
                        user.damage(target.getDamageSources().sonicBoom(target),5f);
                        user.sendMessage(Text.translatable("dabaosword.juedou1"));
                        if (targetSha != 0) {
                            ItemStack sha = stackInTag(tag, target);
                            sha.decrement(1);
                        }
                    }
                } else { entity.timeUntilRegen = 0;
                    entity.damage(user.getDamageSources().sonicBoom(user),5f);}
            }
            if (!user.isCreative()) {stack.decrement(1);}
            jizhi(user); benxi(user);
            voice(user, Sounds.JUEDOU);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}

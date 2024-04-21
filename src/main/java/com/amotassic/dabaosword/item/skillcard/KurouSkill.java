package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.ModTools;
import com.amotassic.dabaosword.util.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class KurouSkill extends SkillItem implements ModTools {
    public KurouSkill(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (user.getHealth() > 4.99) {
                if (!user.isCreative()) user.setHealth(user.getHealth()-4.99f);
                user.giveItemStack(new ItemStack(ModItems.GAIN_CARD, 3));
                if (new Random().nextFloat() < 0.5) {
                    voice(user, Sounds.KUROU1);
                } else {voice(user, Sounds.KUROU2);}
            } else {user.sendMessage(Text.translatable("item.dabaosword.kurou.tip").formatted(Formatting.RED), true);}
        }
        return super.use(world, user, hand);
    }
}

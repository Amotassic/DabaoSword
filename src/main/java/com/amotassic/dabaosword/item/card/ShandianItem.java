package com.amotassic.dabaosword.item.card;

import com.amotassic.dabaosword.item.ModItems;
import com.amotassic.dabaosword.util.Sounds;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import static com.amotassic.dabaosword.util.ModTools.cardUsePre;
import static com.amotassic.dabaosword.util.ModTools.voice;

public class ShandianItem extends CardItem {
    public ShandianItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && hand == Hand.MAIN_HAND) {
            if (cardUsePre(user, user.getMainHandStack(), user)) return TypedActionResult.success(user.getMainHandStack());
        }
        return super.use(world, user, hand);
    }

    @Override
    public void cardUse(LivingEntity user, ItemStack stack, LivingEntity target) {
        if (user.getWorld() instanceof ServerWorld world) {
            MinecraftServer server = world.getServer();
            CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
            try {
                ParseResults<ServerCommandSource> results = dispatcher.parse("weather thunder 15s", server.getCommandSource());
                dispatcher.execute(results);
            } catch (CommandSyntaxException e) {throw new RuntimeException(e);}
            //world.setWeather(0, 15, true, true);
            world.getPlayers().forEach(player -> {
                player.addStatusEffect(new StatusEffectInstance(ModItems.SHANDIAN, 299));
                if (player != user) voice(player, Sounds.SHANDIAN);
            });
            Box box = new Box(user.getBlockPos()).expand(10);
            for (LivingEntity near : world.getEntitiesByClass(LivingEntity.class, box, e -> !(e instanceof PlayerEntity))) {
                near.addStatusEffect(new StatusEffectInstance(ModItems.SHANDIAN, 299));
            }
        }
    }
}

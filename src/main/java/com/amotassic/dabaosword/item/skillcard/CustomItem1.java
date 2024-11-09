package com.amotassic.dabaosword.item.skillcard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class CustomItem1 extends SkillItem {
    public CustomItem1(Settings settings) {super(settings);}

    private static final NbtCompound nbt = new NbtCompound();

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        super.appendTooltip(stack, world, tooltip, tooltipContext);
    }

    private static NbtCompound readFromJson(Identifier file) {
        NbtCompound compound = new NbtCompound();
        Gson gson = new Gson();
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(CustomItem1.class.getResourceAsStream("/data/dabaosword/" + file.getPath())));
        JsonObject o = gson.fromJson(reader, JsonObject.class);
        compound.putInt("tooltip_lines", o.get("tooltip_lines").getAsInt());
        for (var e : o.getAsJsonArray("effects")) {
            JsonObject o1 = e.getAsJsonObject();
            NbtCompound nbt1 = new NbtCompound();
            if (o1.has("heal")) nbt1.putFloat("heal", o1.get("heal").getAsFloat());
            if (o1.has("draw")) nbt1.putInt("draw", o1.get("draw").getAsInt());
            if (o1.has("cd")) nbt1.putInt("cd", o1.get("cd").getAsInt());
            compound.put("effects", nbt1);
        }

        return compound;
    }
}

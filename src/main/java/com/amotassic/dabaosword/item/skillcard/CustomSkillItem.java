package com.amotassic.dabaosword.item.skillcard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.amotassic.dabaosword.item.ModItems.CUSTOM_SKILLS;
import static com.amotassic.dabaosword.item.ModItems.CUSTOM_SKILLS_DIR;
import static com.amotassic.dabaosword.util.ModTools.hasTrinket;

//暂时无用
public class CustomSkillItem extends SkillItem {
    private final JsonObject json;
    private final Item item;

    @SuppressWarnings("CallToPrintStackTrace")
    public CustomSkillItem(String skill_name) {
        File targetFile = new File(CUSTOM_SKILLS_DIR, skill_name + ".json");
        String fileContents = "";
        try {
            fileContents = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
        } catch (IOException e) {e.printStackTrace();}
        Gson gson = new Gson();
        json = gson.fromJson(fileContents, JsonObject.class);
        item = CUSTOM_SKILLS.get(skill_name);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        json.getAsJsonArray("tooltip").forEach(e -> tooltip.add(Text.translatable(e.getAsString())));
    }

    @Override
    public Pair<Float, Float> modifyDamage(LivingEntity target, DamageSource source, float amount) {
        if (hasTrinket(item, target)) {
            return new Pair<>(0f, -5f);
        }
        return new Pair<>(0f, 0f);
    }
}

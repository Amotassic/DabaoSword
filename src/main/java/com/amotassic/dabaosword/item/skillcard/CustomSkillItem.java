package com.amotassic.dabaosword.item.skillcard;

import com.amotassic.dabaosword.api.skill.Skill;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.amotassic.dabaosword.item.ModItems.CUSTOM_SKILLS;
import static com.amotassic.dabaosword.item.ModItems.CUSTOM_SKILLS_DIR;

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
    public void addTip(Skill skill, List<Text> tooltip) {
        json.getAsJsonArray("tooltip").forEach(e -> tooltip.add(Text.translatable(e.getAsString())));
    }

}

package com.amotassic.dabaosword.item.card.equipment;

import com.amotassic.dabaosword.api.skill.Skill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

import java.util.List;

public class Mount extends Equipment {
    public Mount(Settings settings) {super(settings);}

    public void addTip(Skill skill, List<Text> tooltip) {tooltip.add(getTip());}

    public static class Attack extends Mount {
        public Attack(Settings settings) {super(settings);}

        public int getExtraReach(LivingEntity entity, Skill skill) {return 1;}
    }

    public static class Defend extends Mount {
        public Defend(Settings settings) {super(settings);}

        public int getDefend(LivingEntity entity, Skill skill) {return 1;}
    }
}

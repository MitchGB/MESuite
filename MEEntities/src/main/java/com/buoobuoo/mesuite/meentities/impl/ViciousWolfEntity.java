package com.buoobuoo.mesuite.meentities.impl;

import com.buoobuoo.mesuite.meentities.MEEntitiesPlugin;
import com.buoobuoo.mesuite.meentities.interf.CustomEntity;
import com.buoobuoo.mesuite.meentities.pathfinding.ReturnToOriginGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

public class ViciousWolfEntity extends Wolf implements CustomEntity {
    public ViciousWolfEntity(Location loc) {
        super(EntityType.WOLF, ((CraftWorld) loc.getWorld()).getHandle());
        this.setAggressive(true);
    }

    @Override
    public void registerGoals(){
        this.goalSelector.addGoal(0, new ReturnToOriginGoal(MEEntitiesPlugin.getInstance().getEntityManager(), this, this, 1.5));
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal(this, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, 4, true, false, e -> {
            if(getOrigin() == null)
                return true;

            Location origin = getOrigin().getLeft();
            int maxRange = getOrigin().getRight();

            Location wolfLoc = getBukkitEntity().getLocation();


            return origin.distance(wolfLoc) <= maxRange;
        }));
    }

    @Override
    public String entityID() {
        return "VICIOUS_WOLF";
    }

    @Override
    public String entityName() {
        return "Vicious Wolf";
    }

    @Override
    public double maxHealth() {
        return 15;
    }

    @Override
    public double damage() {
        return 2;
    }

    @Override
    public double tagOffset() {
        return -1;
    }

    @Override
    public int entityLevel() {
        return 2;
    }
}



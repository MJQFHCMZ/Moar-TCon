package com.existingeevee.moretcon.other;

import java.util.List;

import com.existingeevee.moretcon.other.gravity.GCGravityCalculator;
import com.google.common.collect.Lists;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class WorldGravityUtils {

	protected static final List<GravityModifier> CALCULATORS = Lists.newLinkedList();
	
	public static double getWorldGravitiationalAcceleration(World world, Vec3d vec) {		
		for (GravityModifier calc : CALCULATORS) {
			if (calc.isApplicable(world, vec))
				return calc.getGravitationalAcceleration(world, vec);
		}
		
		return -0.08; //vanilla default
	}
	
	static {
		if (Loader.isModLoaded("galacticraftcore")) {
			CALCULATORS.add(new GCGravityCalculator());
		}
		
		//TODO AR, glacidus. idk what other mods modify gravity to an extreme extent
	}
	
	public static interface GravityModifier {
		public abstract boolean isApplicable(World world, Vec3d vec);
		public abstract double getGravitationalAcceleration(World world, Vec3d vec);
	}
}

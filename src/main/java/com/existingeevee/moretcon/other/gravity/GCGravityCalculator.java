package com.existingeevee.moretcon.other.gravity;

import com.existingeevee.moretcon.other.WorldGravityUtils.GravityModifier;
import com.existingeevee.moretcon.other.utils.MirrorUtils;
import com.existingeevee.moretcon.other.utils.MirrorUtils.IMethod;
import com.existingeevee.moretcon.other.utils.MirrorUtils.MirrorException;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GCGravityCalculator implements GravityModifier {

	private static final Class<?> worldProvider;
	private static final IMethod<Float> getGravity;
	
	static {
		Class<?> clazz;
		try {
			clazz = Class.forName("micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider");
		} catch (ClassNotFoundException e) {
			clazz = null;
		}
		worldProvider = clazz;
		
		IMethod<Float> method;
		try {
			method = MirrorUtils.reflectMethod(worldProvider, "getGravity");
		} catch (MirrorException e) {
			method = null;
		}
		getGravity = method;
	}

	@Override
	public boolean isApplicable(World world, Vec3d vec) {
		return worldProvider != null && getGravity != null && worldProvider.isInstance(world.provider);
	}

	@Override
	public double getGravitationalAcceleration(World world, Vec3d vec) {
		return -0.08 + getGravity.invoke(world.provider);
	}

}

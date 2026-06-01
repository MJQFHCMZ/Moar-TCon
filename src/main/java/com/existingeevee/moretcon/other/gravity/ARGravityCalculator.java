package com.existingeevee.moretcon.other.gravity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.existingeevee.moretcon.other.WorldGravityUtils.GravityModifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ARGravityCalculator implements GravityModifier {

	private static final String CLASS_DIMENSION_MANAGER = "zmaster587.advancedRocketry.dimension.DimensionManager";
	private static final String CLASS_GRAVITY_HANDLER = "zmaster587.advancedRocketry.util.GravityHandler";
	private static final String CLASS_WORLD_PROVIDER_SPACE = "zmaster587.advancedRocketry.world.provider.WorldProviderSpace";
	private static final String CLASS_PLANETARY_PROVIDER = "zmaster587.advancedRocketry.api.IPlanetaryProvider";

	private static Class<?> dimensionManagerClass;
	private static Class<?> gravityHandlerClass;
	private static Class<?> worldProviderSpaceClass;
	private static Class<?> planetaryProviderClass;

	private static Object dimensionManagerInstance;

	private static Method getInstanceMethod;
	private static Method isDimensionCreatedMethod;
	private static Method getDimensionPropertiesMethod;
	private static Method getGravitationalMultiplierMethod;
	private static Method isOtherEntityMethod;

	private static Field gravitationalMultiplierField;
	private static Field otherOffsetField;
	private static Field throwableOffsetField;
	private static Field arrowOffsetField;
	private static Field fluidLivingOffsetField;
	private static Field livingOffsetField;

	private static boolean initialized;
	private static boolean available;

    @SuppressWarnings("rawtypes")
	static Class gcWorldProvider;
    static Method gcGetGravity;
    
	@SuppressWarnings("unchecked")
	@Override
	public boolean isApplicable(Entity e, World world, Vec3d vec) {
		if (!init()) {
			return false;
		}

		try {
			if (gcWorldProvider != null && gcWorldProvider.isAssignableFrom(world.provider.getClass())) {
				return true;
			}
			
			int dim = world.provider.getDimension();
			
			boolean created = (Boolean) isDimensionCreatedMethod.invoke(dimensionManagerInstance, dim);
			boolean spaceProvider = worldProviderSpaceClass.isInstance(world.provider);

			return created || spaceProvider;
		} catch (Throwable t) {
			return false;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public double getGravitationalAcceleration(Entity e, World world, Vec3d vec) {
		if (!init()) {
			return -0.08;
		}

        if (gcWorldProvider != null && gcWorldProvider.isAssignableFrom(e.world.provider.getClass())) {
            try {
            	return -0.08 - (getDouble(livingOffsetField) - (float) gcGetGravity.invoke(world.provider));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException er) {
    			return -0.08;
            }
        }
		
		try {
			double gravMult;

			if (planetaryProviderClass.isInstance(world.provider)) {
				gravMult = ((Number) getGravitationalMultiplierMethod.invoke(world.provider, new BlockPos(vec))).doubleValue();
			} else {
				Object props = getDimensionPropertiesMethod.invoke(dimensionManagerInstance, world.provider.getDimension());
				gravMult = gravitationalMultiplierField.getDouble(props);
			}

			double change = -0.08;

			if (e instanceof EntityItem) {
				change -= gravMult * getDouble(otherOffsetField) - getDouble(otherOffsetField);
			} else if (isOtherEntity(e)) {
				change -= gravMult * getDouble(otherOffsetField) - getDouble(otherOffsetField);
			} else if (e instanceof EntityThrowable) {
				change -= gravMult * getDouble(throwableOffsetField) - getDouble(throwableOffsetField);
			} else if (e instanceof EntityArrow) {
				change -= gravMult * getDouble(arrowOffsetField) - getDouble(arrowOffsetField);
			} else if (e instanceof EntityLivingBase && (e.isInWater() || e.isInLava())) {
				change -= gravMult * getDouble(fluidLivingOffsetField) - getDouble(fluidLivingOffsetField);
			} else if (e instanceof EntityLivingBase) {
				change -= gravMult * getDouble(livingOffsetField) - getDouble(livingOffsetField);
			}

			return change;
		} catch (Throwable t) {
			return -0.08;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static boolean init() {
		if (initialized) {
			return available;
		}

		initialized = true;

		try {
			dimensionManagerClass = Class.forName(CLASS_DIMENSION_MANAGER);
			gravityHandlerClass = Class.forName(CLASS_GRAVITY_HANDLER);
			worldProviderSpaceClass = Class.forName(CLASS_WORLD_PROVIDER_SPACE);
			planetaryProviderClass = Class.forName(CLASS_PLANETARY_PROVIDER);

			getInstanceMethod = dimensionManagerClass.getMethod("getInstance");
			dimensionManagerInstance = getInstanceMethod.invoke(null);

			isDimensionCreatedMethod = dimensionManagerClass.getMethod("isDimensionCreated", int.class);
			getDimensionPropertiesMethod = dimensionManagerClass.getMethod("getDimensionProperties", int.class);

			getGravitationalMultiplierMethod = planetaryProviderClass.getMethod("getGravitationalMultiplier", BlockPos.class);
			isOtherEntityMethod = gravityHandlerClass.getMethod("isOtherEntity", Entity.class);

			otherOffsetField = gravityHandlerClass.getField("OTHER_OFFSET");
			throwableOffsetField = gravityHandlerClass.getField("THROWABLE_OFFSET");
			arrowOffsetField = gravityHandlerClass.getField("ARROW_OFFSET");
			fluidLivingOffsetField = gravityHandlerClass.getField("FLUID_LIVING_OFFSET");
			livingOffsetField = gravityHandlerClass.getField("LIVING_OFFSET");

			Object props = getDimensionPropertiesMethod.invoke(dimensionManagerInstance, 0);
			gravitationalMultiplierField = props.getClass().getField("gravitationalMultiplier");

			available = true;
		} catch (Throwable t) {
			available = false;
		}
	
        try {
            gcWorldProvider = Class.forName("micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider");
            gcGetGravity = gcWorldProvider.getMethod("getGravity");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            gcWorldProvider = null;
        }

		return available;
	}

	private static boolean isOtherEntity(Entity e) {
		try {
			return (Boolean) isOtherEntityMethod.invoke(null, e);
		} catch (Throwable t) {
			return false;
		}
	}

	private static double getDouble(Field field) throws IllegalAccessException {
		return ((Number) field.get(null)).doubleValue();
	}
}	
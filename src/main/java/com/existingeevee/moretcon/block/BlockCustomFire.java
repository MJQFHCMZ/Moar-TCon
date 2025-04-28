package com.existingeevee.moretcon.block;

import java.util.Random;
import java.util.function.Consumer;

import com.existingeevee.moretcon.other.fires.CustomFireEffect;
import com.existingeevee.moretcon.other.fires.CustomFireHelper;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO custom burning properties
//IE if i want it to burn special blocks or custom burning sustained blocks
//Example would be having spirit fire being able to burn bone blocks
public class BlockCustomFire extends BlockFire {

	DamageSource source = DamageSource.IN_FIRE;
	float damage = 1;
	boolean bypassFireImmunity = false;

	Consumer<EntityLivingBase> customEffect = null;

	final CustomFireEffect eff;

	public BlockCustomFire(String itemName, CustomFireEffect eff) {
		setUnlocalizedName(MiscUtils.createNonConflictiveName(itemName.toLowerCase()));
		this.eff = eff;
		if (eff.isFullbright()) {
			this.setLightLevel(1f);
		}
	}
	
	public DamageSource getDamageSource() {
		return source;
	}
 //Minecraft
	public BlockCustomFire setSource(DamageSource ds) {
		this.source = ds;
		return this;
	}

	public float getDamage() {
		return damage;
	}

	public BlockCustomFire setDamage(float damage) {
		this.damage = damage;
		return this;
	}

	public boolean bypassesFireImmunity() {
		return bypassFireImmunity;
	}

	public BlockCustomFire setCustomEffect(Consumer<EntityLivingBase> customEffect) {
		this.customEffect = customEffect;
		return this;
	}

	public BlockCustomFire setBypassFireImmunity(boolean bypassFireImmunity) {
		this.bypassFireImmunity = bypassFireImmunity;
		return this;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
			return;
		}

		if (!entity.isImmuneToFire() || bypassFireImmunity) {
			if (!(entity instanceof EntityLivingBase)) {
				entity.attackEntityFrom(DamageSource.IN_FIRE, damage);
				entity.setFire(8);
				return;
			}

			if (customEffect == null) {
				entity.attackEntityFrom(source, damage);
			} else {
				customEffect.accept((EntityLivingBase) entity);
			}
			CustomFireHelper.setAblaze((EntityLivingBase) entity, eff, 8 * 20);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(24) == 0) {
			worldIn.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		}

		if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && !this.canCatchFire(worldIn, pos.down(), EnumFacing.UP)) {
			if (this.canCatchFire(worldIn, pos.west(), EnumFacing.EAST)) {
				for (int j = 0; j < 2; ++j) {
					double d3 = (double) pos.getX() + rand.nextDouble() * 0.10000000149011612D;
					double d8 = (double) pos.getY() + rand.nextDouble();
					double d13 = (double) pos.getZ() + rand.nextDouble();
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
				}
			}

			if (this.canCatchFire(worldIn, pos.east(), EnumFacing.WEST)) {
				for (int k = 0; k < 2; ++k) {
					double d4 = (double) (pos.getX() + 1) - rand.nextDouble() * 0.10000000149011612D;
					double d9 = (double) pos.getY() + rand.nextDouble();
					double d14 = (double) pos.getZ() + rand.nextDouble();
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
				}
			}

			if (this.canCatchFire(worldIn, pos.north(), EnumFacing.SOUTH)) {
				for (int l = 0; l < 2; ++l) {
					double d5 = (double) pos.getX() + rand.nextDouble();
					double d10 = (double) pos.getY() + rand.nextDouble();
					double d15 = (double) pos.getZ() + rand.nextDouble() * 0.10000000149011612D;
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
				}
			}

			if (this.canCatchFire(worldIn, pos.south(), EnumFacing.NORTH)) {
				for (int i1 = 0; i1 < 2; ++i1) {
					double d6 = (double) pos.getX() + rand.nextDouble();
					double d11 = (double) pos.getY() + rand.nextDouble();
					double d16 = (double) (pos.getZ() + 1) - rand.nextDouble() * 0.10000000149011612D;
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
				}
			}

			if (this.canCatchFire(worldIn, pos.up(), EnumFacing.DOWN)) {
				for (int j1 = 0; j1 < 2; ++j1) {
					double d7 = (double) pos.getX() + rand.nextDouble();
					double d12 = (double) (pos.getY() + 1) - rand.nextDouble() * 0.10000000149011612D;
					double d17 = (double) pos.getZ() + rand.nextDouble();
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
				}
			}
		} else {
			for (int i = 0; i < 3; ++i) {
				double d0 = (double) pos.getX() + rand.nextDouble();
				double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
				double d2 = (double) pos.getZ() + rand.nextDouble();
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	public CustomFireEffect getFireEffect() {
		return eff;
	}
}

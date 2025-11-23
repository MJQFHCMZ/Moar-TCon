package com.existingeevee.moretcon.traits.traits;

import com.existingeevee.moretcon.entity.entities.EntityPlasmaBolt;
import com.existingeevee.moretcon.other.utils.MiscUtils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class PlasmaMissiles extends AbstractTrait {

	public PlasmaMissiles() {
		super(MiscUtils.createNonConflictiveName("plasma_missiles"), 0);
	}

	@Override
	public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
		if (!wasHit || damageDealt < 0.025) {
			return;
		}

		float cooledAttack = 1;

		if (player instanceof EntityPlayer) {
			cooledAttack = ((EntityPlayer) player).getCooledAttackStrength(0.5f);
		}

		float damage = Math.max(2, ToolHelper.getActualAttack(tool) * 0.5f);

		if (tool.getItem() instanceof ToolCore) {
			if (damage > damageDealt * 0.75) { //decently powerful tool
				float norm = damageDealt * 0.75f;
				float excess = damage - norm;

				// a lot better scaling than what base tic gives you.
				damage = norm + (float) Math.sqrt(excess + 1) - 1;
			}
			
			//then we average it with tic's function. 
			//this is wayyyy more balanced but also still gives you freedom to do shit lol :3
			damage += ToolHelper.calcCutoffDamage(damage, ((ToolCore) tool.getItem()).damageCutoff());
			damage /= 2;
		}

		double dist = player.getPositionEyes(1f).distanceTo(target.getPositionVector());
		EntityPlasmaBolt bolt = new EntityPlasmaBolt(target.world, player, target, tool)
				.setImpactTime(Math.max(15, (int) dist))
				.setDamage(damage * (float) Math.sqrt(cooledAttack)); // you use sqrt of attack cooldown so its better for rapiers and such

		bolt.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		target.world.spawnEntity(bolt);
	}
}

package com.existingeevee.moretcon.inits;

import com.existingeevee.moretcon.reforges.AbstractReforge;
import com.existingeevee.moretcon.reforges.reforges.BasicReforge;
import com.existingeevee.moretcon.reforges.reforges.ConsistantReforge;
import com.existingeevee.moretcon.reforges.reforges.HeavyReforge;
import com.existingeevee.moretcon.reforges.reforges.SharpenedReforge;
import com.mojang.realmsclient.gui.ChatFormatting;

public class ModReforges {

	public static AbstractReforge reforgeHeavy;
	public static AbstractReforge reforgeSharpened;
	public static AbstractReforge reforgeConsistant;
		
	public static AbstractReforge reforgeKeen;
	public static AbstractReforge reforgeSuperior;
	public static AbstractReforge reforgeForceful;
	public static AbstractReforge reforgeBroken;
	public static AbstractReforge reforgeDamaged;
	public static AbstractReforge reforgeShoddy;
	public static AbstractReforge reforgeHurtful;
	public static AbstractReforge reforgeStrong;
	public static AbstractReforge reforgeUnpleasant;
	public static AbstractReforge reforgeWeak;
	public static AbstractReforge reforgeRuthless;
	public static AbstractReforge reforgeGodly;
	public static AbstractReforge reforgeDemonic;
	public static AbstractReforge reforgeZealous;
	
	public static AbstractReforge reforgeQuick;
	public static AbstractReforge reforgeDeadly;
	public static AbstractReforge reforgeAgile;
	public static AbstractReforge reforgeNimble;
	public static AbstractReforge reforgeMurderous;
	public static AbstractReforge reforgeSlow;
	public static AbstractReforge reforgeSluggish;
	public static AbstractReforge reforgeLazy;
	public static AbstractReforge reforgeAnnoying;
	public static AbstractReforge reforgeNasty;

	public static AbstractReforge reforgeLarge;
	public static AbstractReforge reforgeMassive;
	public static AbstractReforge reforgeDangerous;
	public static AbstractReforge reforgeSavage;
	public static AbstractReforge reforgeSharp;
	public static AbstractReforge reforgePointy;
	public static AbstractReforge reforgeTiny;
	public static AbstractReforge reforgeTerrible;
	public static AbstractReforge reforgeSmall;
	public static AbstractReforge reforgeDull;
	public static AbstractReforge reforgeUnhappy;
	public static AbstractReforge reforgeBulky;
	public static AbstractReforge reforgeShameful;
	public static AbstractReforge reforgeWeighted;
	public static AbstractReforge reforgeLightweight;
	public static AbstractReforge reforgeLegendary;
	
	public static AbstractReforge reforgeSighted;
	public static AbstractReforge reforgeRapid;
	public static AbstractReforge reforgeHasty;
	public static AbstractReforge reforgeIntimidating;
	public static AbstractReforge reforgeFatal;
	public static AbstractReforge reforgeStaunch;
	public static AbstractReforge reforgeAwful;
	public static AbstractReforge reforgeLethargic;
	public static AbstractReforge reforgeAwkward;
	public static AbstractReforge reforgePowerful;
	public static AbstractReforge reforgeFrenzying;
	public static AbstractReforge reforgeUnreal;

	
	public static AbstractReforge reforgePlaceholder;
	
	public static void init() {
		reforgeHeavy = new HeavyReforge();
		reforgeSharpened = new SharpenedReforge();
		reforgeConsistant = new ConsistantReforge();
		
		reforgeBroken = new BasicReforge("reforgebroken", 0x422727).withKnockback(-0.2f).withDamage(-0.3f).setRandomReforge(1);
		reforgeShoddy = new BasicReforge("reforgeshoddy", 0x422727).withKnockback(-0.15f).withDamage(-0.1f).setRandomReforge(1);
		reforgeDamaged = new BasicReforge("reforgedamaged", 0xc25f5f).withDamage(-0.15f).setRandomReforge(1);
		reforgeWeak = new BasicReforge("reforgeweak", 0xc25f5f).withKnockback(-0.2f).setRandomReforge(1);
		reforgeKeen = new BasicReforge("reforgekeen", 0xffffff).withCritChance(0.03f).setRandomReforge(1);
		reforgeForceful = new BasicReforge("reforgeforceful", 0xffffff).withKnockback(0.15f).setRandomReforge(1);
		reforgeHurtful = new BasicReforge("reforgehurtful", 0xffffff).withDamage(0.1f).setRandomReforge(1);
		reforgeStrong = new BasicReforge("reforgestrong", 0xffffff).withKnockback(0.15f).setRandomReforge(1);
		reforgeRuthless = new BasicReforge("reforgeruthless", 0xffffff).withDamage(0.175f).withKnockback(-0.1f).setRandomReforge(1);
		reforgeZealous = new BasicReforge("reforgezealous", 0xffffff).withCritChance(0.05f).setRandomReforge(1);
		reforgeForceful = new BasicReforge("reforgesuperior", 0xc6bdff).withCritChance(0.03f).withKnockback(0.1f).withDamage(0.1f).setRandomReforge(1);
		reforgeUnpleasant = new BasicReforge("reforgeunpleasant", 0xc6bdff).withDamage(0.05f).withKnockback(0.15f).setRandomReforge(1);
		reforgeGodly = new BasicReforge("reforgegodly", 0xc6bdff).withDamage(0.15f).withCritChance(0.05f).withKnockback(0.15f).setRandomReforge(1);
		reforgeDemonic = new BasicReforge("reforgedemonic", 0xc6bdff).withDamage(0.15f).withCritChance(0.05f).setRandomReforge(1);
		reforgeAnnoying = new BasicReforge("reforgeannoying", 0x422727).withSpeed(-0.15f).withDamage(-0.2f).setRandomReforge(1);
		reforgeSlow = new BasicReforge("reforgeslow", 0xc25f5f).withSpeed(-0.15f).setRandomReforge(1);
		reforgeSluggish = new BasicReforge("reforgesluggish", 0xc25f5f).withSpeed(-0.20f).setRandomReforge(1);
		reforgeLazy = new BasicReforge("reforgelazy", 0xc25f5f).withSpeed(-0.075f).setRandomReforge(1);
		reforgeQuick = new BasicReforge("reforgequick", 0xffffff).withSpeed(0.1f).setRandomReforge(1);
		reforgeAgile = new BasicReforge("reforgeagile", 0xffffff).withSpeed(0.1f).withCritChance(0.03f).setRandomReforge(1);
		reforgeNimble = new BasicReforge("reforgenimble", 0xffffff).withSpeed(0.05f).setRandomReforge(1);
		reforgeNasty = new BasicReforge("reforgenasty", 0xffffff).withSpeed(0.1f).withDamage(0.05f).withCritChance(0.02f).withKnockback(-0.1f).setRandomReforge(1);
		reforgeDeadly = new BasicReforge("reforgedeadly", 0xc6bdff).withSpeed(0.1f).withDamage(0.1f).setRandomReforge(1);
		reforgeMurderous = new BasicReforge("reforgemurderous", 0xc6bdff).withSpeed(0.06f).withDamage(0.07f).withCritChance(0.03f).setRandomReforge(1);
		reforgeTerrible = new BasicReforge("reforgeterrible", 0x422727).withTypes(true, false).withDamage(-0.15f).withCritDamage(-0.025f).withKnockback(-0.12f).setRandomReforge(1);
		reforgeUnhappy = new BasicReforge("reforgeunhappy", 0x422727).withTypes(true, false).withKnockback(-0.1f).withCritDamage(-0.025f).withSpeed(-0.1f).setRandomReforge(1);
		reforgeTiny = new BasicReforge("reforgetiny", 0xc25f5f).withTypes(true, false).withCritDamage(-0.05f).setRandomReforge(1);
		reforgeSmall = new BasicReforge("reforgedmall", 0xc25f5f).withTypes(true, false).withCritDamage(-0.02f).setRandomReforge(1);
		reforgeDull = new BasicReforge("reforgedull", 0xc25f5f).withTypes(true, false).withDamage(-0.15f).setRandomReforge(1);
		reforgeShameful = new BasicReforge("reforgeshameful", 0xc25f5f).withTypes(true, false).withDamage(-0.15f).withKnockback(-0.2f).withCritDamage(0.2f).setRandomReforge(1);
		reforgeLarge = new BasicReforge("reforgelarge", 0xffffff).withTypes(true, false).withCritDamage(0.025f).setRandomReforge(1);
		reforgeDangerous = new BasicReforge("reforgedangerous", 0xffffff).withTypes(true, false).withDamage(0.05f).withCritChance(0.02f).withCritDamage(0.0125f).setRandomReforge(1);
		reforgeSharp = new BasicReforge("reforgesharp", 0xffffff).withTypes(true, false).withDamage(0.15f).setRandomReforge(1);
		reforgePointy = new BasicReforge("reforgepointy", 0xffffff).withTypes(true, false).withDamage(0.1f).setRandomReforge(1);
		reforgeBulky = new BasicReforge("reforgebulky", 0xffffff).withTypes(true, false).withDamage(0.05f).withSpeed(-0.15f).withCritDamage(0.02f).withKnockback(0.1f).setRandomReforge(1);
		reforgeWeighted = new BasicReforge("reforgeweighted", 0xffffff).withTypes(true, false).withSpeed(-0.1f).withKnockback(0.15f).setRandomReforge(1);
		reforgeLightweight = new BasicReforge("reforgelightweight", 0xffffff).withTypes(true, false).withSpeed(0.15f).withKnockback(-0.1f).setRandomReforge(1);
		reforgeLegendary = new BasicReforge("reforgelegendary", 0xc6bdff).withTypes(true, false).withDamage(0.15f).withSpeed(0.10f).withCritChance(0.05f).withCritDamage(0.025f).withKnockback(0.15f).setRandomReforge(1);
		reforgeMassive = new BasicReforge("reforgemassive", 0xc6bdff).withTypes(true, false).withCritDamage(0.05f).setRandomReforge(1);
		reforgeSavage = new BasicReforge("reforgesavage", 0xc6bdff).withTypes(true, false).withDamage(0.1f).withKnockback(0.1f).withCritDamage(0.04f).setRandomReforge(1);

		reforgeSighted = new BasicReforge("reforgesighted", 0xffffff).withTypes(false, true).withDamage(0.1f).withCritChance(0.03f).setRandomReforge(1);
		reforgeRapid = new BasicReforge("reforgerapid", 0xc6bdff).withTypes(false, true).withSpeed(0.15f).withVelocity(0.10f).setRandomReforge(1);
		reforgeHasty = new BasicReforge("reforgehasty", 0xc6bdff).withTypes(false, true).withSpeed(0.1f).withVelocity(0.15f).setRandomReforge(1);
		reforgeIntimidating = new BasicReforge("reforgeintimidating", 0xc6bdff).withTypes(false, true).withKnockback(0.15f).withVelocity(0.05f).setRandomReforge(1);
		reforgeFatal = new BasicReforge("reforgefatal", 0xc6bdff).withTypes(false, true).withDamage(0.1f).withSpeed(0.05f).withCritChance(0.02f).withVelocity(0.05f).withKnockback(0.05f).setRandomReforge(1);
		reforgeStaunch = new BasicReforge("reforgestaunch", 0xc6bdff).withTypes(false, true).withDamage(0.1f).withKnockback(0.15f).setRandomReforge(1);
		reforgeAwful = new BasicReforge("reforgeawful", 0x422727).withTypes(false, true).withDamage(-0.15f).withVelocity(-0.10f).withKnockback(-0.1f).setRandomReforge(1);
		reforgeLethargic = new BasicReforge("reforgelethargic", 0x422727).withTypes(false, true).withVelocity(-0.10f).withSpeed(-0.15f).setRandomReforge(1);
		reforgeAwkward = new BasicReforge("reforgeawkward", 0x422727).withTypes(false, true).withKnockback(-0.20f).withSpeed(-0.1f).setRandomReforge(1);
		reforgePowerful = new BasicReforge("reforgepowerful", 0xffffff).withTypes(false, true).withDamage(0.15f).withSpeed(-0.1f).withCritChance(0.01f).setRandomReforge(1);
		reforgeFrenzying = new BasicReforge("reforgefrenzying", 0xababab).withTypes(false, true).withDamage(-0.15f).withSpeed(0.15f).setRandomReforge(1);
		reforgeUnreal = new BasicReforge("reforgeunreal", 0xc6bdff).withTypes(false, true).withDamage(0.15f).withSpeed(0.1f).withCritChance(0.05f).withVelocity(0.10f).withKnockback(0.15f).setRandomReforge(1);
		
		//reforgePowerful
		
		//?
		reforgePlaceholder = new AbstractReforge("reforgeplaceholder", 0x575757) {
			@Override
			public String getLocalizedPrefix() {
				return ChatFormatting.OBFUSCATED + "XlX|X|X";
			}
		};
	}
}

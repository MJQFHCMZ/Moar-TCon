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
	
	public static AbstractReforge reforgePlaceholder;
	
	public static void init() {
		reforgeHeavy = new HeavyReforge();
		reforgeSharpened = new SharpenedReforge();
		reforgeConsistant = new ConsistantReforge();
		
		//T-2
		reforgeBroken = new BasicReforge("reforgebroken", 0x422727).withKnockback(-0.2f).withDamage(-0.3f).setRandomReforge(1);
		reforgeShoddy = new BasicReforge("reforgeshoddy", 0x422727).withKnockback(-0.15f).withDamage(-0.1f).setRandomReforge(1);

		//T-1
		reforgeDamaged = new BasicReforge("reforgedamaged", 0xc25f5f).withDamage(-0.15f).setRandomReforge(1);
		reforgeWeak = new BasicReforge("reforgeweak", 0xc25f5f).withKnockback(-0.2f).setRandomReforge(1);
		
		//T1
		reforgeKeen = new BasicReforge("reforgekeen", 0xffffff).withCritChance(0.03f).setRandomReforge(1);
		reforgeForceful = new BasicReforge("reforgeforceful", 0xffffff).withKnockback(0.15f).setRandomReforge(1);
		reforgeHurtful = new BasicReforge("reforgehurtful", 0xffffff).withDamage(0.1f).setRandomReforge(1);
		reforgeStrong = new BasicReforge("reforgestrong", 0xffffff).withKnockback(0.15f).setRandomReforge(1);
		reforgeRuthless = new BasicReforge("reforgeruthless", 0xffffff).withDamage(0.175f).withKnockback(-0.1f).setRandomReforge(1);
		reforgeZealous = new BasicReforge("reforgezealous", 0xffffff).withCritChance(0.05f).setRandomReforge(1);
		
		//T2
		reforgeForceful = new BasicReforge("reforgesuperior", 0xc6bdff).withCritChance(0.03f).withKnockback(0.1f).withDamage(0.1f).setRandomReforge(1);
		reforgeUnpleasant = new BasicReforge("reforgeunpleasant", 0xc6bdff).withDamage(0.05f).withKnockback(0.15f).setRandomReforge(1);
		reforgeGodly = new BasicReforge("reforgegodly", 0xc6bdff).withDamage(0.15f).withCritChance(0.05f).withKnockback(0.15f).setRandomReforge(1);
		reforgeDemonic = new BasicReforge("reforgedemonic", 0xc6bdff).withDamage(0.15f).withCritChance(0.05f).setRandomReforge(1);
		
		//?
		reforgePlaceholder = new AbstractReforge("reforgeplaceholder", 0x575757) {
			@Override
			public String getLocalizedPrefix() {
				return ChatFormatting.OBFUSCATED + "XlX|X|X";
			}
		};
	}
}

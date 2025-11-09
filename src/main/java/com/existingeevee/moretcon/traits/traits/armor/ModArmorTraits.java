package com.existingeevee.moretcon.traits.traits.armor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.existingeevee.moretcon.MoreTCon;
import com.existingeevee.moretcon.traits.modifiers.ModExtraTrait2;
import com.existingeevee.moretcon.traits.traits.abst.DummyTrait;
import com.existingeevee.moretcon.traits.traits.armor.modifiers.ModExtraArmorTrait2;
import com.existingeevee.moretcon.traits.traits.armor.modifiers.ModExtraArmorTraitDisplay2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import c4.conarm.lib.ArmoryRegistry;
import c4.conarm.lib.armor.ArmorCore;
import net.minecraft.item.Item;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.traits.ITrait;

public class ModArmorTraits {
	public static Galvanized galvanized = new Galvanized();
	public static FrostburnThorns frostburnThorns = new FrostburnThorns();
	public static Overload overload = new Overload();
	public static Mutant mutant = new Mutant();
	public static Liquid liquid = new Liquid();
	public static BurningThorns burningThorns = new BurningThorns();
	public static HauntedThorns hauntingThorns = new HauntedThorns();
	public static Gravitating gravitating = new Gravitating();
	public static AegisIncantamentum aegisIncantamentum = new AegisIncantamentum();
	public static DummyTrait etherealTangibility = new DummyTrait("ethereal_tangibility_armor", 0xffffff);
	public static WarpedEcho warpedEcho = new WarpedEcho();
	public static Afterheal afterheal = new Afterheal();
	public static Overlasting overlasting = new Overlasting();
	public static Pulsating pulsating = new Pulsating();
	public static Blightshield blightshield = new Blightshield();
	public static Woeful woeful = new Woeful();
	public static Immolating immolating = new Immolating();
	public static Unmeltable unmeltable = new Unmeltable();
	public static Sapping sapping = new Sapping();
	public static PlasmaShield plasmaShield = new PlasmaShield();
	public static Fissile fissile = new Fissile();
	public static Serrated serrated = new Serrated();
	public static Evasive evasive = new Evasive(1);
	public static Evasive evasive2 = new Evasive(2);
	public static Evasive evasive3 = new Evasive(3);
	
	public static List<Modifier> collector = new ArrayList<>(); //TODO modifiers? adding this for now for extraTrait2
	
	
    static List<Modifier> polishedMods;
    static List<Modifier> extraTraitMods;
	
    private static Map<String, ModExtraArmorTrait2> extraTraitLookup = new HashMap<>();

	public static void postInit() {
		registerExtraTraitModifiers();
		if (MoreTCon.proxy.isClient()) {
			
			ModExtraArmorTraitDisplay2 extraTrait2 = new ModExtraArmorTraitDisplay2();
			ArmoryRegistry.registerModifier(extraTrait2);
			collector.add(extraTrait2);
		}
	}
    
    public static void registerExtraTraitModifiers() {
        TinkerRegistry.getAllMaterials().forEach(ModArmorTraits::registerExtraTraitModifiers);
        extraTraitMods = Lists.newArrayList(extraTraitLookup.values());
    }

    private static void registerExtraTraitModifiers(Material material) {
        ArmoryRegistry.getArmor().forEach(armor -> registerExtraTraitModifiers(material, armor));
    }

    private static void registerExtraTraitModifiers(Material material, ArmorCore armor) {
        armor.getRequiredComponents().forEach(pmt -> registerExtraTraitModifiers(material, armor, pmt));
    }

    private static void registerExtraTraitModifiers(Material material, ArmorCore armor, PartMaterialType partMaterialType) {
        partMaterialType.getPossibleParts().forEach(part -> registerExtraTraitModifiers(material, armor, partMaterialType, part));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item & IToolPart> void registerExtraTraitModifiers(Material material, ArmorCore armor, PartMaterialType partMaterialType, IToolPart armorPart) {
        if(armorPart instanceof Item) {
            Collection<ITrait> traits = partMaterialType.getApplicableTraitsForMaterial(material);
            if(!traits.isEmpty()) {
                final Collection<ITrait> traits2 = ImmutableSet.copyOf(traits);
                String identifier = ModExtraTrait2.generateIdentifier(material, traits2);
                ModExtraArmorTrait2 mod = extraTraitLookup.computeIfAbsent(identifier, id -> new ModExtraArmorTrait2(material, traits2, identifier));
                mod.addCombination(armor, (T) armorPart);
            }
        }
    }
}

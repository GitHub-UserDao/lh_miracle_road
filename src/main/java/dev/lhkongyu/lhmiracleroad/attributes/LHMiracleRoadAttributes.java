package dev.lhkongyu.lhmiracleroad.attributes;


import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.tool.AttributesNameTool;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class LHMiracleRoadAttributes {

	public static final String BURDEN_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.BURDEN;
	public static final Attribute BURDEN = create(
			BURDEN_ID,
			60,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String HEAVY_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.HEAVY;

	public static final Attribute HEAVY = create(
			HEAVY_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String RANGED_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.RANGED_DAMAGE;

	public static final Attribute RANGED_DAMAGE = create(
			RANGED_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String HEALING_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.HEALING;

	public static final Attribute HEALING = create(
			HEALING_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String HUNGER_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.HUNGER;

	public static final Attribute HUNGER = create(
			HUNGER_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String INIT_DIFFICULTY_LEVEL_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.INIT_DIFFICULTY_LEVEL;

	public static final Attribute INIT_DIFFICULTY_LEVEL = create(
			INIT_DIFFICULTY_LEVEL_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String JUMP_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.JUMP;

	public static final Attribute JUMP = create(
			JUMP_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

//	public static final String MINING_SPEED_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.MINING_SPEED;
//
//	public static final Attribute MINING_SPEED = create(
//			MINING_SPEED_ID,
//			0.0,
//			0.0,
//			Double.MAX_VALUE
//	).setSyncable(true);

	public static final String CRITICAL_HIT_RATE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ AttributesNameTool.CRITICAL_HIT_RATE;

	public static final Attribute CRITICAL_HIT_RATE = create(
			CRITICAL_HIT_RATE_ID,
			0.0,
			0.0,
			100
	).setSyncable(true);

	public static final String CRITICAL_HIT_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ AttributesNameTool.CRITICAL_HIT_DAMAGE;

	public static final Attribute CRITICAL_HIT_DAMAGE = create(
			CRITICAL_HIT_DAMAGE_ID,
			1.5,
			1.5,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String INJURED_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ AttributesNameTool.INJURED;

	public static final Attribute INJURED = create(
			INJURED_ID,
			1,
			0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String SOUL_INCREASE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ AttributesNameTool.SOUL_INCREASE;

	public static final Attribute SOUL_INCREASE = create(
			SOUL_INCREASE_ID,
			1,
			0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String DAMAGE_ADDITION_ID = "attribute.name."+LHMiracleRoad.MODID+"."+AttributesNameTool.DAMAGE_ADDITION;

	public static final Attribute DAMAGE_ADDITION = create(
			DAMAGE_ADDITION_ID,
			1,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);


	private static Attribute create(String id, double fallback, double min, double max) {
		return new RangedAttribute(
				id,
				fallback,
				min,
				max
		);
	}

	public static void register() {
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.BURDEN, BURDEN);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.HEAVY, HEAVY);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.RANGED_DAMAGE, RANGED_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.HEALING, HEALING);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.HUNGER, HUNGER);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.INIT_DIFFICULTY_LEVEL, INIT_DIFFICULTY_LEVEL);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.JUMP, JUMP);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.CRITICAL_HIT_RATE, CRITICAL_HIT_RATE);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.CRITICAL_HIT_DAMAGE, CRITICAL_HIT_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.INJURED, INJURED);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.SOUL_INCREASE, SOUL_INCREASE);
		ForgeRegistries.ATTRIBUTES.register(AttributesNameTool.DAMAGE_ADDITION, DAMAGE_ADDITION);
	}

	public static void registerPlayerAttribute(EntityAttributeModificationEvent event){
		event.add(EntityType.PLAYER, BURDEN);
		event.add(EntityType.PLAYER, HEAVY);
		event.add(EntityType.PLAYER, RANGED_DAMAGE);
		event.add(EntityType.PLAYER, HEALING);
		event.add(EntityType.PLAYER, HUNGER);
		event.add(EntityType.PLAYER, INIT_DIFFICULTY_LEVEL);
		event.add(EntityType.PLAYER, JUMP);
		event.add(EntityType.PLAYER, CRITICAL_HIT_RATE);
		event.add(EntityType.PLAYER, CRITICAL_HIT_DAMAGE);
		event.add(EntityType.PLAYER, INJURED);
		event.add(EntityType.PLAYER, SOUL_INCREASE);
		event.add(EntityType.PLAYER, DAMAGE_ADDITION);
	}
}

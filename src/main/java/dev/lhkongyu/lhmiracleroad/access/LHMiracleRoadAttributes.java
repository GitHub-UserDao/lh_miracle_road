package dev.lhkongyu.lhmiracleroad.access;


import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.tool.AttributesNameTool;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
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
	}
}

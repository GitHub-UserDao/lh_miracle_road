package dev.lhkongyu.lhmiracleroad.attributes;


import dev.lhkongyu.lhmiracleroad.LHMiracleRoad;
import dev.lhkongyu.lhmiracleroad.tool.NameTool;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class LHMiracleRoadAttributes {

	public static final String BURDEN_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.BURDEN;
	public static final Attribute BURDEN = create(
			BURDEN_ID,
			60,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String HEAVY_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.HEAVY;

	public static final Attribute HEAVY = create(
			HEAVY_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String RANGED_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.RANGED_DAMAGE;

	public static final Attribute RANGED_DAMAGE = create(
			RANGED_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String HEALING_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.HEALING;

	public static final Attribute HEALING = create(
			HEALING_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String HUNGER_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.HUNGER;

	public static final Attribute HUNGER = create(
			HUNGER_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String INIT_DIFFICULTY_LEVEL_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.INIT_DIFFICULTY_LEVEL;

	public static final Attribute INIT_DIFFICULTY_LEVEL = create(
			INIT_DIFFICULTY_LEVEL_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String JUMP_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.JUMP;

	public static final Attribute JUMP = create(
			JUMP_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String MINING_SPEED_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.MINING_SPEED;

	public static final Attribute MINING_SPEED = create(
			MINING_SPEED_ID,
			1.0,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String CRITICAL_HIT_RATE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.CRITICAL_HIT_RATE;

	public static final Attribute CRITICAL_HIT_RATE = create(
			CRITICAL_HIT_RATE_ID,
			0.0,
			0.0,
			100
	).setSyncable(true);

	public static final String CRITICAL_HIT_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.CRITICAL_HIT_DAMAGE;

	public static final Attribute CRITICAL_HIT_DAMAGE = create(
			CRITICAL_HIT_DAMAGE_ID,
			1.5,
			1.5,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String DAMAGE_REDUCTION_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.DAMAGE_REDUCTION;

	public static final Attribute DAMAGE_REDUCTION = create(
			DAMAGE_REDUCTION_ID,
			1,
			0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String SOUL_INCREASE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.SOUL_INCREASE;

	public static final Attribute SOUL_INCREASE = create(
			SOUL_INCREASE_ID,
			1,
			0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String DAMAGE_ADDITION_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.DAMAGE_ADDITION;

	public static final Attribute DAMAGE_ADDITION = create(
			DAMAGE_ADDITION_ID,
			1,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String MAGIC_DAMAGE_ADDITION_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.MAGIC_DAMAGE_ADDITION;

	public static final Attribute MAGIC_DAMAGE_ADDITION = create(
			MAGIC_DAMAGE_ADDITION_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	/**
	 * 属性伤害加成
	 */
	public static final String MAGIC_ATTRIBUTE_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.MAGIC_ATTRIBUTE_DAMAGE;

	public static final Attribute MAGIC_ATTRIBUTE_DAMAGE = create(
			MAGIC_ATTRIBUTE_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String FLAME_ATTRIBUTE_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.FLAME_ATTRIBUTE_DAMAGE;

	public static final Attribute FLAME_ATTRIBUTE_DAMAGE = create(
			FLAME_ATTRIBUTE_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String LIGHTNING_ATTRIBUTE_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.LIGHTNING_ATTRIBUTE_DAMAGE;

	public static final Attribute LIGHTNING_ATTRIBUTE_DAMAGE = create(
			LIGHTNING_ATTRIBUTE_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String DARK_ATTRIBUTE_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.DARK_ATTRIBUTE_DAMAGE;

	public static final Attribute DARK_ATTRIBUTE_DAMAGE = create(
			DARK_ATTRIBUTE_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String HOLY_ATTRIBUTE_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.HOLY_ATTRIBUTE_DAMAGE;

	public static final Attribute HOLY_ATTRIBUTE_DAMAGE = create(
			HOLY_ATTRIBUTE_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String SOUL_ATTRIBUTE_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.SOUL_ATTRIBUTE_DAMAGE;

	public static final Attribute SOUL_ATTRIBUTE_DAMAGE = create(
			SOUL_ATTRIBUTE_DAMAGE_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	/**
	 * 异常伤害
	 */
	public static final String ABNORMAL_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_DAMAGE;

	public static final Attribute ABNORMAL_DAMAGE = create(
			ABNORMAL_DAMAGE_ID,
			1,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String ABNORMAL_BLEED_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_BLEED_DAMAGE;

	public static final Attribute ABNORMAL_BLEED_DAMAGE = create(
			ABNORMAL_BLEED_DAMAGE_ID,
			1,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String ABNORMAL_FROST_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_FROST_DAMAGE;

	public static final Attribute ABNORMAL_FROST_DAMAGE = create(
			ABNORMAL_FROST_DAMAGE_ID,
			1,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String ABNORMAL_POISON_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_POISON_DAMAGE;

	public static final Attribute ABNORMAL_POISON_DAMAGE = create(
			ABNORMAL_POISON_DAMAGE_ID,
			1,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	public static final String ABNORMAL_BURN_DAMAGE_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_BURN_DAMAGE;

	public static final Attribute ABNORMAL_BURN_DAMAGE = create(
			ABNORMAL_BURN_DAMAGE_ID,
			1,
			0.0,
			Double.MAX_VALUE
	).setSyncable(true);

	/**
	 * 异常累计值
	 */
	public static final String ABNORMAL_BLEED_BUILDUP_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_BLEED_BUILDUP;
	public static final Attribute ABNORMAL_BLEED_BUILDUP = create(
			ABNORMAL_BLEED_BUILDUP_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ABNORMAL_FROST_BUILDUP_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_FROST_BUILDUP;
	public static final Attribute ABNORMAL_FROST_BUILDUP = create(
			ABNORMAL_FROST_BUILDUP_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ABNORMAL_POISON_BUILDUP_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_POISON_BUILDUP;
	public static final Attribute ABNORMAL_POISON_BUILDUP = create(
			ABNORMAL_POISON_BUILDUP_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ABNORMAL_BURN_BUILDUP_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_BURN_BUILDUP;
	public static final Attribute ABNORMAL_BURN_BUILDUP = create(
			ABNORMAL_BURN_BUILDUP_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ABNORMAL_BUILDUP_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ABNORMAL_BUILDUP;
	public static final Attribute ABNORMAL_BUILDUP = create(
			ABNORMAL_BUILDUP_ID,
			1,
			0.0,
			Double.MAX_VALUE
	);

	private static Attribute create(String id, double base, double min, double max) {
		return new RangedAttribute(id, base, min, max);
	}

	public static void register() {
		ForgeRegistries.ATTRIBUTES.register(NameTool.BURDEN, BURDEN);
		ForgeRegistries.ATTRIBUTES.register(NameTool.HEAVY, HEAVY);
		ForgeRegistries.ATTRIBUTES.register(NameTool.RANGED_DAMAGE, RANGED_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.HEALING, HEALING);
		ForgeRegistries.ATTRIBUTES.register(NameTool.HUNGER, HUNGER);
		ForgeRegistries.ATTRIBUTES.register(NameTool.INIT_DIFFICULTY_LEVEL, INIT_DIFFICULTY_LEVEL);
		ForgeRegistries.ATTRIBUTES.register(NameTool.JUMP, JUMP);
		ForgeRegistries.ATTRIBUTES.register(NameTool.CRITICAL_HIT_RATE, CRITICAL_HIT_RATE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.CRITICAL_HIT_DAMAGE, CRITICAL_HIT_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.DAMAGE_REDUCTION, DAMAGE_REDUCTION);
		ForgeRegistries.ATTRIBUTES.register(NameTool.SOUL_INCREASE, SOUL_INCREASE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.DAMAGE_ADDITION, DAMAGE_ADDITION);
		ForgeRegistries.ATTRIBUTES.register(NameTool.MINING_SPEED, MINING_SPEED);
		ForgeRegistries.ATTRIBUTES.register(NameTool.MAGIC_DAMAGE_ADDITION, MAGIC_DAMAGE_ADDITION);

		//属性伤害加成
		ForgeRegistries.ATTRIBUTES.register(NameTool.MAGIC_ATTRIBUTE_DAMAGE, MAGIC_ATTRIBUTE_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.FLAME_ATTRIBUTE_DAMAGE, FLAME_ATTRIBUTE_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.LIGHTNING_ATTRIBUTE_DAMAGE, LIGHTNING_ATTRIBUTE_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.DARK_ATTRIBUTE_DAMAGE, DARK_ATTRIBUTE_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.HOLY_ATTRIBUTE_DAMAGE, HOLY_ATTRIBUTE_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.SOUL_ATTRIBUTE_DAMAGE, SOUL_ATTRIBUTE_DAMAGE);

		//异常伤害加成
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_DAMAGE, ABNORMAL_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_BLEED_DAMAGE, ABNORMAL_BLEED_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_FROST_DAMAGE, ABNORMAL_FROST_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_POISON_DAMAGE, ABNORMAL_POISON_DAMAGE);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_BURN_DAMAGE, ABNORMAL_BURN_DAMAGE);

		//异常值
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_BLEED_BUILDUP, ABNORMAL_BLEED_BUILDUP);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_FROST_BUILDUP, ABNORMAL_FROST_BUILDUP);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_POISON_BUILDUP, ABNORMAL_POISON_BUILDUP);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_BURN_BUILDUP, ABNORMAL_BURN_BUILDUP);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ABNORMAL_BUILDUP, ABNORMAL_BUILDUP);

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
		event.add(EntityType.PLAYER, DAMAGE_REDUCTION);
		event.add(EntityType.PLAYER, SOUL_INCREASE);
		event.add(EntityType.PLAYER, DAMAGE_ADDITION);
		event.add(EntityType.PLAYER, MINING_SPEED);
		event.add(EntityType.PLAYER, MAGIC_DAMAGE_ADDITION);

		//异常伤害加成
		event.add(EntityType.PLAYER, ABNORMAL_DAMAGE);
		event.add(EntityType.PLAYER, ABNORMAL_BLEED_DAMAGE);
		event.add(EntityType.PLAYER, ABNORMAL_FROST_DAMAGE);
		event.add(EntityType.PLAYER, ABNORMAL_POISON_DAMAGE);
		event.add(EntityType.PLAYER, ABNORMAL_BURN_DAMAGE);

		//异常累计值
		event.add(EntityType.PLAYER, ABNORMAL_BUILDUP);
	}
}

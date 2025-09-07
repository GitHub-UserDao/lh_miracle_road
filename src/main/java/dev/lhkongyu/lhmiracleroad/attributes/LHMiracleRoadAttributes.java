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


	/**
	 * 攻击转换属性
	 */
	public static final String ATTACK_CONVERT_MAGIC_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ATTACK_CONVERT_MAGIC;

	public static final Attribute ATTACK_CONVERT_MAGIC = create(
			ATTACK_CONVERT_MAGIC_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ATTACK_CONVERT_FLAME_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ATTACK_CONVERT_FLAME;

	public static final Attribute ATTACK_CONVERT_FLAME = create(
			ATTACK_CONVERT_FLAME_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ATTACK_CONVERT_LIGHTNING_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ATTACK_CONVERT_LIGHTNING;

	public static final Attribute ATTACK_CONVERT_LIGHTNING = create(
			ATTACK_CONVERT_LIGHTNING_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ATTACK_CONVERT_DARK_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ATTACK_CONVERT_DARK;

	public static final Attribute ATTACK_CONVERT_DARK = create(
			ATTACK_CONVERT_DARK_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);

	public static final String ATTACK_CONVERT_HOLY_ID = "attribute.name."+LHMiracleRoad.MODID+"."+ NameTool.ATTACK_CONVERT_HOLY;

	public static final Attribute ATTACK_CONVERT_HOLY = create(
			ATTACK_CONVERT_HOLY_ID,
			0.0,
			0.0,
			Double.MAX_VALUE
	);



	private static Attribute create(String id, double base, double min, double max) {
		return new RangedAttribute(
				id,
				base,
				min,
				max
		);
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

		//攻击转换属性
		ForgeRegistries.ATTRIBUTES.register(NameTool.ATTACK_CONVERT_MAGIC, ATTACK_CONVERT_MAGIC);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ATTACK_CONVERT_FLAME, ATTACK_CONVERT_FLAME);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ATTACK_CONVERT_LIGHTNING, ATTACK_CONVERT_LIGHTNING);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ATTACK_CONVERT_DARK, ATTACK_CONVERT_DARK);
		ForgeRegistries.ATTRIBUTES.register(NameTool.ATTACK_CONVERT_HOLY, ATTACK_CONVERT_HOLY);
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

		//属性伤害加成
		event.add(EntityType.PLAYER, MAGIC_ATTRIBUTE_DAMAGE);
		event.add(EntityType.PLAYER, FLAME_ATTRIBUTE_DAMAGE);
		event.add(EntityType.PLAYER, LIGHTNING_ATTRIBUTE_DAMAGE);
		event.add(EntityType.PLAYER, DARK_ATTRIBUTE_DAMAGE);
		event.add(EntityType.PLAYER, HOLY_ATTRIBUTE_DAMAGE);

		//攻击转换属性
		event.add(EntityType.PLAYER, ATTACK_CONVERT_MAGIC);
		event.add(EntityType.PLAYER, ATTACK_CONVERT_FLAME);
		event.add(EntityType.PLAYER, ATTACK_CONVERT_LIGHTNING);
		event.add(EntityType.PLAYER, ATTACK_CONVERT_DARK);
		event.add(EntityType.PLAYER, ATTACK_CONVERT_HOLY);
	}
}

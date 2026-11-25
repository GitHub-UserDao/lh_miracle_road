package dev.lhkongyu.lhmiracleroad.client.screen.config;

import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyData;
import dev.lhkongyu.lhmiracleroad.items.trophy.TrophyEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.random.RandomGenerator;

final class ConfigEditorPageMath {
    private ConfigEditorPageMath() {}

    static int maximumDurability(int durability, int from, int to, IntToDoubleFunction multiplier) {
        for (int level = from + 1; level <= to; level++) {
            durability = (int) (durability * multiplier.applyAsDouble(level));
        }
        return durability;
    }

    record Requirement(String type, int minimum, String preset) {}

    static Requirement selectPenalty(List<Requirement> requirements, ToLongFunction<String> totalLevel,
                                     Predicate<String> hasEffects) {
        for (Requirement requirement : requirements) {
            if (totalLevel.applyAsLong(requirement.type()) < requirement.minimum() && hasEffects.test(requirement.preset())) {
                return requirement;
            }
        }
        return null;
    }

    static Map<String, Integer> sample(List<TrophyData> groups, float luck, RandomGenerator random,
                                       Function<TrophyEntry, String> rewardId, Predicate<TrophyEntry> validReward) {
        TrophyData group = draw(groups, data -> data.weight > 0 ? data.getWeight(luck) : 0, random);
        Map<String, Integer> rewards = new LinkedHashMap<>();
        if (group == null) return rewards;

        int minRolls = clamp(group.roll_count_min, 0, 256);
        int maxRolls = clamp(group.roll_count_max, minRolls, 256);
        int rolls = between(random, minRolls, maxRolls);
        List<TrophyEntry> pool = new ArrayList<>(group.entries);
        for (int roll = 0; roll < rolls && !pool.isEmpty(); roll++) {
            TrophyEntry entry = draw(pool, reward -> reward.weight > 0 ? reward.getWeight(luck) : 0, random);
            if (entry == null) break;
            String id = rewardId.apply(entry);
            if (id == null || !validReward.test(entry)) continue;
            int minCount = clamp(entry.min, 1, 4096);
            int maxCount = clamp(entry.max, minCount, 4096);
            rewards.merge(id, between(random, minCount, maxCount), Integer::sum);
            if (!group.allowRepeat) pool.removeIf(other -> id.equals(rewardId.apply(other)));
        }
        return rewards;
    }

    private static int between(RandomGenerator random, int minimum, int maximum) {
        return minimum == maximum ? minimum : random.nextInt(minimum, maximum + 1);
    }

    private static <T> T draw(List<T> items, ToIntFunction<T> weight, RandomGenerator random) {
        long total = items.stream().mapToLong(weight::applyAsInt).sum();
        if (total <= 0) return null;
        long chosen = random.nextLong(total);
        for (T item : items) {
            chosen -= weight.applyAsInt(item);
            if (chosen < 0) return item;
        }
        return null;
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(value, maximum));
    }
}

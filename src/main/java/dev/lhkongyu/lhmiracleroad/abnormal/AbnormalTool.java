package dev.lhkongyu.lhmiracleroad.abnormal;

public class AbnormalTool {

    private static final int[] EXTRA = {
            23,21,19,17,15,13,11,9,7,5
    };

    public static int getBleedMaxBuildup(float hp) {

        if (hp <= 20)
            return 100;

        int buildup = 100;

        if (hp > 30) buildup += 75;
        if (hp > 50) buildup += 50;
        if (hp > 70) buildup += 25;

        int stages = Math.max(
                0,
                (int)((hp - 80) / 20)
        );

        for (int i = 0; i < stages; i++) {
            buildup += EXTRA[Math.min(i, EXTRA.length - 1)];
        }

        return buildup;
    }
}

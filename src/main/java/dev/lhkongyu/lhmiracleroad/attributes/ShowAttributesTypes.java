package dev.lhkongyu.lhmiracleroad.attributes;

public enum ShowAttributesTypes {

    BASE(1),
    EXTRA_BASE(2),
    BASE_PERCENTAGE(3),
    EXTRA_PERCENTAGE(4);

    private final int value;

    ShowAttributesTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ShowAttributesTypes fromString(String text) {
        if (text != null) {
            for (ShowAttributesTypes type : ShowAttributesTypes.values()) {
                if (text.equalsIgnoreCase(type.name())) {
                    return type;
                }
            }
        }
        return ShowAttributesTypes.BASE;
    }

    public static ShowAttributesTypes intToAttributeType(int value) {
        return switch (value) {
            case 2 -> ShowAttributesTypes.EXTRA_BASE;
            case 3 -> ShowAttributesTypes.BASE_PERCENTAGE;
            case 4 -> ShowAttributesTypes.EXTRA_PERCENTAGE;
            default -> ShowAttributesTypes.BASE;
        };
    }
}

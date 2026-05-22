package project.sweepshare.enums;

import lombok.Getter;

@Getter
public enum CleaningStyle {
    FIXED_PER_ROOM(0),
    WEELY_ROTATION(1),
    TASK_AMOUNT(2);

    private final int value;
    CleaningStyle(int value) { this.value = value; }
    public int getValue() { return value; }
}

package project.sweepshare.enums;

import lombok.Getter;

@Getter
public enum RentStyle {
    INDIVIDUAL_CONTRACTS(0),
    SINGLE_PAYER(1);

    private final int value;
    RentStyle(int value) { this.value = value; }
    public int getValue() { return value; }
}

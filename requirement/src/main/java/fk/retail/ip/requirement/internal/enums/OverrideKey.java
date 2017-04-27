package fk.retail.ip.requirement.internal.enums;

/**
 * Created by agarwal.vaibhav on 03/03/17.
 */
public enum OverrideKey {

    SLA("sla"),
    QUANTITY("quantity"),
    APP("app"),
    SUPPLIER("supplier"),
    OVERRIDE_COMMENT("override_comment"),
    STATE("state"),
    PO_ID("po_id");

    private String key;

    OverrideKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() { return this.key;}
}

package crm.entity;

public enum Status {

    ACTIVE,
    PROPOSED,
    NEGOTIATED,
    IMPLEMENTED,
    DONE;

    public static final Status[] ALL = {ACTIVE, PROPOSED, NEGOTIATED, IMPLEMENTED, DONE};

}

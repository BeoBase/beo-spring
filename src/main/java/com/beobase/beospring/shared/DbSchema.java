package com.beobase.beospring.shared;

/**
 * Names of the database schemas. Entities state their schema explicitly,
 * for example {@code @Table(name = "users", schema = DbSchema.BEO_BASE)},
 * so there is no default schema and each constant is defined in one place.
 */
public final class DbSchema {

    public static final String BEO_BASE = "beo_base";

    private DbSchema() {
    }
}

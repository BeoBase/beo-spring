package com.beobase.beospring.app.internal;

import java.util.ArrayList;
import java.util.List;

import com.beobase.beospring.shared.DbSchema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "apps", schema = DbSchema.BEO_BASE)
@Getter
@Setter
@NoArgsConstructor
class App {

    // The id is created by the backend (NanoId), the database has no default
    @Id
    @Column(name = "id", nullable = false, length = 10)
    private String id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // One app can belong to several tags (folders), stored as a Postgres text[]
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags", nullable = false)
    private List<String> tags = new ArrayList<>();

    // Internal path (e.g. "user-input") or external URL, depending on routeType
    @Column(name = "route", nullable = false)
    private String route;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", nullable = false, length = 10)
    private RouteType routeType;
}

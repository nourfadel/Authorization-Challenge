package com.elsherbieny.bookStore.model;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(Permission.CREATE, Permission.READ, Permission.UPDATE, Permission.DELETE)),
    EDITOR(Set.of(Permission.CREATE, Permission.READ, Permission.UPDATE)),
    VIEWER(Set.of(Permission.READ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}

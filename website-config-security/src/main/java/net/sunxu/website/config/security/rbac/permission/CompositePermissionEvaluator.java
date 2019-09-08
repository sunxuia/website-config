package net.sunxu.website.config.security.rbac.permission;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;

class CompositePermissionEvaluator implements PermissionEvaluator {

    private static final PermissionEvaluator ALWAYS_PASS = (a, b, c) -> true;

    private static final PermissionEvaluator ALWAYS_REJECT = (a, b, c) -> false;

    private static final String WILDCARD = "*";

    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

    private final Map<String, PermissionEvaluator> permissionEvaluators = new HashMap<>();

    private final boolean defaultAllow;

    CompositePermissionEvaluator(@NonNull Map<String, Object> properties, boolean defaultAllow) {
        this.defaultAllow = defaultAllow;
        properties.forEach((resourceKey, value) -> {
            if (value instanceof String) {
                String str = ((String) value).trim();
                if (WILDCARD.equals(str)) {
                    permissionEvaluators.put(WILDCARD, ALWAYS_PASS);
                } else if (str.isEmpty()) {
                    permissionEvaluators.put(resourceKey, ALWAYS_REJECT);
                } else {
                    permissionEvaluators.put(resourceKey, new RolePermissionEvaluator(str));
                }
            } else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                var map = (Map<String, Object>) value;
                boolean isList = true;
                for (String mapKey : map.keySet()) {
                    if (!NUMBER_PATTERN.matcher(mapKey).matches()) {
                        isList = false;
                        break;
                    }
                }
                if (isList) {
                    var roleNames = map.values().toArray(new String[map.size()]);
                    var evaluator = new RolePermissionEvaluator(roleNames);
                    permissionEvaluators.put(resourceKey, evaluator);
                } else {
                    permissionEvaluators.put(resourceKey, new CompositePermissionEvaluator(map, defaultAllow));
                }
            } else {
                throw new RuntimeException("Resource value not known, key: " + resourceKey + ", value: " + value);
            }
        });
    }

    @Override
    public boolean isPermitted(String[] resourceNames, int index, Collection<? extends GrantedAuthority> authorities) {
        if (index == resourceNames.length) {
            return defaultAllow;
        }
        String resourceName = resourceNames[index];
        var evaluator = permissionEvaluators.get(resourceName);
        if (evaluator == null) {
            evaluator = permissionEvaluators.get(WILDCARD);
        }
        if (evaluator == null) {
            return defaultAllow;
        }
        return evaluator.isPermitted(resourceNames, index + 1, authorities);
    }
}

package net.sunxu.website.config.security.rbac;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RbacProperties {

    private boolean enabled = true;

    private boolean defaultAllow = true;

    /**
     * 特殊配置规则:
     * *:xx, 对于其它规则未匹配的, 没有顺序.
     * xx: '!role', 表示拒绝这个角色, 程序会首先检查这个.
     * xx: *, 表示接受任何角色.
     */
    private Map<String, Object> permissions = new LinkedHashMap<>();

}

package central.studio.provider;

import central.studio.provider.properties.AdminProperties;
import central.studio.provider.properties.SupervisorProperties;
import central.validation.Label;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据服务中心配置
 *
 * @author Alan Yeh
 * @since 2022/07/07
 */
@Data
@Validated
@ConfigurationProperties("studio.provider")
public class ProviderProperties {

    @NotBlank
    @Label("标识")
    private String code = "central-provider";

    @NotBlank
    @Label("密钥")
    private String key = "central-provider-key";

    /**
     * 超级管理员配置
     */
    @Valid
    private SupervisorProperties supervisor = new SupervisorProperties();

    /**
     * 普通管理员配置
     */
    @Valid
    private Map<String, AdminProperties> admins = new HashMap<>();
}

package com.nebula.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Rsa配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rsa")
public class RsaKeyConfig {

    private boolean enable = true;

    private String publicKey;

    private String privateKey;

}

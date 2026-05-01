package com.cryptic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long expirationMs = 86400000; // 24 hours default

    public String getSecret()            { return secret; }
    public long getExpirationMs()        { return expirationMs; }
    public void setSecret(String s)      { this.secret = s; }
    public void setExpirationMs(long e)  { this.expirationMs = e; }
}

package com.ababaiev.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SessionConfig {
    private byte[] sessionKey;
    private byte[] iv;
}

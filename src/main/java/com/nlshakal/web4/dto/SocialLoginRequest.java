package com.nlshakal.web4.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SocialLoginRequest {
    private String code;
    private String provider;
}
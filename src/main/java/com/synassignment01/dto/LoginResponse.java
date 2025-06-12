package com.synassignment01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a login response with token.
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    String token;
}

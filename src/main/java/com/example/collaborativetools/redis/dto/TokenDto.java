package com.example.collaborativetools.redis.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenDto {
	private final String accessToken;
	private final String refreshToken;

	public static TokenDto of(String accessToken, String refreshToken) {
		return new TokenDto(accessToken, refreshToken);
	}
}
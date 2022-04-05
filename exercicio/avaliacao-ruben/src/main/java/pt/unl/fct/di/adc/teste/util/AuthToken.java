package pt.unl.fct.di.adc.teste.util;

import java.util.UUID;

public class AuthToken {
	public static final long EXPIRATION_TIME = 1000 * 60 * 30; // 30min
	public String username;
	public String tokenID;
	public long creationData;
	public long expirationData;

	public AuthToken(String username) {
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
	}
}

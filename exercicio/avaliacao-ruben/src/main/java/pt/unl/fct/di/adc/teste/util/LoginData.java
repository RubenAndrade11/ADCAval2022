package pt.unl.fct.di.adc.teste.util;

public class LoginData {
	
	public String username;
	public String password;
	
	public LoginData() {
		
	}
	
	public LoginData(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public boolean validLogin() {
		return username != null && password != null;
	}
}

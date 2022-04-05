package pt.unl.fct.di.adc.teste.util;

public class ChangePasswordData {
	
	public String username;
	public String oldPassword;
	public String newPassword;
	
	public ChangePasswordData() {
		
	}
	
	public ChangePasswordData(String username, String oldPassword, String newPassword) {
		this.username = username;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
	
}

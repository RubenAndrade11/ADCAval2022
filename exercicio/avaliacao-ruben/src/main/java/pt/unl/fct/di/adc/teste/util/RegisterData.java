package pt.unl.fct.di.adc.teste.util;

public class RegisterData {
	public String username;
	public String password;
	public String email;
	public String name;
	public String mobilephone;
	public String phone;
	public String address;
	public String nif;
	public String privacy;
	public String role;
	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public RegisterData(String username, String password, String email, String name,
			String mobilephone, String phone, String address, String nif, String privacy) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.name = name;
		this.mobilephone = mobilephone;
		this.phone = phone;
		this.address = address;
		this.nif = nif;
		this.privacy = privacy;
		}
	
	public boolean validRegistration() {
		return username != null && password != null;
	}
}

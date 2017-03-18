package pizzaOrder.restService.model.nonActivatedUsers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "non_activated_user")
public class NonActivatedUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;	
	
	@Size(min = 6, max = 20, message = "Username length must be between 6 and 20.")
	private String username;
	
	//private String passwordConfirm; 
	
	@Size(min = 6, max = 30, message = "Password length must be between 6 and 20.")
	private String password;
	
	@NotBlank (message = "Please enter your email address.")//TODO make mail validation '...@...'
	private String mail;
	
	@Min(value = 1000000, message="Phone number must have between 7 and 9 digits")
	@Max(value = 999999999, message="Phone number must have between 7 and 9 digits")
	private int phone;
	
	@NotBlank (message = "Please select your role.")
	private String role;


	//
	//CONSTRUCTORS
	//
	public NonActivatedUser(){
		super();
	}
	
	public NonActivatedUser(Long id, String username, String password, String mail, int phone, String role) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.mail = mail;
		this.phone = phone;
		this.role = role;
	}
	
	//
	// GETTERS AND SETTERS
	//
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}

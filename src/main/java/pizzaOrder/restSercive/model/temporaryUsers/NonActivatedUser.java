package pizzaOrder.restSercive.model.temporaryUsers;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import pizzaOrder.restService.model.indent.Indent;

@Entity
@Table(name = "non_activated_user")
public class NonActivatedUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;	
	
	@Size(min = 6, max = 20, message = "Username length must be between 6 and 20.")
	private String username;
	
	@Size(min = 6, max = 30, message = "Password length must be between 6 and 20.")
	private String password;
	
	@NotBlank (message = "Please enter your email address.")
	private String mail;
	
	@Min(value = 1000000, message="Phone number must have between 7 and 9 digits")
	@Max(value = 999999999, message="Phone number must have between 7 and 9 digits")
	private int phone;
	
	@NotBlank (message = "Please select your role.")
	private String role;

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

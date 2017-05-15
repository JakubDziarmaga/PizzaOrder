package pizzaOrder.restService.model.users;

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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import pizzaOrder.restService.model.indent.Indent;

@Entity
@Table(name="user_data")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_user")
	private Long id;
	private String username;
	private String password;
	private String mail;
	private int phone;
	
	@OneToMany(mappedBy = "user", cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	private List<Indent> indent;

	private String role;	
	
	//
	//CONSTRUCTORS
	//
	public User() {
		super();
	}
	
	public User(Long id, String username, String password, String mail, int phone, String role) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.mail = mail;
		this.phone = phone;
		this.role = role;
	}
	//
	//GETTERS AND SETTERS
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
	public List<Indent> getIndent() {
		return indent;
	}
	public void setIndent(List<Indent> indent) {
		this.indent = indent;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}

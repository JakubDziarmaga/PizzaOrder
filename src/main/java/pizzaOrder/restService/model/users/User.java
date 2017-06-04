package pizzaOrder.restService.model.users;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.stars.Stars;

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

    @ManyToMany(mappedBy = "users", cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @Fetch(value = FetchMode.SELECT)
	private List<Stars> stars;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((indent == null) ? 0 : indent.hashCode());
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + phone;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((stars == null) ? 0 : stars.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (indent == null) {
			if (other.indent != null)
				return false;
		} else if (!indent.equals(other.indent))
			return false;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phone != other.phone)
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (stars == null) {
			if (other.stars != null)
				return false;
		} else if (!stars.equals(other.stars))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	//
	//CONSTRUCTORS
	//
	public User() {
		super();
	}
	
	public List<Stars> getStars() {
		return stars;
	}

	public void setStars(List<Stars> stars) {
		this.stars = stars;
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

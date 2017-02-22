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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import pizzaOrder.restService.model.indent.Indent;


	@Entity
	@Table(name = "non_activated_user")
	public class NonActivatedUser {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name="id_user")
		private Long id;
		private String username;
		private String password;
		private String mail;
		private int phone;	
		private String role;
		

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

		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
	}

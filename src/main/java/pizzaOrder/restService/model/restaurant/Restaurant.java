package pizzaOrder.restService.model.restaurant;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.menu.Menu;

@Entity
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_restaurant")
	private Long id;
	
	@Size(min = 3, max = 30, message = "Please enter your name.")
	private String name;
	
	@Size(min = 3, max = 20, message = "Please enter your city.")
	private String city;
	
	@Size(min = 3, max = 30, message = "Please enter your address.")
	private String adress;
	
	@Min(value = 1000000, message="Phone number must have between 7 and 9 digits")
	@Max(value = 999999999, message="Phone number must have between 7 and 9 digits")
	private int phone;
	
	@Column(name="id_owner")
	private Long ownerId;
	
	@OneToMany(mappedBy = "restaurant", cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
//	@JsonManagedReference
	@Fetch(value = FetchMode.SELECT)
	private List<Menu> menu;

	@OneToMany(mappedBy = "restaurant", cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	private List<Indent> indent;
	
	//
	// GETTERS AND SETTERS
	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long idOwner) {
		this.ownerId = idOwner;
	}

	public List<Menu> getMenu() {
		return menu;
	}

	public void setMenu(List<Menu> menu) {
		this.menu = menu;
	}

//	public List<Indent> getIndent() {
//		return indent;
//	}
//
//	public void setIndent(List<Indent> indent) {
//		this.indent = indent;
//	}

}

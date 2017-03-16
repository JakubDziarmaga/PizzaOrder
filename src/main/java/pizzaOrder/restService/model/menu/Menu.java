package pizzaOrder.restService.model.menu;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.restaurant.Restaurant;

@Entity

public class Menu {
	//TODO attribute name
	//TODO add validation class for each entity instead of e.g @NotEmpty adnotation
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_menu")
	private Long id;		
	
	@Min(value=0,message = "Price can't be negative.")
	@NotNull
	private Double price;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "id_restaurant")
	private Restaurant restaurant;
	
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name = "menu_ingredients", joinColumns = @JoinColumn(name = "id_menu", nullable = false),
    inverseJoinColumns = @JoinColumn(name = "id_ingredients", nullable = false))
	@Fetch(value = FetchMode.SELECT)
//	@NotEmpty(message = "Please choose at least one ingredient.")
	//TODO add after split program for client and server
	private List<Ingredients> ingredients;

	@OneToMany(mappedBy = "menu", cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SELECT)
	private List<Indent> indent;

	//
	// GETTERS AND SETTERS
	//
	public Menu() {
	}

	public Menu(Double price) {
		super();
		this.price = price;
	}
	

	public Menu(Long id, Double price, Restaurant restaurant, List<Ingredients> ingredients, List<Indent> indent) {
		super();
		this.id = id;
		this.price = price;
		this.restaurant = restaurant;
		this.ingredients = ingredients;
		this.indent = indent;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public List<Ingredients> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredients> ingredients) {
		this.ingredients = ingredients;
	}

	public List<Indent> getIndent() {
		return indent;
	}

	public void setIndent(List<Indent> indent) {
		this.indent = indent;
	}

}

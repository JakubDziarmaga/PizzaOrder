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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import pizzaOrder.restService.model.indent.Indent;
import pizzaOrder.restService.model.ingredients.Ingredients;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.size.Size;

@Entity
public class Menu {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_menu")
	private Long id;		

	@Column(name="name")
	private String nameMenu;
	
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "id_restaurant")
	private Restaurant restaurant;
	
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name = "menu_ingredients", joinColumns = @JoinColumn(name = "id_menu", nullable = false),inverseJoinColumns = @JoinColumn(name = "id_ingredients", nullable = false))
	@Fetch(value = FetchMode.SELECT)
	private List<Ingredients> ingredients;

//	@OneToMany(mappedBy = "menu", cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
//	@Fetch(value = FetchMode.SELECT)
//	private List<Indent> indent;

	@OneToMany(mappedBy = "menu", cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	@Fetch(value = FetchMode.SELECT)
	private List<Size> size;
	
	//
	//CONSTRUCTORS
	//
	public Menu() {
	}

	public Menu(Long id, Double price, Restaurant restaurant, List<Ingredients> ingredients, List<Indent> indent) {
		super();
		this.id = id;
		this.restaurant = restaurant;
		this.ingredients = ingredients;
//		this.indent = indent;
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



	public String getNameMenu() {
		return nameMenu;
	}

	public void setNameMenu(String nameMenu) {
		this.nameMenu = nameMenu;
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

//	public List<Indent> getIndent() {
//		return indent;
//	}
//
//	public void setIndent(List<Indent> indent) {
//		this.indent = indent;
//	}

	public List<Size> getSize() {
		return size;
	}

	public void setSize(List<Size> size) {
		this.size = size;
	}

}

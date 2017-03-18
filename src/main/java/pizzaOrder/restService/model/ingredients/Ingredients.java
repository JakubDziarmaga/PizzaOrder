package pizzaOrder.restService.model.ingredients;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import pizzaOrder.restService.model.menu.Menu;


@Entity
public class Ingredients {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_ingredients")
	private Long id;
	private String name;
    @ManyToMany(mappedBy = "ingredients")
    @Fetch(value = FetchMode.SELECT)
	private List<Menu> menu;
	
	
	
	
	//
	//GETTERS AND SETTERS
	//
	
	public Ingredients() {
		super();
	}

	public Ingredients(String name) {
		super();
		this.name = name;
	}
	
	public Ingredients(Long id,String name) {
		super();
		this.id = id;
		this.name = name;
	}

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

	public List<Menu> getMenu() {
		return menu;
	}

	public void setMenu(List<Menu> menu) {
		this.menu = menu;
	}
	
	

}

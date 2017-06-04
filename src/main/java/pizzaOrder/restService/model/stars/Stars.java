package pizzaOrder.restService.model.stars;

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
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;

import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Entity
public class Stars {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_stars")	
	private Long id;
	private Integer amount;
	private Double mean;	
    @OneToOne(mappedBy="stars")
	private Restaurant restaurant;
    

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinTable(name = "user_stars", joinColumns = @JoinColumn(name = "id_stars", nullable = false),inverseJoinColumns = @JoinColumn(name = "id_user", nullable = false))
	@Fetch(value = FetchMode.SELECT)
	private List<User> users;
	
	public Stars() {
		super();
		this.amount = 0;
		this.mean = 0.0;
	}
	
	public Stars(Long id, Integer amount, Double mean) {
		super();
		this.id = id;
		this.amount = amount;
		this.mean = mean;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Double getMean() {
		return mean;
	}
	public void setMean(Double mean) {
		this.mean = mean;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	} 

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public void addUser(User user){
		users.add(user);
	}
	
}

package pizzaOrder.restService.model.indent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import pizzaOrder.restService.model.menu.Menu;
import pizzaOrder.restService.model.restaurant.Restaurant;
import pizzaOrder.restService.model.users.User;

@Entity
public class Indent// extends ResourceSupport 
{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_indent")
	private Long id;
	@Column(name="is_paid")
	private boolean isPaid;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JsonBackReference
//	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "id")
	@JoinColumn(name = "id_user")
	private User user;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "id_restaurant")
	private Restaurant restaurant;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "id_menu")
	private Menu menu;
	
	
	@Column(name = "date_time")
    @Temporal(TemporalType.TIMESTAMP)
	private Date date;
	
	//TODO check if it still works
	@JsonIgnore
	public String getFormattedDate(){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return df.format(date);
	}
	
	
	//
	//GETTERS AND SETTERS
	//
	public Indent(){
		
	}
	
	public Indent(Long id, boolean isPaid, User user, Restaurant restaurant, Menu menu, Date date) {
		super();
		this.id = id;
		this.isPaid = isPaid;
		this.user = user;
		this.restaurant = restaurant;
		this.menu = menu;
		this.date = date;
	}	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public boolean isPaid() {
		return isPaid;
	}
	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Restaurant getRestaurant() {
		return restaurant;
	}
	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date dateTime) {
		this.date = dateTime;
	}	

}

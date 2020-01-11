package curso.api.rest.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Telephone implements Serializable{

	private static final long serialVersionUID = 1L;

		@Id
        @GeneratedValue(strategy = GenerationType.AUTO)	    
	    private Long id;
	    
	    private String type;
	    private String number;
	    
	    @JsonIgnore
	    @ManyToOne(optional = true, fetch = FetchType.EAGER)
	    private UserSystem userSystem;
	    
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
	    
	    public void setUserSystem(UserSystem userSystem) {
			this.userSystem = userSystem;
		}
	    
	    public UserSystem getUserSystem() {
			return userSystem;
		}
}

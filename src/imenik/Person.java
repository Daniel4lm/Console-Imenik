package imenik;

public class Person {
	
	private Integer idPerson;
    private String firstName;
    private String lastName;
    
    public Person() {
		// Default constructor
	}
    
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public Person(Integer idPerson, String firstName, String lastName) {
    	this(firstName, lastName);
    	this.idPerson = idPerson;        
    }

	public String get_firstName() {
		return firstName;
	}

	public void set__firstName(String firstName) {
		this.firstName = firstName;
	}

	public String get_lastName() {
		return lastName;
	}

	public void set_lastName(String lastName) {
		this.lastName = lastName;
	}
    
    

}

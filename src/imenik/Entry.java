package imenik;

public class Entry {
	
	private Person person;
	private String number;
	
	public Entry() {
		// default constructor
	}
	
	public Entry(Person person, String number) {
		this.person = person;
		this.number = number;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}	

}

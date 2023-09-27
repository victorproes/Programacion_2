package laboral;

public class Persona {
	public String nombre, sexo, dni;

	public Persona(String nombre, String sexo, String dni) {

		this.nombre = nombre;
		this.sexo = sexo;
		this.dni = dni;
	}

	public Persona(String nombre, String sexo) {

		this.nombre = nombre;
		this.sexo = sexo;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public void Imprime() {
		System.out.println("Nombre: " + nombre + " Sexo= " + dni + " DNI:" + sexo);
	}
}

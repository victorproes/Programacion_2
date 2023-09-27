package laboral;

public class Empleado extends Persona {
	private int categoria;
	public int anyos;

	public Empleado(String nombre, String sexo, String dni) throws DatosNoCorrectoExcpetion {
		super(nombre, sexo, dni);
		this.categoria = 1;
		this.anyos = 0;
	}

	public Empleado(String nombre, String sexo, String dni, int categoria, int anyos) throws DatosNoCorrectoExcpetion {
		super(nombre, sexo, dni);
		if (!(categoria >= 0 && categoria <= 10)) {
			throw new DatosNoCorrectoExcpetion("error la categoria tiene que ser entre 0 y 10");
		}
		if (anyos < 0) {
			throw new DatosNoCorrectoExcpetion("Error los anyos no pueden ser negativos");

		}

		this.categoria = categoria;
		this.anyos = anyos;

	}

	public int getCategoria() {
		return categoria;
	}

	public void setCategoria(int categoria) {
		this.categoria = categoria;
	}

	public void incrAnyo() {
		anyos++;
	}

	public void imprime() {
		Imprime();
		System.out.println("anyos: " + anyos + " categoria: " + categoria);
	}
}

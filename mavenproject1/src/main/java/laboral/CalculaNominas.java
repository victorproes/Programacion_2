package laboral;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CalculaNominas {
	public static void main(String[] args) throws NumberFormatException, IOException, DatosNoCorrectoExcpetion {

		try {
			// Leer empleados desde el archivo de texto "empleados.txt"

			// ESTE DA TODO BIEN
			Empleado e1 = new Empleado("James Cosling", "M", "32000032G", 4, 7);
			Empleado e2 = new Empleado("Ada Lovelace", "32000031R", "F");
			escribe(e1);
			escribe(e2);

			e2.incrAnyo();
			e1.setCategoria(9);

			System.out.println("Modificacion");
			escribe(e1);
			escribe(e2);

			// DA ERROR PORQUE LA CATEGORIA TIENE QUE SER ENTRE 0 Y 10 Y EL AÑO NO PUEDE SER
			// NEGATIVO
			/*
			 * Empleado e3 = new Empleado("James Cosling", "M", "32000032G", 11, -1);
			 * Empleado e3 = new Empleado("James Cosling", "M", "32000032G", 5, -1);
			 * 
			 * escribe(e3);
			 */

		} catch (DatosNoCorrectoExcpetion e) {
			System.out.println(e);
		}

		// Leer empleados desde el archivo de texto "empleados.txt"

		List<Empleado> listaEmpleados = new ArrayList<>();

		try {

			BufferedReader reader = new BufferedReader(new FileReader(
					"C:\\Users\\Usuario23\\eclipse-workspace\\mavenproject1\\src\\main\\java\\laboral\\empleados.txt"));
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] campos = linea.split(",");
				if (campos.length == 5) {
					String nombre = campos[0].trim();

					String dni = campos[1].trim();

					String sexo = campos[2].trim();

					int categoria = Integer.parseInt(campos[3].trim());

					int anyos = Integer.parseInt(campos[4].trim());

					Empleado empleado = new Empleado(nombre, sexo, dni, categoria, anyos);

					listaEmpleados.add(empleado);

				}

			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Calcular sueldos y escribir en el archivo binario "salarios.dat"
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(
					"C:\\Users\\Usuario23\\eclipse-workspace\\mavenproject1\\src\\main\\java\\laboral\\salarios.dat"));

			for (Empleado empleado : listaEmpleados) {

				int sueldo = Nomina.sueldo(empleado);
				String dni = empleado.dni;
				outputStream.writeUTF(dni);
				outputStream.writeInt(sueldo);

			}
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(
					"C:\\Users\\Usuario23\\eclipse-workspace\\mavenproject1\\src\\main\\java\\laboral\\salarios.dat"));
			while (true) {
				try {
					String dni = inputStream.readUTF();
					int sueldo = inputStream.readInt();
					System.out.println("DNI: " + dni + ", Sueldo: " + sueldo);
				} catch (EOFException e) {
					// Se llegó al final del archivo
					break;
				}
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String jdbcURL = "jdbc:mysql://localhost:3306/nomina";
		String usuarioDB = "root";
		String contraseñaDB = "123456";

		try {
			Connection conexion = DriverManager.getConnection(jdbcURL, usuarioDB, contraseñaDB);

			// Leer los datos de los empleados desde la tabla Empleados
			Statement statement = conexion.createStatement();
			ResultSet aumentoAños = statement
					.executeQuery("UPDATE empleados SET anyos = anyos+1 WHERE dni = '32000031R';");

			ResultSet cambioCategoria = statement
					.executeQuery("UPDATE empleados SET categoria = 9 WHERE dni ='32000032G';");

			ResultSet resultSet = statement.executeQuery("SELECT * FROM empleados;");

			while (resultSet.next()) {
				String nombre = resultSet.getString("nombre");
				String dni = resultSet.getString("dni");
				String sexo = resultSet.getString("sexo");
				int categoria = resultSet.getInt("categoria");
				int anyos = resultSet.getInt("anyos");

				// Realiza las operaciones necesarias con los datos
				System.out.println("Nombre: " + nombre + ", DNI: " + dni + ", Sexo: " + sexo + ", Categoría: "
						+ categoria + ", Años: " + anyos);
			}

			// Cierra la conexión y los recursos
			resultSet.close();
			statement.close();
			conexion.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (Empleado empleado : listaEmpleados) {
			int sueldo = Nomina.sueldo(empleado);
			actualizarNomina(empleado.dni, sueldo);

		}

		CalculaNominas.altaEmpleado(
				"C:\\Users\\Usuario23\\eclipse-workspace\\mavenproject1\\src\\main\\java\\laboral\\empleadosNuevos.txt");

		
		boolean preferirArchivosDeTexto = false; // Cambia a true si prefieres trabajar con archivos de texto

        // Ejemplo de lectura de información desde la base de datos
        List<Empleado> listaNueva = obtenerEmpleadosDesdeBD();

        // Realizar operaciones en la base de datos
        // Por ejemplo, calcular sueldos y actualizar la base de datos
        for (Empleado empleado : listaEmpleados) {
            int sueldo = Nomina.sueldo(empleado);
            actualizarNomina(empleado.dni, sueldo); // Supongamos que tienes un método para actualizar el sueldo en la base de datos
        }

        // Realizar operaciones de respaldo en archivos secundarios
        boolean respaldoExitoso = respaldarEnArchivos(listaEmpleados);

        // Si el respaldo no es exitoso o si se prefiere trabajar con los archivos de texto
        if (!respaldoExitoso || preferirArchivosDeTexto) {
            // Leer empleados desde el archivo de texto
            List<Empleado> listaEmpleadosDesdeArchivo = leerEmpleadosDesdeArchivoTexto("empleados.txt");

            // Realizar operaciones en los archivos de texto
            // Por ejemplo, calcular sueldos y guardar en archivos de respaldo
            for (Empleado empleado : listaEmpleadosDesdeArchivo) {
                int sueldo = Nomina.sueldo(empleado);
                guardarEnArchivoDeRespaldo(empleado, sueldo); // Supongamos que tienes un método para guardar en un archivo de respaldo
            }
        }
	}

	private static void escribe(Empleado e1) {
		e1.imprime();
		System.out.println("Sueldo:" + Nomina.sueldo(e1));
		System.out.println("---------------------------------------------");

	}

	private static int obtenerEmpleadoIdPorDni(String dni) {
		String url = "jdbc:mysql://localhost:3306/nomina";
		String usuario = "root";
		String contraseña = "123456";

		try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
			// Define la consulta SQL para buscar el ID del empleado por su DNI
			String sql = "SELECT id FROM empleados WHERE dni = ?";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, dni);

				// Ejecuta la consulta SQL y obtén el resultado
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						// Si se encuentra un resultado, devuelve el ID del empleado
						return rs.getInt("id");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Si no se encuentra ningún resultado o si ocurre un error, devuelve un valor
		// negativo o -1 (indicando que no se encontró el empleado)
		return -1;
	}

	private static void actualizarNomina(String dni, int sueldo) {
		String url = "jdbc:mysql://localhost:3306/nomina";
		String usuario = "root";
		String contraseña = "123456";

		try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
			// consulta SQL para insertar o actualizar el sueldo en la tabla nominas
			String sql = "INSERT INTO nominas (empleado_id, sueldo) VALUES (?, ?)"
					+ " ON DUPLICATE KEY UPDATE sueldo = VALUES(sueldo)";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				// Obtencion del ID del empleado con el metodo obtenerEmpleadoIdPorDni)
				int empleadoId = obtenerEmpleadoIdPorDni(dni);

				stmt.setInt(1, empleadoId);
				stmt.setInt(2, sueldo);

				// Ejecuta la consulta SQL para insertar o actualizar el sueldo
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Método para respaldar empleados en archivos de texto o binarios
    private static void respaldarEmpleados(String tipoRespaldo, String nombreArchivo, List<Empleado> empleados) {
        if ("texto".equalsIgnoreCase(tipoRespaldo)) {
            // Respaldo en archivo de texto
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
                for (Empleado empleado : empleados) {
                    String linea = empleado.nombre + "," + empleado.dni+ "," +
                                    empleado.sexo + "," + empleado.getCategoria() + "," +
                                    empleado.anyos;
                    writer.write(linea);
                    writer.newLine();
                }
                System.out.println("Empleados respaldados en archivo de texto: " + nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("binario".equalsIgnoreCase(tipoRespaldo)) {
            // Respaldo en archivo binario
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
                for (Empleado empleado : empleados) {
                    outputStream.writeObject(empleado);
                }
                System.out.println("Empleados respaldados en archivo binario: " + nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Tipo de respaldo no válido. Use 'texto' o 'binario'.");
        }
    }

	private static final String URL = "jdbc:mysql://localhost:3306/nomina";
	private static final String USUARIO = "root";
	private static final String CONTRASEÑA = "123456";

	

	// Método para dar de alta un empleado en la base de datos
	public static void altaEmpleado(Empleado empleado) {
		try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
			// Define la consulta SQL para insertar un nuevo empleado
			String sql = "INSERT INTO empleados (nombre, dni, sexo, categoria, anyos) VALUES (?, ?, ?, ?, ?)";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, empleado.nombre);
				stmt.setString(2, empleado.dni);
				stmt.setString(3, empleado.sexo);
				stmt.setInt(4, empleado.getCategoria());
				stmt.setInt(5, empleado.anyos);

				// Ejecuta la consulta SQL para insertar el nuevo empleado
				int filasAfectadas = stmt.executeUpdate();

				if (filasAfectadas > 0) {
					System.out.println("Empleado dado de alta con éxito.");

					// Calcula y almacena automáticamente el sueldo del empleado
					int sueldo = Nomina.sueldo(empleado);
					actualizarNomina(empleado.dni, sueldo);
				} else {
					System.out.println("No se pudo dar de alta al empleado.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Método sobrecargado para dar de alta empleados por lotes desde un archivo
	public static void altaEmpleado(String archivoEmpleados) throws DatosNoCorrectoExcpetion {
		try (BufferedReader reader = new BufferedReader(new FileReader(archivoEmpleados))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] campos = linea.split(",");
				if (campos.length == 5) {
					String nombre = campos[0].trim();
					String dni = campos[1].trim();
					String sexo = campos[2].trim();
					int categoria = Integer.parseInt(campos[3].trim());
					int anyos = Integer.parseInt(campos[4].trim());

					Empleado empleado = new Empleado(nombre, dni, sexo, categoria, anyos);
					altaEmpleado(empleado); // Llama al método de alta individual
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<Empleado> obtenerEmpleadosDesdeBD() throws DatosNoCorrectoExcpetion {
	    List<Empleado> empleados = new ArrayList<>();
	    
	    String url = "jdbc:mysql://localhost:3306/nomina";
	    String usuario = "root";
	    String contraseña = "123456";

	    try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
	        // Define la consulta SQL para obtener empleados desde la base de datos
	        String sql = "SELECT * FROM empleados";

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            // Ejecuta la consulta SQL y obtén el resultado
	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    // Para cada fila en el resultado, crea un objeto Empleado
	                    String nombre = rs.getString("nombre");
	                    String dni = rs.getString("dni");
	                    String sexo = rs.getString("sexo");
	                    int categoria = rs.getInt("categoria");
	                    int anyos = rs.getInt("anyos");

	                    Empleado empleado = new Empleado(nombre, dni, sexo, categoria, anyos);
	                    empleados.add(empleado);
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return empleados;
	}
	
	// Método para leer empleados desde un archivo de texto
    private static List<Empleado> leerEmpleadosDesdeArchivoTexto(String nombreArchivo) throws DatosNoCorrectoExcpetion {
        List<Empleado> listaEmpleados = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] campos = linea.split(",");
                if (campos.length == 5) {
                    String nombre = campos[0].trim();
                    String dni = campos[1].trim();
                    String sexo = campos[2].trim();
                    int categoria = Integer.parseInt(campos[3].trim());
                    int anyos = Integer.parseInt(campos[4].trim());

                    Empleado empleado = new Empleado(nombre, dni, sexo, categoria, anyos);
                    listaEmpleados.add(empleado);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listaEmpleados;
    }
	
	


}

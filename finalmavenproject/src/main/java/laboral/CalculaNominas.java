package laboral;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CalculaNominas {
	public static void main(String[] args) throws DatosNoCorrectoExcpetion {

		/*
		 * 1.2 Actualizar dicho fichero “empleados.txt” conforme a los cambios
		 * especificados en el apartado 4.5.
		 */
		ActualizarEmpleadosTxt();

		/*
		 * 2.1,2.2 METODO PARA INCREMENTAR LOS AÑOS DEL SEGUNDO EMPLEADO Y PONER
		 * CATEGORIA 9 EN EL PRIMERO
		 */
		actualizarEmpleadosBd();

		/*
		 * 1.1,1.1,1.3,2.3 Actualizar la base de datos almacenando el sueldo resultante
		 * para cada empleado.
		 */
	

		// Calcular sueldos y escribir en el archivo binario "salarios.dat"
		 List<Empleado> listaEmpleados =
				 leerEmpleadosDesdeTexto("C:\\Users\\Victor\\eclipse-workspace\\finalmavenproject\\src\\main\\java\\laboral\\empleados.txt");

				// Calcular sueldos y guardar en un archivo binario
				 calcularYGuardarSueldos(listaEmpleados,
				 "C:\\Users\\Victor\\eclipse-workspace\\finalmavenproject\\src\\main\\java\\laboral\\salarios.dat");
//		for (Empleado empleado : listaEmpleados) {
//
//			int sueldo = Nomina.sueldo(empleado);
//			actualizarNomina(empleado.dni, sueldo);
//
//		}

		/* Metodo alta Empleado */

		Empleado e1 = new Empleado("Victor", "47266493K", "M", 7, 8);
		CalculaNominas.altaEmpleado(e1);

		/* Metodo altaEmpleadoArchivo */
//		CalculaNominas.altaEmpleado(
//				"C:\\Users\\Victor\\eclipse-workspace\\finalmavenproject\\src\\main\\java\\laboral\\empleadosNuevos.txt");

		/* GUARDAR LOS DATOS DE LA BASE DE DATOS EN UN ARCHIVO DE TEXTO */
		CalculaNominas.guardarArchivoRespaldo();

		Menu();
	}
	// Método para leer empleados desde un archivo de texto
		private static List<Empleado> leerEmpleadosDesdeTexto(String archivoTexto) throws DatosNoCorrectoExcpetion {
			List<Empleado> empleados = new ArrayList<>();

			try (BufferedReader br = new BufferedReader(new FileReader(archivoTexto))) {
				String linea;
				while ((linea = br.readLine()) != null) {
					// Parsear la línea para obtener los datos del empleado
					String[] partes = linea.split(",");
					String dni = partes[0];
					String nombre = partes[1];
					String sexo = partes[2];
					int categoria = Integer.parseInt(partes[3]);
					int anyos = Integer.parseInt(partes[4]);

					// Crear un objeto Empleado y agregarlo a la lista
					Empleado empleado = new Empleado(dni, nombre, sexo, categoria, anyos);
					empleados.add(empleado);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return empleados;
			
			
		}
		
		// Método para calcular sueldos y guardar en un archivo binario
		private static void calcularYGuardarSueldos(List<Empleado> empleados, String archivoBinario) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoBinario))) {
				for (Empleado empleado : empleados) {
					int sueldo = Nomina.sueldo(empleado); // Calcular el sueldo usando la clase Nomina
					String dniSueldo = empleado.dni + "," + sueldo; // Combinar DNI y sueldo en un String
					oos.writeObject(dniSueldo); // Guardar DNI y sueldo como String
				}
				System.out.println("Sueldos calculados y guardados en " + archivoBinario);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	private static void actualizarEmpleadosBd() {
		Connection conn = null;

		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			Statement statement = conn.createStatement();
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

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}

	}

	private static void ActualizarEmpleadosTxt() {
		List<String> lineasActualizadas = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(
				"C:\\Users\\Victor\\eclipse-workspace\\finalmavenproject\\src\\main\\java\\laboral\\empleados.txt"))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] campos = linea.split(",");
				if (campos.length == 5) {
					String nombre = campos[0].trim();
					String dni = campos[1].trim();
					char sexo = campos[2].trim().charAt(0);
					int categoria = Integer.parseInt(campos[3].trim());
					int anyos = Integer.parseInt(campos[4].trim());

					/* Aplica las actualizaciones según el empleado */
					if (dni.equalsIgnoreCase("32000032G")) {
						// Actualiza la categoría del empleado con DNI "32000032G"
						categoria = 9; // Cambia la categoría según tus necesidades
					} else if (dni.equalsIgnoreCase("32000031R")) {
						// Incrementa los años de trabajo del empleado con DNI "32000031R"
						anyos++; // Incrementa los años en 1
					}
					// Crea la línea actualizada
					String lineaActualizada = nombre + "," + dni + "," + sexo + "," + categoria + "," + anyos;
					lineasActualizadas.add(lineaActualizada);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Escribe las líneas actualizadas en el archivo "empleados.txt"
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(
				"C:\\Users\\Victor\\eclipse-workspace\\finalmavenproject\\src\\main\\java\\laboral\\empleados.txt"))) {
			for (String linea : lineasActualizadas) {
				writer.write(linea);
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void actualizarNomina(String dni, int sueldo) {
		Connection conn = null;

		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "INSERT INTO nominas (empleado_id, sueldo) VALUES (?, ?)"
					+ " ON DUPLICATE KEY UPDATE sueldo = VALUES(sueldo)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				// Obtén el ID del empleado basado en su DNI (suponiendo que tengas una relación
				// adecuada en la base de datos)
				int empleadoId = obtenerEmpleadoIdPorDni(dni);

				stmt.setInt(1, empleadoId);
				stmt.setInt(2, sueldo);

				// Ejecuta la consulta SQL para insertar o actualizar el sueldo
				stmt.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}
	}

	private static int obtenerEmpleadoIdPorDni(String dni) {
		Connection conn = null;

		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "SELECT id FROM empleados WHERE dni = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				// Obtén el ID del empleado basado en su DNI (suponiendo que tengas una relación
				// adecuada en la base de datos)
				stmt.setString(1, dni);

				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						// Si se encuentra un resultado, devuelve el ID del empleado
						return rs.getInt("id");
					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}
		// Si no se encuentra ningún resultado o si ocurre un error, devuelve un valor
		// negativo o -1 (indicando que no se encontró el empleado)
		return -1;
	}

	private static void altaEmpleado(Empleado empleado) {
		Connection conn = null;

		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "INSERT INTO empleados (nombre, dni, sexo, categoria, anyos) VALUES (?, ?, ?, ?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, empleado.nombre);
				stmt.setString(2, empleado.dni);
				stmt.setString(3, String.valueOf(empleado.sexo));
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
		} finally {

			ConexionBD.cerrarConexion(conn);
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

	private static void guardarArchivoRespaldo() {
		Connection conn = null;

		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "SELECT * FROM empleados";

			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
				// Crear un archivo de texto para guardar los datos
				FileWriter archivoTexto = new FileWriter(
						"C:\\Users\\Victor\\eclipse-workspace\\finalmavenproject\\src\\main\\java\\laboral\\respaldo.txt");

				// Iterar a través de los resultados y escribir en el archivo de texto
				while (rs.next()) {
					int id = rs.getInt("id");
					String nombre = rs.getString("nombre");
					String dni = rs.getString("dni");
					String sexo = rs.getString("sexo");
					String categoria = rs.getString("categoria");
					String anyos = rs.getString("anyos");
					// Otros campos que desees guardar

					// Escribir los datos en el archivo de texto
					archivoTexto.write("ID: " + id + ", Nombre: " + nombre + ", DNI: " + dni + ", Sexo: " + sexo
							+ " Categoria: " + categoria + " Anyos: " + anyos + "\n");
				}

				// Cerrar el archivo de texto
				archivoTexto.close();
				System.out.println("Datos guardados en respaldo.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}
	}

	private static void Menu() throws DatosNoCorrectoExcpetion {
		Connection conn = null;

		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			Scanner scanner = new Scanner(System.in);
			int opcion;

			do {
				System.out.println("Menú de opciones:");
				System.out.println("0. Salir");
				System.out.println("1. Mostrar información de todos los empleados");
				System.out.println("2. Mostar el salario del empleado por dni");
				System.out.println("3. Modificar");
				System.out.println("4. ---");
				System.out.println("5. Recalcular y actualizar los sueldos de todos los empleados.");
				System.out.println("6. Realizar una copia de seguridad de la base de datos en ficheros.");

				System.out.print("Selecciona una opción: ");
				opcion = scanner.nextInt();

				switch (opcion) {
				case 0:
					System.out.print("Adios");
					break;
				case 1:
					mostrarInformacionEmpleados();
					break;
				case 2:
					mostrarSueldoPorDni();
					break;
				case 3:
					modificar();
					break;
				case 4:
					break;
				case 5:
					recalcular();
					break;
				case 6:
					backUp();
					break;
				default:
					break;
				}
			} while (opcion != 0);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}
	}

	private static void mostrarInformacionEmpleados() {
		Connection conn = null;

		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "SELECT nombre,dni,sexo,categoria,anyos FROM empleados";

			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

				// Iterar a través de los resultados y escribir en el archivo de texto
				while (rs.next()) {

					String nombre = rs.getString("nombre");
					String dni = rs.getString("dni");
					String sexo = rs.getString("sexo");
					String categoria = rs.getString("categoria");
					String anyos = rs.getString("anyos");

					System.out.println("Nombre: " + nombre);
					System.out.println("DNI: " + dni);
					System.out.println("Sexo: " + sexo);
					System.out.println("Categoría: " + categoria);
					System.out.println("Años trabajados: " + anyos);
					System.out.println("-----------");
				}

				// Cerrar el archivo de texto

				System.out.println("Datos guardados en respaldo.txt");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}
	}

	private static void mostrarSueldoPorDni() {
		Connection conn = null;
		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();
			Scanner sc = new Scanner(System.in);

			System.out.print("Dime el dni");
			String dni = sc.nextLine();
			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "SELECT sueldo,dni from nominas join empleados on empleados.id=nominas.empleado_id where dni='"
					+ dni + "'";

			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

				// Iterar a través de los resultados y escribir en el archivo de texto
				while (rs.next()) {

					int sueldo = rs.getInt("sueldo");
					String dni2 = rs.getString("dni");
					System.out.println("Sueldo: " + sueldo);
					System.out.println("Dni: " + dni2);

					System.out.println("-----------");
				}

				// Cerrar el archivo de texto

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);

		}
	}

	private static void modificar() throws DatosNoCorrectoExcpetion {
		Connection conn = null;
		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();
			Scanner sc = new Scanner(System.in);

			while (true) {
				System.out.println("Seleccione una opción:");
				System.out.println("1. Modificar nombre");
				System.out.println("2. Modificar DNI");
				System.out.println("3. Modificar categoría");
				System.out.println("4. Modificar años trabajados");
				System.out.println("5. Salir");
				System.out.print("Ingrese el número de opción: ");
				int opcion = sc.nextInt();
				sc.nextLine(); // Consume el salto de línea

				if (opcion == 1) {
					// Modificar nombre
					System.out.print("Ingrese el DNI del empleado: ");
					String dni = sc.nextLine();
					System.out.print("Ingrese el nuevo nombre: ");
					String nuevoNombre = sc.nextLine();

					// Ejecuta la consulta SQL para modificar el nombre
					String sql = "UPDATE empleados SET nombre = ? WHERE dni = ?";
					try (PreparedStatement stmt = conn.prepareStatement(sql)) {
						stmt.setString(1, nuevoNombre);
						stmt.setString(2, dni);
						int filasAfectadas = stmt.executeUpdate();
						if (filasAfectadas > 0) {
							System.out.println("Nombre modificado correctamente.");
						} else {
							System.out.println("No se encontró ningún empleado con ese DNI.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (opcion == 2) {
					// Modificar DNI
					System.out.print("Ingrese el DNI del empleado a modificar: ");
					String dniAntiguo = sc.nextLine();
					System.out.print("Ingrese el nuevo DNI: ");
					String nuevoDNI = sc.nextLine();

					// Ejecuta la consulta SQL para modificar el DNI
					String sql = "UPDATE empleados SET dni = ? WHERE dni = ?";
					try (PreparedStatement stmt = conn.prepareStatement(sql)) {
						stmt.setString(1, nuevoDNI);
						stmt.setString(2, dniAntiguo);
						int filasAfectadas = stmt.executeUpdate();
						if (filasAfectadas > 0) {
							System.out.println("DNI modificado correctamente.");
						} else {
							System.out.println("No se encontró ningún empleado con ese DNI.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (opcion == 3) {
					// Modificar categoría y actualizar sueldo
					System.out.print("Ingrese el DNI del empleado cuya categoría desea modificar: ");
					String dniEmpleado = sc.nextLine();

					// Verifica si el empleado existe
					int empleadoId = obtenerEmpleadoIdPorDni(dniEmpleado);
					if (empleadoId != -1) {
						System.out.print("Ingrese la nueva categoría: ");
						int nuevaCategoria = Integer.parseInt(sc.nextLine());

						// Actualiza la categoría del empleado en la base de datos
						String sql = "UPDATE empleados SET categoria = ? WHERE id = ?";
						try (PreparedStatement stmt = conn.prepareStatement(sql)) {
							stmt.setInt(1, nuevaCategoria);
							stmt.setInt(2, empleadoId);
							int filasAfectadas = stmt.executeUpdate();

							if (filasAfectadas > 0) {
								// La categoría se ha modificado correctamente
								// Ahora calculamos el nuevo sueldo utilizando tu método
								Empleado empleado = obtenerEmpleadoPorId(empleadoId);
								int nuevoSueldo = Nomina.sueldo(empleado);

								// Actualiza el sueldo del empleado en la base de datos
								actualizarSueldo(empleadoId, nuevoSueldo);

								System.out.println("Categoría y sueldo actualizados correctamente.");
							} else {
								System.out.println("No se encontró ningún empleado con ese DNI.");
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("No se encontró ningún empleado con ese DNI.");
					}
				} else if (opcion == 4) {
					// Modificar categoría y actualizar sueldo
					System.out.print("Ingrese el DNI del empleado cuyo anyos desea modificar: ");
					String dniEmpleado = sc.nextLine();

					// Verifica si el empleado existe
					int empleadoId = obtenerEmpleadoIdPorDni(dniEmpleado);
					if (empleadoId != -1) {
						System.out.print("Ingrese los nuevos anyos: ");
						int nuevosAnyos = Integer.parseInt(sc.nextLine());

						// Actualiza los anyos del empleado en la base de datos
						String sql = "UPDATE empleados SET anyos = ? WHERE id = ?";
						try (PreparedStatement stmt = conn.prepareStatement(sql)) {
							stmt.setInt(1, nuevosAnyos);
							stmt.setInt(2, empleadoId);
							int filasAfectadas = stmt.executeUpdate();

							if (filasAfectadas > 0) {
								// La categoría se ha modificado correctamente
								// Ahora calculamos el nuevo sueldo utilizando tu método
								Empleado empleado = obtenerEmpleadoPorId(empleadoId);
								int nuevoSueldo = Nomina.sueldo(empleado);

								// Actualiza el sueldo del empleado en la base de datos
								actualizarSueldo(empleadoId, nuevoSueldo);

								System.out.println("Anyos y sueldo actualizados correctamente.");
							} else {
								System.out.println("No se encontró ningún empleado con ese DNI.");
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("No se encontró ningún empleado con ese DNI.");
					}
				} else if (opcion == 5) {
					break;

				} else {
					System.out.println("Opción no válida. Intente de nuevo.");
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);

		}
	}

	private static Empleado obtenerEmpleadoPorId(int empleadoId) throws DatosNoCorrectoExcpetion {
		Empleado empleado = null;
		Connection conn = null;
		try {
			conn = ConexionBD.obtenerConexion();
		} catch (Exception e) {
			// TODO: handle exception
		}
		String sql = "SELECT * FROM empleados WHERE id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, empleadoId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String nombre = rs.getString("nombre");
					String dni = rs.getString("dni");
					String sexo = rs.getString("sexo");
					int categoria = rs.getInt("categoria");
					int anyos = rs.getInt("anyos");

					empleado = new Empleado(nombre, dni, sexo, categoria, anyos);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return empleado;
	}

	private static void actualizarSueldo(int empleadoId, int nuevoSueldo) throws DatosNoCorrectoExcpetion {
		// Obtener el empleado por su ID
		Connection conn = null;
		try {
			conn = ConexionBD.obtenerConexion();
		} catch (Exception e) {
			// TODO: handle exception
		}
		Empleado empleado = obtenerEmpleadoPorId(empleadoId);

		if (empleado != null) {
			// Calcular el nuevo sueldo utilizando tu método sueldo(Empleado empleado)
			nuevoSueldo = Nomina.sueldo(empleado);

			// Actualizar el sueldo en la base de datos
			String sql = "UPDATE nominas SET sueldo = ? WHERE empleado_id = ?";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, nuevoSueldo);
				stmt.setInt(2, empleadoId);

				int filasActualizadas = stmt.executeUpdate();

				if (filasActualizadas > 0) {
					System.out.println("Sueldo actualizado con éxito.");
				} else {
					System.out.println("No se pudo actualizar el sueldo.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No se encontró el empleado con el ID proporcionado.");
		}
	}

	private static List<Empleado> obtenerEmpleadosBd() throws DatosNoCorrectoExcpetion {
		Connection conn = null;
		List<Empleado> listaEmpleados = new ArrayList<>();
		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "SELECT * FROM empleados";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {

						String nombre = rs.getString("nombre");
						String dni = rs.getString("dni");
						String sexo = rs.getString("sexo");
						int categoria = rs.getInt("categoria");
						int anyos = rs.getInt("anyos");

						Empleado empleado = new Empleado(nombre, dni, sexo, categoria, anyos);
						listaEmpleados.add(empleado);
					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}
		return listaEmpleados;
	}

	private static void recalcular() throws DatosNoCorrectoExcpetion {

		// Recupera todos los empleados de la base de datos
		List<Empleado> empleados = obtenerEmpleadosBd();

		// Recalcula y actualiza los sueldos de todos los empleados
		for (Empleado empleado : empleados) {
			int nuevoSueldo = Nomina.sueldo(empleado); // Calcula el nuevo sueldo
			try {
				actualizarSueldoPorDNI(empleado.dni, nuevoSueldo);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Actualiza el sueldo en la base de datos
		}

		// Cierra la conexión a la base de datos
	}

	// Implementa la lógica para actualizar el sueldo de un empleado en la base de
	// datos por DNI
	private static void actualizarSueldoPorDNI(String dni, int nuevoSueldo) throws SQLException {
		Connection conn = null;
		try {
			// Obtener la conexión a la base de datos
			conn = ConexionBD.obtenerConexion();

			// Leer los datos de los empleados desde la tabla Empleados
			String sql = "UPDATE nominas join empleados on empleados.id=nominas.empleado_id SET sueldo = ? WHERE dni = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, nuevoSueldo);
				stmt.setString(2, dni);
				stmt.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConexionBD.cerrarConexion(conn);
		}

	}
	
	private static void backUp() {
		String comando = "C:\\Program Files\\MariaDB 11.1\\bin\\mysqldump"; // Comando para realizar el volcado de la base de datos en MariaDB

		// Opcional: Puedes especificar la ruta completa si no está en la variable de entorno PATH
		// String comando = "/ruta/completa/a/mariadb-dump";

		String baseDeDatos = "2b"; // Reemplaza esto con el nombre de tu base de datos
		String usuario = "root"; // Reemplaza esto con tu nombre de usuario de MariaDB
		String contraseña = ""; // Reemplaza esto con tu contraseña de MariaDB
		String rutaDeArchivo = "C:\\Users\\Victor\\eclipse-workspace\\finalmavenproject\\src\\main\\java\\laboral\\backup.sql"; // Ruta donde deseas guardar el archivo de respaldo
		

		// Construir el comando completo para realizar la copia de seguridad
		String[] comandos = new String[] {
		    comando,
		    "--user=" + usuario,
		    "--password=" + contraseña,
		    "--databases", baseDeDatos,
		    "--result-file=" + rutaDeArchivo
		};

		// Ejecutar el comando para realizar la copia de seguridad
		try {
		    ProcessBuilder builder = new ProcessBuilder(comandos);
		    Process process = builder.start();
		    int exitCode = process.waitFor();

		    if (exitCode == 0) {
		        System.out.println("Copia de seguridad exitosa.");
		    } else {
		        System.err.println("Error al realizar la copia de seguridad. Código de salida: " + exitCode);
		    }
		} catch (IOException | InterruptedException e) {
		    e.printStackTrace();
		}

	}

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Pattern;

public class Main {

	private static Socket clientSocket;
	private static InputStream IS;
	private static OutputStream OS;
	private static String name;

	public static void main(String[] args) throws IOException {
		try {
			initSocket();
			inputUserName();
			initListenerThread();
			menu();
		} catch (java.net.ConnectException e) {
			System.out.println(">> La conexi�n se ha detenido o no ha sido posible conectarse, cerrando...");
		}
		cerrarSocket();
	}

	// INICIAMOS EL SOCKET
	private static void initSocket() throws IOException {
		clientSocket = new Socket();
		System.out.println(">> Estableciendo conexi�n");

		// CAMBIA ESTAS VARIABLES EN FUNCI�N DE TU SOCKET SERVIDOR
		// LA PRIMERA VEZ QUE INICIES TANTO EL SERVIDOR COMO EL CLIENTE PUEDE QUE
		// SE REQUIERA HABILITAR EL FIREWALL
		String ip = "172.30.2.72";
		int port = 6666;

		InetSocketAddress addr = new InetSocketAddress(ip, port);
		clientSocket.connect(addr);

		IS = clientSocket.getInputStream();
		OS = clientSocket.getOutputStream();
	}

	// PEDIMOS EL NOMBRE AL USUARIO CLIENTE
	private static void inputUserName() throws IOException {
		do {
			System.out.println("MEN�:\n=====\n1. Escribir mensaje.\n2. Recibir mensaje.\n3. Salir.");
			name = pedirString("Introduzca su nombre: ", "[A-za-z]{1,30}");
		} while (name == "");
	}

	// INICIAMOS EL HILO DE ESCUCHA DE MENSAJES DE OTROS CLIENTES PROVENIENTES DEL
	// SERVIDOR
	private static void initListenerThread() {
		Listener hEscuchar = new Listener(IS, name);
		hEscuchar.start();
	}

	// M�TODO QUE MUESTRA UN MEN� AL USUARIO
	private static void menu() throws IOException {
		int opcion = 0;
		do {
			opcion = 0;
			System.out.println("MEN�:\n=====\n1. Escribir mensaje.\n2. Salir.");
			opcion = pedirInt();
			if (opcion == 1) {
				enviarMensaje();
			}
		} while (opcion != 2);

		System.out.println(">> Mensaje enviado");
	}

	// PEDIMOS EL MENSAJE AL USUARIO Y LO ENVIAMOS
	private static void enviarMensaje() throws IOException {
		String msg = name + ": " + pedirString(">> Introduzca el mensaje a enviar: ", "[A-za-z]{1,99}");
		OS.write(msg.getBytes());
		System.out.println(">> Mensaje enviado");
	}

	// CERRAMOS EL SOCKET Y FINALIZAMOS EL PROGRAMA
	private static void cerrarSocket() throws IOException {
		System.out.println(">> Cerrando el socket cliente");
		clientSocket.close();
		System.out.println(">> Programa finalizado");
	}

	// M�TODO PARA PEDIR UN STRING AL USUARIO, RECIBE EL MENSAJE A MOSTRAR Y LA
	// EXPRESI�N REGULAR QUE DEBE CUMPLIR
	public static String pedirString(String mensajeMostrado, String regex) throws IOException {
		BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		Boolean control = true;
		do {
			control = true;
			System.out.println(mensajeMostrado);
			input = BR.readLine();
			if (!Pattern.matches(regex, input)) {
				control = false;
				System.out.println("Error: Formato incorrecto <<" + regex + ">>");
			}
		} while (!control);

		return input;
	}

	// M�TODO PARA PEDIR UN ENTERO AL USUARIO
	public static Integer pedirInt() throws IOException {
		BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));
		Integer input = 0;
		Boolean flag = true;
		do {
			flag = true;
			try {
				input = Integer.parseInt(BR.readLine());
			} catch (NumberFormatException e) {
				System.out.println(">> Formato incorrecto, vuelva a introducir la opci�n");
				flag = false;
			}
		} while (input < 1 || input > 2 || !flag);

		return input;
	}
}

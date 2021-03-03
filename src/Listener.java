import java.io.IOException;
import java.io.InputStream;

public class Listener extends Thread {

	InputStream IS;
	String name;

	public Listener(InputStream IS, String nombre) {
		this.IS = IS;
		this.name = name;
	}

	public void run() {

		while (true) {
			try {
				byte[] mensajeRecibido = new byte[25];
				IS.read(mensajeRecibido);

				// EL SERVIDOR NOS VA A DEVOLVER TODOS LOS MENSAJES, POR LO QUE DEBEMOS FILTRAR
				// Y MOSTRAR ÚNICAMENTE LOS QUE NO SON NUESTROS
				if (mensajeRecibido != null && !new String(mensajeRecibido).split(": ")[0].contains(name)) {
					System.out.println(new String(mensajeRecibido));
				}
			} catch (IOException e) {
			}
		}
	}
}

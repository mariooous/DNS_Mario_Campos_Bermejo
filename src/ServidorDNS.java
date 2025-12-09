import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorDNS {
    private static HashMap<String, String> registros = new HashMap<>();
    private static final String ARCHIVO = "src/dns_direcciones.txt";

    public static void main(String[] args) {
        cargarRegistros();
        iniciarServidor();
    }
    private static void iniciarServidor() {
        try (ServerSocket server = new ServerSocket(5000)) {
            Socket cliente = server.accept();
            atenderCliente(cliente);
        } catch (IOException e) {

        }
    }
    private static void cargarRegistros() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(" ");
                if (partes.length == 3 && partes[1].equals("A")) {
                    registros.put(partes[0], partes[2]);
                }
            }
        } catch (IOException e) {
        }
    }
    private static void atenderCliente(Socket cliente) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)
        ) {
            String comando;
            while ((comando = in.readLine()) != null) {
                String respuesta = procesarComando(comando);
                out.println(respuesta);
                if (comando.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }
            cliente.close();

        } catch (IOException e) {
        }
    }

    private static String procesarComando(String comando) {
        try {
            if (comando.equalsIgnoreCase("EXIT")) {
                return "200 cerrando,adios";
            }
            String[] partes = comando.split(" ");
            if (partes.length == 3 && partes[0].equals("LOOKUP") && partes[1].equals("A")) {
                String dominio = partes[2];
                String ip = registros.get(dominio);

                if (ip != null) {
                    return "200 " + ip;
                } else {
                    return "404 no encontado";
                }
            }

            return "400 Bad Request";

        } catch (Exception e) {
            return "500 Server error";
        }
    }
}
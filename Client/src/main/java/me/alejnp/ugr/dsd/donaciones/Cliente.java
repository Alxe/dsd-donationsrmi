package me.alejnp.ugr.dsd.donaciones;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.function.Consumer;

public class Cliente {

  public static void main(String[] args) {
    if(args.length == 0) {  
      System.out.println("Uso: <host> <accion> ...");
      System.exit(-1);
    }

    try {
      if(System.getSecurityManager() == null) {
        System.setSecurityManager(new SecurityManager());
      }

      Registry registry = LocateRegistry.getRegistry("localhost", 1099);
      Servicio servicio = (Servicio) registry.lookup("Donaciones");
      Cliente cliente = new Cliente(servicio);

      Consumer<String[]> op;
      switch(args[0].toLowerCase()) {
        case "donar":     op = cliente::donar; break;
        case "registrar": op = cliente::registrar; break;
        case "consultar": op = cliente::consultar; break;
        default: throw new UnsupportedOperationException("Operación no válida");
      }

      op.accept(Arrays.copyOfRange(args, 1, args.length));
    } catch (Exception e) {
      e.printStackTrace(); 
    }
  }

  private final Servicio servicio;

  private Cliente(Servicio servicio) {
    this.servicio = servicio;
  }

  void registrar(String[] args) {
    String nombreCliente = args[0];

    try {
      Boolean exito = servicio.registrar(nombreCliente);

      System.out.printf("Registro realizado: %b%n", exito);
    } catch(Exception e) {}
  }

  void consultar(String[] args) {
    try {
      Double cantidad = servicio.consultar();

      System.out.printf("Cantidad %f%n", cantidad);
    } catch(Exception e) {}
  }

  void donar(String[] args) {
    String nombreCliente = args[0];
    Double cantidad = Double.valueOf(args[1]);

    try {
      Boolean exito = servicio.donar(nombreCliente, cantidad);

      System.out.printf("Donazación realizada: %b%n", exito);
    } catch(Exception e) {}
  }
}
package me.alejnp.ugr.dsd.donaciones;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * ServidorBalanceado
 */
public class ServidorBalanceado implements Servicio, Serializable {
  public static void main(String[] args) {
    if(System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
    
    try {
      Registry registry = LocateRegistry.createRegistry(1099);

      Servicio servicios[] = new Servicio[4];
      
      for(int i = 0; i < servicios.length; ++i) {
        servicios[i] = (Servicio) UnicastRemoteObject.exportObject(new ServidorNodo(), 0);
        
        registry.rebind("Servicio" + i, servicios[i]);
      }

      Servicio stub = (Servicio) UnicastRemoteObject.exportObject(new ServidorBalanceado(servicios), 0);
      registry.rebind("Donaciones", stub);
    } catch(Exception e) {
      e.printStackTrace();
      System.err.println("Error al crear servicios");
    }
  }

  private static final long serialVersionUID = -287091934294518954L;

  private final List<Servicio> nodos;
  private int nodoPos = 0;

  public ServidorBalanceado(Servicio... nodos) {
    this.nodos = List.of(nodos);
  }

  private Servicio nextNodo() {
    nodoPos = (nodoPos + 1) % nodos.size();
    return nodos.get(nodoPos);
  }

  @Override
  public boolean registrar(String nombreCliente) throws RemoteException {
    if(existe(nombreCliente)) return false;

    return nextNodo().registrar(nombreCliente);
  }

  @Override
  public boolean donar(String nombreCliente, Double cantidad) throws RemoteException {
    for(Servicio nodo : nodos) {
      if(nodo.existe(nombreCliente)) {
        return nodo.donar(nombreCliente, cantidad);
      }
    }
    
    return false;
  }

  @Override
  public double consultar() throws RemoteException {
    double sum = 0.;

    for(Servicio nodo : nodos) {
      sum += nodo.consultar();
    }

    return sum;
  }

  @Override
  public boolean existe(String nombreCliente) throws RemoteException {
    for(Servicio nodo : nodos) {
      if(nodo.existe(nombreCliente)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public double consultar(String nombreCliente) throws RemoteException {
    double sum = 0.;

    for(Servicio nodo : nodos) {
      sum += nodo.consultar(nombreCliente);
    }

    return sum;
  }
  
}
package core;

/**
 * Created by slave00 on 26/10/16.
 */
public interface EventosServidor {
    void registrarObservador(Observador obj);
    void notificarObservadores(String obj);
    void notificarLogout(String obj);
    void notificarLista(String obj);
}

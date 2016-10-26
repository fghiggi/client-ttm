package core;

/**
 * Created by slave00 on 26/10/16.
 */
public interface AssuntoObservavel {
    public void registrarObservador(Observador obj);
    public void removerObservador(Observador obj);
    public void notificarObservadores(String obj);
    public void notificarLogout(String obj);
}

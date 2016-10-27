package core;

/**
 * Created by slave00 on 26/10/16.
 */
public interface Observador {
    void atualizar(String obj);
    void removerLista(String obj);
    void adicionarLista(String obj);
}

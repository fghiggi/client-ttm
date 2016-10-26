package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Cliente implements Runnable, AssuntoObservavel {
    List<Observador> observadores = new ArrayList<Observador>();
    Socket s;
    PrintWriter out;
    BufferedReader in;
    Thread t;
    Integer passo = 0;
    boolean pooling;

    static final String RECEBE_MENSAGEM_PUCLICA = "$:->mensagem";
    static final String RECEBE_MENSAGEM_PRIVADA = "$:->privado";
    static final String RECEBE_USUARIO = "$:->usuario";
    static final String RECEBE_ENTROU_SALA = "$:->entrou";
    static final String RECEBE_STATUS = "$:->status";
    static final String RECEBE_SAIR_SALA = "$:->sair";
    static final String ENVIO_MENSAGEM_PUCLICA = "/mensagem";
    static final String ENVIO_MENSAGEM_PRIVADA = "/privado";
    static final String ENVIO_LISTA = "/lista";

    private static final String CRLF = "\r\n"; // newline

    public Cliente(String host, int port, boolean pooling){
        this.pooling = pooling;
        setup(host, port);
        start();
    }

    private void setup(String host, int port) {
        try {
            s = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());
        } catch (ConnectException ce){
            System.out.println(ce.getMessage());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarMensagem(String msg){
        String um = msg + CRLF;

        if(passo >= 2){
            um += ENVIO_MENSAGEM_PUCLICA + " " + um;
        }
        out.write(um);
        out.flush();

        if(passo == 0)
            passo++;
    }

    public void enviarMensagem(String msg, String destino){
        String um = msg + CRLF;

        out.write(ENVIO_MENSAGEM_PRIVADA + " " + destino + " " + um);
        out.flush();
    }

    private  void verificarMensagem(String mensagem){
        MensagemServer tipo = Protocolo.parseMensagemServer(mensagem);

        switch (tipo){
            case MENSAGEM_PUCLICA: notificarObservadores(mensagem.substring(RECEBE_MENSAGEM_PUCLICA.length() + 1));
                break;
            case MENSAGEM_PRIVADA:
                String x[] = mensagem.split(" ");

                notificarObservadores("MENSAGEM PRIVADA DE " + x[1] + ": " + x[2]);
                break;
            case SAIU_SALA:
                String z[] = mensagem.split(" ");
                notificarObservadores(z[1] + " " + z[2] + " " + z[3] + " " + z[4]);
                notificarLogout(z[1]);
                break;
            case ENTROU_SALA:
                String y[] = mensagem.split(" ");
                notificarObservadores(y[1] + "Entrou na sala ");
                break;
            case ENVIO_LISTA:
                passo++;
                mensagem += "@@u@@" + mensagem;
                notificarObservadores(mensagem);
                break;
            case RECEBE_STATUS:
                notificarObservadores(mensagem.substring(RECEBE_STATUS.length() + 1));
                break;
            case INVALIDA: notificarObservadores(mensagem);
                break;
        }
    }

    public void receberMensagem(){
        try {
            String msg = in.readLine();

            verificarMensagem(msg);
        } catch (NullPointerException ex){
            serverClosed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start(){
        t = new Thread(this);
        t.start();
    }

    private void serverClosed(){
        notificarObservadores("O servidor caiu");

        for(int i = 0; i < 5; i++){
            notificarObservadores("A aplicação vai fechar em " + (i + 1));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        closeStrams();

        System.exit(0);
    }

    private void closeStrams(){
        try {
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        this.pooling = false;
        passo++;
        enviarMensagem("/sair");
        closeStrams();
        try {
            this.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(pooling){
            receberMensagem();
        }
    }

    @Override
    public void registrarObservador(Observador obj) {
        observadores.add(obj);
    }

    @Override
    public void removerObservador(Observador obj) {
        observadores.remove(obj);
    }

    @Override
    public void notificarObservadores(String obj) {
        for(Observador o:observadores){
            o.atualizar(obj);
        }
    }

    @Override
    public void notificarLogout(String obj) {
        for(Observador o:observadores){
            o.removerLista(obj);
        }
    }
}
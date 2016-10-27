package core;

import com.feevale.protocolo.MensagemCliente;
import com.feevale.protocolo.MensagemServer;
import com.feevale.protocolo.Protocolo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Cliente implements Runnable, EventosServidor {
    private List<Observador> observadores = new ArrayList<>();
    private Socket s;
    private PrintWriter out;
    private BufferedReader in;
    private Thread t;
    private boolean pooling;
    private boolean conectado = false;

    private static final String QUEBRA_DE_LINHA = "\r\n";

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

    private void enviarMensagem(String msg){
        out.write(msg + QUEBRA_DE_LINHA);
        out.flush();
    }

    private  void verificarMensagem(String mensagem){
        MensagemServer tipo = Protocolo.parseMensagemServer(mensagem);
        String args[];

        switch (tipo){
            case MENSAGEM_PUCLICA:
                args = mensagem.split(" ");

                notificarObservadores(String.format("%s diz: %s", args[1], args[2]));
                break;
            case MENSAGEM_PRIVADA:
                args = mensagem.split(" ");

                notificarObservadores(String.format("Mensagem privada de %s: %s",args[1], args[2]));
                break;
            case SAIU_SALA:
                notificarObservadores(mensagem.substring(MensagemServer.SAIU_SALA.mensagem.length() + 1));

                notificarLogout(mensagem.split(" ")[1]);
                break;
            case ENTROU_SALA:
                args = mensagem.split(" ");

                notificarObservadores(String.format("%s Entrou na sala.", args[1]));
                break;
            case ENVIO_LISTA:
                conectado = true;

                notificarLista(mensagem);
                break;
            case RECEBE_STATUS:
                notificarObservadores(mensagem.substring(MensagemServer.RECEBE_STATUS.mensagem.length() + 1));
                break;
            case INVALIDA: notificarObservadores(mensagem);
                break;
        }
    }

    public void enviarMensagemPrivada(String mensagem, String destinatario){
        enviarMensagem(String.format("%s %s %s", MensagemCliente.PRIVADO.mensagem, destinatario, mensagem));
    }

    public void enviarMensagemPublica(String mensagem){
        if(conectado)
            enviarMensagem(String.format("%s %s", MensagemCliente.MENSAGEM.mensagem, mensagem));
        else
            enviarMensagem(mensagem);
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
        notificarObservadores("O servidor caiu :S");

        dormir(3000);

        for(int i = 5; i > 0; i--){
            notificarObservadores(String.format("A aplicação vai fechar em %s", i));

            dormir(1000);
        }

        closeStreams();

        System.exit(0);
    }

    private void dormir(int milis){
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeStreams(){
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

        enviarMensagem(MensagemCliente.SAIR.mensagem);

        closeStreams();
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

    @Override
    public void notificarLista(String obj) {
        for(Observador o:observadores){
            o.adicionarLista(obj);
        }
    }
}
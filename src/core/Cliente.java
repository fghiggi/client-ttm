package core;

import com.sun.org.apache.regexp.internal.RE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Observable;

public class Cliente extends Observable implements Runnable {
    Socket s;
    PrintWriter out;
    BufferedReader in;
    Thread t;
    Integer passo = 0;

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

    public Cliente(String host, int port){
        setup(host, port);
        start();
    }

    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
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

    public void receberMensagem(){
        try {
            String msg = in.readLine();
            System.out.println(msg);
            if(msg.startsWith(RECEBE_MENSAGEM_PUCLICA)) {
                String x[] = msg.split(" ");

                notifyObservers(x[1] + ": " + x[2]);
            }else if(msg.startsWith(RECEBE_MENSAGEM_PRIVADA)) {
                String x[] = msg.split(" ");

                notifyObservers("MENSAGEM PRIVADA" + x[1] + ": " + x[2]);
            } else if (msg.startsWith(RECEBE_SAIR_SALA)) {
                String x[] = msg.split(" ");
                notifyObservers(x[1] + " " + x[2] + " " + x[3] + " " + x[4]);
            } else if(msg.startsWith(RECEBE_ENTROU_SALA)){
                String x[] = msg.split(" ");
                notifyObservers("Entrou na sala " + x[1]);
            } else if(msg.startsWith(RECEBE_USUARIO)) {
                passo++;
                msg += "@@u@@" + msg;
                notifyObservers(msg);
            } else if(msg.startsWith(RECEBE_STATUS)) {
                notifyObservers(msg.substring(RECEBE_STATUS.length() + 1));
            } else {
                notifyObservers(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start(){
        t = new Thread(this);
        t.start();
    }

    public void stop(){
        try {
            passo++;
            enviarMensagem("/sair");
            out.close();
            in.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            receberMensagem();
        }
    }
}
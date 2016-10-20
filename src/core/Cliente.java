package core;

import java.io.BufferedReader;
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
        out.write(um);
        out.flush();
    }

    public void receberMensagem(){
        try {
            notifyObservers(in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start(){
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while(true){
            receberMensagem();
        }
    }
}
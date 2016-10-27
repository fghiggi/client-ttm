package view;

import core.Cliente;
import core.Observador;
import javafx.scene.input.KeyCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by slave00 on 19/10/16.
 */
public class ChatView extends JFrame implements Observador {
    private JList<String> listaUsuarios;
    private JTextArea areaMensagens;
    private JTextField campoMensagem;
    private JButton botaoEnviar;

    private Cliente cli;

    public ChatView(){
        build();
    }

    public void startUp(Cliente cli){
        this.cli = cli;
        cli.registrarObservador(this);
    }

    public void build(){
        DefaultListModel model = new DefaultListModel();
        listaUsuarios = new JList<String>(model);
        listaUsuarios.setFixedCellWidth(150);
        add(new JScrollPane(listaUsuarios), BorderLayout.LINE_START);

        areaMensagens = new JTextArea(30, 70);
        areaMensagens.setEditable(false);
        areaMensagens.setLineWrap(true);
        add(new JScrollPane(areaMensagens), BorderLayout.CENTER);

        Box box = Box.createHorizontalBox();
        add(box, BorderLayout.SOUTH);
        campoMensagem = new JTextField();
        botaoEnviar = new JButton("Enviar");
        box.add(campoMensagem);
        box.add(botaoEnviar);

        pack();

        setTitle("cliente chat talk to much");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        ActionListener sendListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String str = campoMensagem.getText();

                if (str != null && str.trim().length() > 0){
                    if(listaUsuarios.getSelectedValue() != null){
                        cli.enviarMensagemPrivada(str, listaUsuarios.getSelectedValue());
                    }else {
                        cli.enviarMensagemPublica(str);
                    }
                }

                campoMensagem.selectAll();
                campoMensagem.requestFocus();
                campoMensagem.setText("");
            }
        };

        KeyAdapter ad = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    listaUsuarios.clearSelection();
            }
        };

        campoMensagem.addActionListener(sendListener);
        botaoEnviar.addActionListener(sendListener);
        listaUsuarios.addKeyListener(ad);
        campoMensagem.addKeyListener(ad);
        areaMensagens.addKeyListener(ad);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cli.stop();
            }
        });
    }

    @Override
    public void atualizar(String obj) {
        areaMensagens.append(obj);
        areaMensagens.append("\n");
    }

    @Override
    public void removerLista(String obj) {
        DefaultListModel dlm = (DefaultListModel) listaUsuarios.getModel();

        if(dlm.contains(obj)){
            dlm.removeElement(obj);
        }
    }

    @Override
    public void adicionarLista(String obj) {
        DefaultListModel dlm = (DefaultListModel) listaUsuarios.getModel();

        String usuario = obj.split(" ")[1];

        if(!dlm.contains(usuario)){
            dlm.addElement(usuario);
        }
    }
}

package view;

import core.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by slave00 on 19/10/16.
 */
public class ChatView extends JFrame implements Observer {
    private JList<String> listaUsuarios;
    private JTextArea areaMensagens;
    private JTextField campoMensagem;
    private JButton botaoEnviar;

    private Cliente cli;

    public ChatView(Cliente cli){
        this.cli = cli;
        cli.addObserver(this);

        build();
    }

    public void build(){
        listaUsuarios = new JList<String>();
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
                if (str != null && str.trim().length() > 0)
                    cli.enviarMensagem(str);
                campoMensagem.selectAll();
                campoMensagem.requestFocus();
                campoMensagem.setText("");
            }
        };

        campoMensagem.addActionListener(sendListener);
        botaoEnviar.addActionListener(sendListener);
    }

    @Override
    public void update(Observable o, Object arg) {
        final Object finalArg = arg;
        Runnable updateTxtMensagens = () -> {
            areaMensagens.append(finalArg.toString());
            areaMensagens.append("\n");
        };

        SwingUtilities.invokeLater(updateTxtMensagens);
    }
}

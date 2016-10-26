package core;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Created by slave00 on 26/10/16.
 */
public enum MensagemServer {
    MENSAGEM_PUCLICA ("$:->mensagem"),
    MENSAGEM_PRIVADA ("$:->privado"),
    SAIU_SALA ("$:->sair"),
    ENTROU_SALA ("$:->entrou"),
    ENVIO_LISTA ("$:->usuario"),
    RECEBE_STATUS ("$:->status"),
    INVALIDA;;

    private final String mensagem;

    MensagemServer(final String msg) {
        mensagem = msg;
    }

    MensagemServer() {
        mensagem = "";
    }

    private final static Map<String, MensagemServer> map =
            stream(MensagemServer.values()).collect(toMap(msg -> msg.mensagem, msg -> msg));

    public static MensagemServer obterMensagemServer(String msg) {
        return map.get(msg);
    }
}

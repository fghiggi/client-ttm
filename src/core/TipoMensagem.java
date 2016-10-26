package core;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

/**
 * Created by slave00 on 26/10/16.
 */
public enum TipoMensagem {
    LISTA ("/lista"),
    MENSAGEM ("/mensagem"),
    PRIVADO ("/privado"),
    SAIR ("/sair"),
    INVALIDA;

    private final String mensagem;

    TipoMensagem(final String msg) {
        mensagem = msg;
    }

    TipoMensagem() {
        mensagem = "";
    }

    private final static Map<String, TipoMensagem> map =
            stream(TipoMensagem.values()).collect(toMap(msg -> msg.mensagem, msg -> msg));

    public static TipoMensagem obterTipoMensagem(String msg) {
        return map.get(msg);
    }
}
package core;

/**
 * Created by slave00 on 26/10/16.
 */
public class Protocolo {
    static final String MENSAGEM_PUCLICA = "$:->mensagem";
    static final String MENSAGEM_PRIVADA = "$:->privado";
    static final String SAIU_SALA = "$:->sair";
    static final String ENTROU_SALA = "$:->entrou";
    static final String ENVIO_LISTA = "$:->usuario";
    static final String RECEBE_STATUS = "$:->status";

    public static TipoMensagem parse(String mensagem){
        String comando = mensagem.split(" ")[0];

        TipoMensagem msg = TipoMensagem.obterTipoMensagem(comando);

        return msg == null? TipoMensagem.INVALIDA : msg;
    }

    public static MensagemServer parseMensagemServer(String mensagem){
        String comando = mensagem.split(" ")[0];

        MensagemServer msg = MensagemServer.obterMensagemServer(comando);

        return msg == null? MensagemServer.INVALIDA : msg;
    }
}

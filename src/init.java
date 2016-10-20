import core.Cliente;
import view.ChatView;

/**
 * Created by slave00 on 12/10/16.
 */
public class init {
    public static void main(String[] args) {
        new ChatView(new Cliente("127.0.0.1", 8088));
    }
}

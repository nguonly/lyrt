package net.lyrt.rollback.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguonly on 6/27/17.
 */
public class ClientMain {
    public static void main(String... args) throws IOException {
        List<ClientServiceHandler> clients = new ArrayList<>();

        clients.add(new ClientServiceHandler(new String[]{"LZ", "AES", "LZX-AES"}));
        clients.add(new ClientServiceHandler(new String[]{"LZX", "LZ", "LZ-AES"}));
        clients.add(new ClientServiceHandler(new String[]{"AES", "LZ", "AES-LZ"}));

        clients.forEach(c -> c.start());
    }
}

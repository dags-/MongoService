package me.dags.mongo;

import java.net.InetSocketAddress;

/**
 * @author dags <dags@dags.me>
 */
public interface MongoService {

    InetSocketAddress getAddress();
}

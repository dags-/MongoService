package me.dags.mongo;

import com.google.inject.Inject;
import de.bwaldvogel.mongo.MongoBackend;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.h2.H2Backend;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "mongoservice", name = "MongoService", version = "0.1", description = "A plugin-provided mongodb server")
public class MongoPlugin implements MongoService {

    private final Logger logger;
    private final MongoServer server;

    @Inject
    public MongoPlugin(@ConfigDir(sharedRoot = false) Path dir, PluginContainer container) {
        logger = container.getLogger();
        server = new MongoServer(getBackend(dir));
        server.bind(getAddress(dir));
    }

    @Override
    public InetSocketAddress getAddress() {
        return server.getLocalAddress();
    }

    @Listener
    public void pre(GamePreInitializationEvent event) {
        logger.info("Serving MongoDB on: {}", server.getLocalAddress());
        Sponge.getServiceManager().setProvider(this, MongoService.class, this);
    }

    @Listener
    public void stop(GameStoppedServerEvent event) {
        server.shutdownNow();
    }

    private static InetSocketAddress getAddress(Path dir) {
        // so
        Path config = dir.resolve("config");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(ConfigurationOptions.defaults().setShouldCopyDefaults(true))
                .setPath(config)
                .build();

        // much
        ConfigurationNode node;
        try {
            node = loader.load();
        } catch (IOException e) {
            node = loader.createEmptyNode();
        }

        // boiler
        int port = node.getNode("port").getInt(27017);

        try {
            mkdir(dir);
            loader.save(node);
            // plate
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new InetSocketAddress("127.0.0.1", port);
    }

    private static MongoBackend getBackend(Path dir) {
        String database = mkdir(dir).resolve("database.mv").toString();
        return new H2Backend(database);
    }

    private static Path mkdir(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }
}

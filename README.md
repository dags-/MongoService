# MongoService
A plugin-provided mongodb server backed by a file MVStore -
 you probably shouldn't use this :|

Basically wraps this into a sponge plugin:  
https://github.com/bwaldvogel/mongo-java-server

### Configuration
Default port is `27017`, you can change this in the `config.conf`.

<sub>...done</sub>

### Use
The db server is initialized during instantiation of the MongoService plugin object (ie before any GameStateEvents have been fired).  
You should probably not connect your mongodb client any sooner than the `GamePreInitializationEvent` phase.

If for some reason you want to retreive the InetSocketAddress of the server programatically, you can query Sponge's service manager for the `MongoService`:
```java
@Listener
public void init(GameInitializationEvent event) {
    MongoService service = Sponge.getServiceManager().provideUnchecked(MongoService.class);
    InetSocketAddress server = service.getAddress();
    // connect client
}
```

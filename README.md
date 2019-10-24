[![Build Status](https://travis-ci.org/libredrop/peer-discovery-kotlin.svg?branch=master)](https://travis-ci.org/libredrop/peer-discovery-kotlin)

This lib is implementation of [peer-discovery](https://github.com/libredrop/peers-discovery) for Kotlin, JVM.

### Example

Discovery peer:
```kotlin
val peerDiscobery = PeerDiscovery.Builder().build()

// You could add additional meta info like: public key, name, small icons
val metaInfo = MetaInfoBuilder().putString("slogan", "Hello world!").build()

launch(Dispatchers.IO) {
    fixture.start("my-service", metaInfo = metaInfo).collect {
        println("peer was found!")

        establishConnection(peer.addresses, peer.port)
    }    
}
```

Library supports a dictionary of metadata which allows more easily identify peer without extra calls:

```kotlin
val metaInfo = MetaInfoBuilder()
    .putString(key, value)
    .putInt(key, value)
    .putBoolean(key, value)
    .putByteArray(key, value)
    .build()
```

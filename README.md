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

### License

```
MIT License

Copyright (c) 2019 LibreDrop

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

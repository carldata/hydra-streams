# Hydra Streams

[![Build status](https://travis-ci.org/carldata/hydra-streams.svg?branch=master)](https://travis-ci.org/carldata/hydra-streams)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.carldata/hydra-streams_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.carldata/hydra-streams_2.12)

Protocol specification and implementation for Hydra

## Quick start
 
 Add the following dependency to the build.sbt
 ```scala
 libraryDependencies += "io.github.carldata" %% "hydra-streams" % "0.4.4"
 ```


## Build

Compile AVRO schema

```bash
java -jar ~/bin/avro/avro-tools-1.8.2.jar compile schema avro/data.avpr src/test/scala
```


# Join in!

We are happy to receive bug reports, fixes, documentation enhancements,
and other improvements.

Please report bugs via the
[github issue tracker](http://github.com/carldata/hydra-streams/issues).



# Redistributing

FlowScript source code is distributed under the Apache-2.0 license.

**Contributions**

Unless you explicitly state otherwise, any contribution intentionally submitted
for inclusion in the work by you, as defined in the Apache-2.0 license, shall be
licensed as above, without any additional terms or conditions.

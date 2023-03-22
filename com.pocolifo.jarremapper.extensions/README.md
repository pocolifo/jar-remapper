# JAR Remapper Extension

Extra features for [JAR Remapper](https://github.com/pocolifo/jar-remapper)

## Features

#### More Remapping Engines
- [Tiny Remapper](https://github.com/FabricMC/tiny-remapper) by FabricMC
- [Special Source](https://github.com/md-5/SpecialSource) by MD-5


## Mapping Engine Support
| Remapping Engine | Classes | Fields | Methods | Parameters  |
|------------------|---------|--------|---------|-------------|
| Standard         | X       | X      | X       | X           |
| Tiny Remapper    | X       | X      | X       | Coming Soon |
| Special Source   | X       | X      | X       |             |

`X` = Support

## Coming Soon
- Parameter name remapping for Tiny Remapper

## Getting Started

#### Tiny Remapper Remapping Engine

Append the `withRemappingEngine` option to JAR Remapper. 

```java
JarRemapper.newRemap()
    .withRemappingEngine(new TinyRemapperEngine()
        // (required)
        // Use any options you'd like from Tiny Remapper
        // except for: withMappings and ignoreFieldDesc
        // These are automatically set.
        .setOptions(TinyRemapper.newRemapper())
        
        // (optional)
        // Excludes the META-INF directory from output JAR
        .excludeMetaInf()
    )
// ...whatever other options you use for JAR Remapper...
```

#### Special Source Remapping Engine

Append the `withRemappingEngine` option to JAR Remapper.

```java
JarRemapper.newRemap()
    .withRemappingEngine(new SpecialSourceEngine()
        // (optional)
        // Excludes the META-INF directory from output JAR
        .excludeMetaInf()
    )
// ...whatever other options you use for JAR Remapper...
```

# Develop

1. Clone this repository
2. Import the project into IntelliJ IDEA

### To test
1. Edit the `jarremapper` extension configuration in [build.gradle](./build.gradle) to add readers and engines
2. Run the `generateTests` Gradle task under the `jarremapperdev` group to generate test classes
3. Test like normal

# Pocolifo's JAR Remapper

Making remapping JARs easy, organized, and painless

## Features

#### Remapping Engines
- Multiple different engines to remap JARs
- StandardRemappingEngine by the devs of JAR Remapper
- FabricMC's TinyRemapper using a Remapping Engine (*requires additional dependency, see [jar-remapper-extension](https://github.com/pocolifo/jar-remapper-extension)*)

#### Remapping
- Class remapping
- Method remapping
- Field remapping
- Parameter remapping

#### Common mapping formats
- Built-in ModCoderPack's SRG/MCP mapping support
- Built-in Tiny v1/v2 mapping support

#### Flexible API
- Custom Mapping Readers
- Custom Remapping Engines

## Coming Soon
- Built-in Proguard mapping support


# Getting Started

Load in your favorite mapping format

```java
JarMapping mappings;

// MCP mappings
mappings = new McpMappingReader(
    new File("joined.srg"),
    new File("joined.exc"),

    new File("methods.csv"),
    new File("fields.csv"),
    new File("params.csv")
).read();

// Tiny v1 mappings
mappings = new Tiny1MappingReader(
    new File("mappings-tiny-v1.tiny")
).read("remapFromThisNamespace", "remapToThisNamespace");

// Tiny v2 mappings
mappings = new Tiny2MappingReader(
    new File("mappings-tiny-v2.tiny")
).read("remapFromThisNamespace", "remapToThisNamespace");
```

With your mappings, remap your JAR file

```java
JarRemapper.newRemap()
    // (required)
    // The JAR to be remap
    .withInputFile(new File("input.jar"))
    
    // (required)
    // The output of the remapped JAR
    .withOutputFile(new File("output.jar"))
    
    // (required)
    // The mappings to remap the JAR with
    .withMappings(mappings)
        
    // (optional)
    // The Remapping Engine to remap the JAR with
    // default: StandardRemappingEngine
    .withRemappingEngine(new StandardRemappingEngine())
        
    // (optional)
    // Automatically deletes the output file before remapping
    // default: false
    .overwriteOutputFile()
    
    // Begin remapping the JAR!
    .remap();
```

# Develop

1. Clone this repository
2. Import the project into IntelliJ IDEA

### To test
1. Edit the `jarremapper` extension configuration in [build.gradle](com.pocolifo.jarremapper/build.gradle) to add readers and engines
2. Run the `generateTests` Gradle task under the `jarremapperdev` group to generate test classes
3. Test like normal
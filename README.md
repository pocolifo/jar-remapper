# Pocolifo's JAR Remapper

Making remapping JARs easy, organized, and painless

## Features

#### Remapping
- Class remapping
- Method remapping
- Field remapping
- Parameter remapping

#### Common Minecraft mapping formats
- Built-in SRG/MCP mapping support
- Built-in Yarn v1 & v2-merged mapping support

#### Flexible API
- Remapping plugins
- Automatically exclude `META-INF` directory from output JAR
- Read your own mappings

## Coming Soon

- Local variable remapping
- Built-in Proguard mapping support
- Multithreaded remapping

# Getting Started

Simply read some mappings

```java
// mcp mappings
McpMappingReader reader = new McpMappingReader(
        new File("joined.srg"),
        new File("joined.exc"),

        new File("methods.csv"),
        new File("fields.csv"),
        new File("params.csv")
);

// yarn v1 mappings
YarnV1MappingReader reader = new YarnV1MappingReader(
    new File("mappings-1.17.1.tiny")
);

// yarn v2 merged mappings
YarnMergedV2MappingReader reader = new YarnMergedV2MappingReader(
    new File("mappings-merged-v2-1.17.1.tiny")
);

// read the mappings
JarMapping jarMapping = reader.read();
```

Then, remap the JAR like so

```java 
JarRemapper.newBuilder()
   // (required)
   // Set input (not remapped) JAR
   .setInputFile(new File("minecraft.jar"))
   
   // (required)
   // Read and set the mappings we will remap with
   .setJarMapping(jarMapping)
   
   // (required)
   // Set the output (remapped) JAR
   .setOutputFile(new File("minecraft-deobfuscated.jar"))
   
   // (optional)        
   // Set a remapping plugin (see SimpleProgressListener above)
   .setRemappingPlugin(new SimpleProgressListener())
   
   // (optional)
   // Exclude the META-INF directory from the output JAR
   .excludeMetaInf()
   
   // Remap the JAR!
   .remap();
```

# Code snippets

Simple remapping plugin for tracking progress

```java
import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.plugin.IRemappingPlugin;

import java.util.zip.ZipFile;

public class SimpleProgressListener implements IRemappingPlugin {

   @Override
   public void onBeginRemap(ZipFile remappingJar) {
      System.out.println("Beginning remap");
   }

   @Override
   public void onBeforeRemapClass(ClassMapping classMapping) {
      System.out.println("Just about to remap a class...");
   }

   @Override
   public void onAfterRemapClass(ClassMapping classRemapped) {
      System.out.println("Just finished remapping a class!");
   }

   @Override
   public void onDoneRemap() {
      System.out.println("All classes remapped!");
   }

}
```

# Develop

1. Clone this repository
2. Import the project into IntelliJ IDEA
3. Run the `setup` Gradle task
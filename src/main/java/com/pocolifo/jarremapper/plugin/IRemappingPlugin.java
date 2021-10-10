package com.pocolifo.jarremapper.plugin;

import com.pocolifo.jarremapper.mapping.ClassMapping;

import java.util.zip.ZipFile;

public interface IRemappingPlugin {
    void onBeginRemap(ZipFile remappingJar);

    void onBeforeRemapClass(ClassMapping remappingClass);

    void onAfterRemapClass(ClassMapping classRemapped);

    void onDoneRemap();
}

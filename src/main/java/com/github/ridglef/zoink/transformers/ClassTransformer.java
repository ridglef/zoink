package com.github.ridglef.zoink.transformers;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {
    public void transformClass(ClassNode cn);
}

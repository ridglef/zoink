package com.github.ridglef.zoink.transformers;

import org.objectweb.asm.tree.ClassNode;

public class SourceFileTransformer implements ClassTransformer{
    @Override
    public void transformClass(ClassNode cn) {
        cn.sourceFile = null;
    }
}

package com.github.ridglef.zoink.transformers;

import org.objectweb.asm.tree.ClassNode;

public class SourceDebugTransformer implements ClassTransformer{
    @Override
    public void transformClass(ClassNode cn) {
        cn.sourceDebug = null;
    }
}

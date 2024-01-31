package com.github.ridglef.zoink.transformers;

import org.objectweb.asm.tree.ClassNode;

public class ParameterTransformer implements ClassTransformer{
    @Override
    public void transformClass(ClassNode cn) {
        cn.methods.forEach(methodNode -> methodNode.parameters = null);
    }
}

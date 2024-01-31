package com.github.ridglef.zoink.transformers;

import org.objectweb.asm.tree.ClassNode;

public class LocalVariableTransformer implements ClassTransformer{

    @Override
    public void transformClass(ClassNode cn) {
        cn.methods.forEach(methodNode -> methodNode.localVariables = null);
    }
}

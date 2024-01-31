package com.github.ridglef.zoink.transformers;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;

public class LineNumberTransformer implements ClassTransformer{
    @Override
    public void transformClass(ClassNode cn) {
        cn.methods.forEach(methodNode -> methodNode.instructions.forEach(abstractInsnNode -> {
            if (abstractInsnNode instanceof LineNumberNode) methodNode.instructions.remove(abstractInsnNode);
        }));
    }
}

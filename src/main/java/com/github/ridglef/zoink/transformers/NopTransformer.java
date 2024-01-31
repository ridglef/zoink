package com.github.ridglef.zoink.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class NopTransformer implements ClassTransformer{
    @Override
    public void transformClass(ClassNode cn) {
        cn.methods.forEach(methodNode -> methodNode.instructions.forEach(abstractInsnNode -> {
            if (abstractInsnNode.getOpcode() == Opcodes.NOP) methodNode.instructions.remove(abstractInsnNode);
        }));
    }
}

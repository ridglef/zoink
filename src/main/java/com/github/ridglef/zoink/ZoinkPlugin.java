package com.github.ridglef.zoink;

import com.github.ridglef.zoink.transformers.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectMapper;
import org.gradle.internal.impldep.com.fasterxml.jackson.databind.ObjectWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

public class ZoinkPlugin implements Plugin<Project> {
    @SuppressWarnings({"all"})
    @Override
    public void apply(Project project) {
        project.afterEvaluate(projectAfterEvaluate -> {
            project.getTasks().getByName("jar").doLast(task -> {
        List<ClassNode> classes = new ArrayList<>();
        project.getPlugins().apply(JavaPlugin.class);

        Jar jarTask = (Jar) task;
        JarFile inputJar = null;
        try {
            inputJar = new JarFile(new File(jarTask.getArchivePath().toURI()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (JarOutputStream zipOutputStream = new JarOutputStream(new FileOutputStream(new File(project.getBuildDir(), "libs/" + project.getName() + "-zoinked.jar")))) {
            for (Enumeration<JarEntry> iter = inputJar.entries(); iter.hasMoreElements(); ) {
                JarEntry entry = iter.nextElement();
                try (InputStream in = inputJar.getInputStream(entry)) {
                    if (entry.getName().endsWith(".class")) {
                        ClassReader reader = new ClassReader(in);
                        ClassNode classNode = new ClassNode();
                        reader.accept(classNode, 0);
                        classes.add(classNode);
                    } else if (entry.getName().endsWith(".json")) {
                        String minifiedJson = minifyJson(in);
                        zipOutputStream.putNextEntry(new ZipEntry(entry.getName()));
                        copy(new ByteArrayInputStream(minifiedJson.getBytes()), zipOutputStream);
                    } else {
                        zipOutputStream.putNextEntry(new JarEntry(entry.getName()));
                        copy(in, zipOutputStream);
                    }
                }
            }
            List<ClassTransformer> transformers = List.of(new LineNumberTransformer(), new LocalVariableTransformer(), new NopTransformer(), new ParameterTransformer(), new SourceDebugTransformer(), new SourceFileTransformer());
            classes.forEach(classNode -> {
                transformers.forEach(classTransformer -> classTransformer.transformClass(classNode));
            });

            zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);

            for (ClassNode classNode : classes) {

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                classNode.accept(writer);

                try {
                    byte[] bytes = writer.toByteArray();
                    zipOutputStream.putNextEntry(new JarEntry(classNode.name + ".class"));
                    zipOutputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            });
        });
    }

    private static String minifyJson(InputStream inputStream) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            return objectWriter.writeValueAsString(objectMapper.readTree(jsonContent.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        int read;
        byte[] buffer = new byte[0x1000];
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}

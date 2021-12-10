package org.powernukkit.tools;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author joserobjr
 * @since 2021-12-02
 */
@PowerNukkitOnly
@Since("FUTURE")
@Log4j2
public class AutoUpdatePowerNukkitOnlyAnnotations2 {
    public static void main(String[] args) {
        Preconditions.checkArgument(args.length == 0 || args.length == 1, "Invalid arguments");
        Path clourburstNukkitSourceCode = Paths.get(args.length == 1? args[0] : "../../org.cloudburst/Nukkit");

        new AutoUpdatePowerNukkitOnlyAnnotations2().execute(
                clourburstNukkitSourceCode.resolve("src/main/java"),
                Paths.get("src/main/java")
        );
    }

    private void execute(Path nukkitSrc, Path powerNukkitSrc) {
        try (Stream<Path> walk = Files.walk(powerNukkitSrc)) {
            walk.parallel()
                    .filter(Files::isRegularFile)
                    .filter(it-> it.getFileName().toString().toLowerCase(Locale.ENGLISH).endsWith(".java"))
                    .forEach(it-> update(it, nukkitSrc.resolve(powerNukkitSrc.relativize(it).toString())));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void update(Path powerNukkitFilePath, Path nukkitFilePath) {
        try {
            if (!Files.isRegularFile(nukkitFilePath)) {
                setFullyPowerNukkitOnly(powerNukkitFilePath);
            } else {
                compareDifferences(powerNukkitFilePath, nukkitFilePath);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String toString(CtModel javaUnit) {
        return javaUnit.getAllTypes()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n\n", "", "\n"));
    }

    private void setFullyPowerNukkitOnly(Path powerNukkitFilePath) throws IOException {
        CtModel javaUnit = loadJavaUnit(powerNukkitFilePath);
        int changes = javaUnit.getAllTypes().parallelStream().mapToInt(this::addAnnotationsToEverything).sum();
        if (changes > 0) {
            log.info("{}\tchanges to\t{}", changes, powerNukkitFilePath);
            //Files.write(powerNukkitFilePath, toString(javaUnit).getBytes(StandardCharsets.UTF_8));
        }
    }

    private void compareDifferences(Path powerNukkitFilePath, Path nukkitFilePath) throws IOException {
        CtModel pnJavaUnit = loadJavaUnit(powerNukkitFilePath);
        CtModel nuJavaUnit = loadJavaUnit(nukkitFilePath);

        int changes = pnJavaUnit.getAllTypes().parallelStream()
                .mapToInt(pnJavaType ->
                        nuJavaUnit.getAllTypes().stream()
                                .filter(it -> pnJavaType.getQualifiedName().equals(it.getQualifiedName()))
                                .findFirst()
                                .map(nuJavaType -> compareDifferencesBetweenTypes(pnJavaType, nuJavaType))
                                .orElseGet(() -> addAnnotationsToEverything(pnJavaType))
                ).sum();

        if (changes > 0) {
            log.info("{}\tchanges to\t{}", changes, powerNukkitFilePath);
            //Files.write(powerNukkitFilePath, toString(pnJavaUnit).getBytes(StandardCharsets.UTF_8));
        }
    }

    private int compareDifferencesBetweenTypes(CtType<?> pnJavaType, CtType<?> nuJavaType) {
        IntStream changeStream = IntStream.of(removeAnnotation(pnJavaType));
        AtomicBoolean needPowerNukkitOnly = new AtomicBoolean();
        IntStream stream = pnJavaType.getFields().parallelStream().filter(it-> it.isPublic() || it.isProtected()).mapToInt(pnField -> {
            CtField<?> nuField = nuJavaType.getField(pnField.getSimpleName());
            if (nuField == null || !nuField.isPublic() && !nuField.isProtected()) {
                needPowerNukkitOnly.set(true);
                return addAnnotation(pnField);
            }
            if (!nuField.getType().toString().equals(pnField.getType().toString())) {
                log.error("Incompatible field change detected at {} field {}", pnJavaType.getQualifiedName(), pnField.getSimpleName());
                needPowerNukkitOnly.set(true);
                return addAnnotation(pnField);
            }

            return removeAnnotation(pnField);
        });
        changeStream = IntStream.concat(changeStream, stream);

        stream = pnJavaType.getMethods().parallelStream().mapToInt(pnMethod -> {
            if (!pnMethod.isPublic() && !pnMethod.isProtected() || pnMethod.hasAnnotation(Override.class)) {
                return removeAnnotation(pnMethod);
            }

            CtMethod<?> nuMethod = nuJavaType.getMethod(
                    pnMethod.getSimpleName(),
                    pnMethod.getParameters().stream().map(CtTypedElement::getType).toArray(CtTypeReference<?>[]::new)
            );
            if (nuMethod == null || !nuMethod.isPublic() && !nuMethod.isProtected()) {
                needPowerNukkitOnly.set(true);
                return addAnnotation(pnMethod);
            }
            if (!Objects.equals(nuMethod.getSignature(), pnMethod.getSignature())) {
                log.error("Incompatible method change detected at {} method {}({})",
                        pnJavaType.getQualifiedName(),
                        pnMethod.getSimpleName(),
                        pnMethod.getParameters().stream().map(CtParameter::getType).map(CtTypeReference::getSimpleName).collect(Collectors.joining(", "))
                );
                needPowerNukkitOnly.set(true);
                return addAnnotation(pnMethod);
            }

            return removeAnnotation(pnMethod);
        });
        changeStream = IntStream.concat(changeStream, stream);

        int changes = changeStream.sum();
        if (!needPowerNukkitOnly.get()) {
            changes += removeImport(pnJavaType);
        }
        return changes;
    }

    private int removeImport(CtType<?> source) {
        /*if (source.hasImport(PowerNukkitOnly.class.getName())) {
            imp.removeImport(PowerNukkitOnly.class.getName());
            return 1;
        }*/
        return 0;
    }

    private int addAnnotationsToEverything(CtType<?> source) {
        IntStream changeStream = IntStream.of(addAnnotation(source));
        changeStream = IntStream.concat(changeStream, source.getFields().parallelStream().mapToInt(this::addAnnotation));
        changeStream = IntStream.concat(changeStream, source.getMethods().parallelStream().mapToInt(this::addAnnotation));
        changeStream = IntStream.concat(changeStream, source.getNestedTypes().parallelStream().mapToInt(this::addAnnotationsToEverything));
        return changeStream.parallel().sum();
    }

    private CtModel loadJavaUnit(Path filePath) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(filePath.toString());
        return launcher.buildModel();
    }

    private int addAnnotation(CtElement obj) {
        if (obj instanceof CtLambda<?>) {
            return 0;
        }
        if (obj instanceof CtConstructor<?> || obj instanceof CtMethod<?> || obj instanceof CtField) {
            if (((CtTypeMember) obj).getDeclaringType().isAnnotationType()) {
                return 0;
            }
        }
        if (obj instanceof CtMethod<?> && obj.hasAnnotation(Override.class)) {
            return 0;
        }
        if (!obj.hasAnnotation(PowerNukkitOnly.class)) {
            //obj.getFactory().Annotation().annotate(obj, PowerNukkitOnly.class);
            log.warn("Must add annotation to " + name(obj));
            return 1;
        }
        return 0;
    }

    private int removeAnnotation(CtElement obj) {
        Optional<CtAnnotation<? extends Annotation>> ctAnnotation = obj.getAnnotations().stream().filter(it -> it.getName().equals(PowerNukkitOnly.class.getName())).findFirst();
        if (!ctAnnotation.isPresent()) {
            return 0;
        }
        //obj.removeAnnotation(ctAnnotation.get());
        log.warn("Must remove annotation from " + name(obj));
        return 1;
    }

    private String name(CtElement obj) {
        if (obj instanceof CtTypeInformation) {
            return ((CtTypeInformation) obj).getQualifiedName();
        } else if (obj instanceof CtTypeMember) {
            CtTypeMember member = (CtTypeMember) obj;
            StringBuilder sb = new StringBuilder(1024)
                    .append(member.getDeclaringType().getQualifiedName())
                    .append('#')
                    .append(member.getSimpleName());
            if (obj instanceof CtExecutable) {
                CtExecutable<?> executable = (CtExecutable<?>)obj;
                sb.append('(')
                        .append(executable.getParameters().stream()
                                .map(param -> param.getType().getSimpleName())
                                .collect(Collectors.joining(", ")))
                        .append(')');
            }
            return sb.toString();
        } else if (obj instanceof CtNamedElement) {
            return ((CtNamedElement) obj).getSimpleName();
        } else {
            return obj.toStringDebug();
        }
    }
}

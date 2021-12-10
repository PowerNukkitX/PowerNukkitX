package org.powernukkit.tools;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.*;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.AnnotationTargetSource;
import org.jboss.forge.roaster.model.source.Importer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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
public class AutoUpdatePowerNukkitOnlyAnnotations {
    public static void main(String[] args) {
        Preconditions.checkArgument(args.length == 0 || args.length == 1, "Invalid arguments");
        Path clourburstNukkitSourceCode = Paths.get(args.length == 1? args[0] : "../../org.cloudburst/Nukkit");

        new AutoUpdatePowerNukkitOnlyAnnotations().execute(
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

    private void setFullyPowerNukkitOnly(Path powerNukkitFilePath) throws IOException {
        JavaUnit javaUnit = loadJavaUnit(powerNukkitFilePath);
        int changes = javaUnit.getTopLevelTypes().parallelStream().mapToInt(this::addAnnotationsToEverything).sum();
        if (changes > 0) {
            log.info("{}\tchanges to\t{}", changes, powerNukkitFilePath);
            //Files.write(powerNukkitFilePath, javaUnit.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private void compareDifferences(Path powerNukkitFilePath, Path nukkitFilePath) throws IOException {
        JavaUnit pnJavaUnit = loadJavaUnit(powerNukkitFilePath);
        JavaUnit nuJavaUnit = loadJavaUnit(nukkitFilePath);
        int changes = pnJavaUnit.getTopLevelTypes().parallelStream()
                .mapToInt(pnJavaType ->
                        nuJavaUnit.getTopLevelTypes().stream()
                                .filter(it -> pnJavaType.getCanonicalName().equals(it.getCanonicalName()))
                                .findFirst()
                                .map(nuJavaType -> compareDifferencesBetweenTypes(pnJavaType, nuJavaType))
                                .orElseGet(() -> addAnnotationsToEverything(pnJavaType))
                ).sum();
        if (changes > 0) {
            log.info("{}\tchanges to\t{}", changes, powerNukkitFilePath);
            //Files.write(powerNukkitFilePath, pnJavaUnit.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    private int compareDifferencesBetweenTypes(JavaType<?> pnJavaType, JavaType<?> nuJavaType) {
        IntStream changeStream = IntStream.of(removeAnnotation(pnJavaType));
        AtomicBoolean needPowerNukkitOnly = new AtomicBoolean();
        if (pnJavaType instanceof FieldHolder<?>) {
            IntStream stream = ((FieldHolder<?>) pnJavaType).getFields().parallelStream().filter(it-> it.isPublic() || it.isProtected()).mapToInt(pnField -> {
                if (!(nuJavaType instanceof FieldHolder<?>)) {
                    needPowerNukkitOnly.set(true);
                    return addAnnotation(pnField);
                }
                Field<?> nuField = ((FieldHolder<?>) nuJavaType).getField(pnField.getName());
                if (nuField == null || nuField.isPackagePrivate() || nuField.isPrivate()) {
                    needPowerNukkitOnly.set(true);
                    return addAnnotation(pnField);
                }
                if (!nuField.getType().toString().equals(pnField.getType().toString())) {
                    log.error("Incompatible field change detected at {} field {}", pnJavaType.getQualifiedName(), pnField.getName());
                    needPowerNukkitOnly.set(true);
                    return addAnnotation(pnField);
                }

                return removeAnnotation(pnField);
            });
            changeStream = IntStream.concat(changeStream, stream);
        }
        if (pnJavaType instanceof MethodHolder<?>) {
            IntStream stream = ((MethodHolder<?>) pnJavaType).getMethods().parallelStream().mapToInt(pnMethod -> {
                if (pnMethod.isPrivate() || pnMethod.isPackagePrivate() || pnMethod.hasAnnotation(Override.class)) {
                    return removeAnnotation(pnMethod);
                }

                if (!(nuJavaType instanceof MethodHolder<?>)) {
                    needPowerNukkitOnly.set(true);
                    return addAnnotation(pnMethod);
                }
                Method<?, ?> nuMethod = ((MethodHolder<?>) nuJavaType).getMethod(
                        pnMethod.getName(),
                        pnMethod.getParameters().stream().map(it -> it.getType().getName()).toArray(String[]::new)
                );
                if (nuMethod == null || nuMethod.isPackagePrivate() || nuMethod.isPrivate()) {
                    needPowerNukkitOnly.set(true);
                    return addAnnotation(pnMethod);
                }
                if (!Objects.equals(
                        Optional.ofNullable(nuMethod.getReturnType()).map(Object::toString).orElse(null),
                        Optional.ofNullable(pnMethod.getReturnType()).map(Object::toString).orElse(null))
                ) {
                    log.error("Incompatible method change detected at {} method {}({})",
                            pnJavaType.getQualifiedName(),
                            pnMethod.getName(),
                            pnMethod.getParameters().stream().map(Parameter::getName).collect(Collectors.joining(", "))
                    );
                    needPowerNukkitOnly.set(true);
                    return addAnnotation(pnMethod);
                }

                return removeAnnotation(pnMethod);
            });
            changeStream = IntStream.concat(changeStream, stream);
        }

        int changes = changeStream.sum();
        if (!needPowerNukkitOnly.get()) {
            changes += removeImport(pnJavaType);
        }
        return changes;
    }

    private int removeImport(Object source) {
        if (source instanceof Importer<?>) {
            Importer<?> imp = (Importer<?>) source;
            if (imp.hasImport(PowerNukkitOnly.class.getName())) {
                imp.removeImport(PowerNukkitOnly.class.getName());
                return 1;
            }
        }
        return 0;
    }

    /*private int addImport(Object source) {
        if (source instanceof Importer<?>) {
            Importer<?> imp = (Importer<?>) source;
            if (!imp.hasImport(PowerNukkitOnly.class)) {
                imp.addImport(PowerNukkitOnly.class);
                return 1;
            }
        }
        return 0;
    }*/

    private int addAnnotationsToEverything(Object source) {
        int changes = addAnnotation(source)/* + addImport(source)*/;
        IntStream changeStream = IntStream.empty();
        if (source instanceof MemberHolder<?>) {
            changeStream = ((MemberHolder<?>) source).getMembers().parallelStream().mapToInt(this::addAnnotation);
        }
        if (source instanceof TypeHolder<?>) {
            changeStream = IntStream.concat(changeStream,
                    ((TypeHolder<?>) source).getNestedTypes().parallelStream().mapToInt(this::addAnnotationsToEverything)
            );
        }
        return changes + changeStream.sum();
    }

    private JavaUnit loadJavaUnit(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream input = new BufferedInputStream(is)
        ) {
            return Roaster.parseUnit(input);
        }
    }

    private int addAnnotation(Object obj) {
        if (obj instanceof VisibilityScoped) {
            VisibilityScoped vis = (VisibilityScoped) obj;
            if (vis.isPackagePrivate() || vis.isPrivate()) {
                return 0;
            }
        }
        if (!(obj instanceof AnnotationTargetSource<?, ?>)) {
            return 0;
        }
        AnnotationTargetSource<?, ?> ats = (AnnotationTargetSource<?, ?>) obj;
        if (!ats.hasAnnotation(PowerNukkitOnly.class.getName())) {
            ats.addAnnotation(PowerNukkitOnly.class.getName());
            log.warn("Must add @PowerNukkitOnly to " + obj);
        }
        return 1;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int removeAnnotation(Object obj) {
        if (!(obj instanceof AnnotationTargetSource<?, ?>)) {
            return 0;
        }
        AnnotationTargetSource ats = (AnnotationTargetSource) obj;
        AnnotationSource annotation = ats.getAnnotation(PowerNukkitOnly.class.getName());
        if (annotation != null) {
            //ats.removeAnnotation(annotation);
            log.warn("Must remove @PowerNukkitOnly from " + obj);
            return 1;
        }
        return 0;
    }
}

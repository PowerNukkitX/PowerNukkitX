package org.powernukkit.tools;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.HumanStringComparator;
import com.google.common.base.Preconditions;
import com.google.gson.GsonBuilder;
import io.netty.util.internal.EmptyArrays;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.SpoonPom;
import spoon.support.reflect.declaration.CtConstructorImpl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author joserobjr
 * @since 2021-12-08
 */
@PowerNukkitOnly
@Since("FUTURE")
@Log4j2
public class AnnotationProblemScanner {
    private static final String NEED_TO_ADD_POWERNUKKIT_ONLY = "Need to add @PowerNukkitOnly to ";
    private static final String NEED_TO_REMOVE_POWERNUKKIT_ONLY = "Need to remove @PowerNukkitOnly from ";

    Map<String, CtType<?>> nukkitTypes;

    public static void main(String[] args) {
        Preconditions.checkArgument(args.length == 0 || args.length == 1, "Invalid arguments");
        Path clourburstNukkitSourceCode = Paths.get(args.length == 1? args[0] : "../../org.cloudburst/Nukkit");

        new AnnotationProblemScanner().execute(
                clourburstNukkitSourceCode,
                Paths.get(".")
        );
    }


    private static boolean isApi(CtModifiable obj) {
        if (obj instanceof CtMethod<?>) {
            CtMethod<?> method = (CtMethod<?>) obj;
            if (method.getSimpleName().equals("canEqual") && obj.hasAnnotation(Generated.class)) {
                return false;
            }
            if (method.getDeclaringType().isAnnotationType()) {
                return true;
            }
        }
        if (obj.isPublic() || obj.isProtected()) {
            return true;
        }
        if (obj instanceof CtField<?>) {
            CtType<?> declaringType = ((CtField<?>) obj).getDeclaringType();
            String fieldName = ((CtField<?>) obj).getSimpleName();
            fieldName = fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
            CtMethod<?> getter = declaringType.getMethod("get" + fieldName);
            if (getter == null || !getter.hasAnnotation(Generated.class) || !isApi(getter)) {
                getter = declaringType.getMethod("is" + fieldName);
            }
            if (getter != null && getter.hasAnnotation(Generated.class) && isApi(getter)) {
                return true;
            }
            CtMethod<?> setter = declaringType.getMethod("set" + fieldName, ((CtField<?>) obj).getType());
            if (setter != null && setter.hasAnnotation(Generated.class)) {
                return isApi(setter);
            }
        }
        return false;
    }

    private boolean isPowerNukkitOnlyExecutable(CtTypedElement<?> powerNukkitExecutable, CtType<?> nukkitType) {
        if (powerNukkitExecutable instanceof CtMethod<?>) {
            return isPowerNukkitOnlyMethod((CtMethod<?>) powerNukkitExecutable, nukkitType);
        } else {
            return isPowerNukkitOnlyConstructor((CtConstructorImpl<?>) powerNukkitExecutable, nukkitType);
        }
    }

    private boolean isPowerNukkitOnlyMethod(CtMethod<?> powerNukkitMethod, CtType<?> nukkitType) {
        String qualifiedName = powerNukkitMethod.getDeclaringType().getQualifiedName();
        if (qualifiedName.startsWith("java.")) {
            return false;
        }
        if (nukkitType == null) {
            nukkitType = nukkitTypes.get(qualifiedName);
        }
        if (nukkitType == null) {
            return true;
        }
        String name = powerNukkitMethod.getSimpleName();
        CtTypeReference<?>[] parameters = powerNukkitMethod.getParameters().stream().map(CtParameter::getType).toArray(CtTypeReference<?>[]::new);
        CtMethod<?> nkMethod = nukkitType.getMethod(name, parameters);
        if (nkMethod != null) {
            return !isApi(nkMethod);
        }

        String[] parameterTypes = Arrays.stream(parameters).map(CtTypeInformation::getQualifiedName).toArray(String[]::new);
        return nukkitType.getAllMethods().stream()
                        .filter(AnnotationProblemScanner::isApi)
                        .noneMatch(nukkitMethod ->
                                name.equals(nukkitMethod.getSimpleName())
                                        && Arrays.equals(parameterTypes,
                                                nukkitMethod.getParameters().stream()
                                                        .map(CtParameter::getType)
                                                        .map(CtTypeInformation::getQualifiedName)
                                                        .toArray(String[]::new)
                                        )
                        );
    }

    private boolean isPowerNukkitOnlyConstructor(CtConstructorImpl<?> constructor, CtType<?> nukkitType) {
        if (nukkitType == null) {
            nukkitType = nukkitTypes.get(constructor.getDeclaringType().getQualifiedName());
        }
        if (!(nukkitType instanceof CtClass<?>)) {
            return true;
        }
        CtClass<?> nukkitClass = (CtClass<?>) nukkitType;
        CtTypeReference<?>[] parameters = constructor.getParameters().stream().map(CtParameter::getType).toArray(CtTypeReference<?>[]::new);
        CtConstructorImpl<?> nkConstructor = (CtConstructorImpl<?>) nukkitClass.getConstructor(parameters);
        if (nkConstructor != null) {
            return !isApi(nkConstructor);
        }

        String[] parameterTypes = Arrays.stream(parameters).map(CtTypeInformation::getQualifiedName).toArray(String[]::new);
        return nukkitClass.getConstructors().stream()
                .filter(AnnotationProblemScanner::isApi)
                .noneMatch(nukkitConstructor -> Arrays.equals(parameterTypes,
                                nukkitConstructor.getParameters().stream()
                                        .map(CtParameter::getType)
                                        .map(CtTypeInformation::getQualifiedName)
                                        .toArray(String[]::new)
                        )
                );
    }

    @SneakyThrows
    private void cmd(Path dir, String... command) {
        int code = new ProcessBuilder()
                .command(command)
                .directory(dir.toAbsolutePath().normalize().toFile())
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor();
        Preconditions.checkArgument(code == 0, "Command Failed");
    }

    private void mvn(Path src, String... args) {
        List<String> parts = new ArrayList<>();
        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows")) {
            parts.add(src.resolve("mvnw.cmd").toAbsolutePath().normalize().toString());
        } else {
            parts.add("/bin/env");
            parts.add("sh");
            parts.add(src.resolve("mvnw").toAbsolutePath().normalize().toString());
        }
        parts.addAll(Arrays.asList(args));
        cmd(src, parts.toArray(EmptyArrays.EMPTY_STRINGS));
    }

    private void git(Path src, String... args) {
        List<String> parts = new ArrayList<>();
        if (!System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("windows")) {
            parts.add("/bin/env");
        }
        parts.add("git");
        parts.addAll(Arrays.asList(args));
        cmd(src, parts.toArray(EmptyArrays.EMPTY_STRINGS));
    }

    private void delombok(Path src) {
        log.info("Delomboking " + src.toAbsolutePath().normalize());
        mvn(src, "clean", "lombok:delombok", "dependency:build-classpath", "-Dmdep.outputFile=target/classpath.txt");
    }

    private void delombokPatch(Path src) {
        log.info("Adding delombok plugin to " + src.toAbsolutePath().normalize());
        Path nukkitPatch = Paths.get("src/test/resources/org/powernukkit/tools/nukkit.patch");
        git(src, "reset", "--hard", "HEAD");
        git(src, "apply", "-3", nukkitPatch.toAbsolutePath().normalize().toString());
    }

    @SneakyThrows
    private Launcher createLauncher(Path src) {
        log.info("Creating launcher for " + src.toAbsolutePath().normalize());
        Launcher launcher = new Launcher();
        SpoonPom pom = new SpoonPom(src.toAbsolutePath().normalize().toString(), MavenLauncher.SOURCE_TYPE.APP_SOURCE, launcher.getEnvironment());

        List<File> sourceDirectories = new ArrayList<>(pom.getSourceDirectories());
        File srcMainJava = src.resolve("src/main/java").toAbsolutePath().normalize().toFile();
        sourceDirectories.removeIf(file -> file.getAbsoluteFile().equals(srcMainJava));
        sourceDirectories.add(src.resolve("target/delombok").toAbsolutePath().normalize().toFile());
        sourceDirectories.forEach(file -> launcher.addInputResource(file.toPath().toAbsolutePath().normalize().toString()));

        String[] classpath = String.join("\n", Files.readAllLines(src.resolve("target/classpath.txt"))).split(File.pathSeparator);
        log.info("Classpath: " + Arrays.asList(classpath));

        launcher.getEnvironment().setNoClasspath(false);
        launcher.getModelBuilder().setSourceClasspath(classpath);

        launcher.getEnvironment().setComplianceLevel(pom.getSourceVersion());

        return launcher;
    }

    @SneakyThrows
    private void execute(Path nukkitSrc, Path powerNukkitSrc) {
        delombokPatch(nukkitSrc);
        delombok(nukkitSrc);
        Launcher nukkitLauncher = createLauncher(nukkitSrc);

        log.info("Building " + powerNukkitSrc.toAbsolutePath().normalize());
        mvn(powerNukkitSrc,
                "clean", "lombok:delombok", "dependency:build-classpath", "-Dmdep.outputFile=target/classpath.txt",
                "compile", "compiler:testCompile");
        Launcher powerNukkitLauncher = createLauncher(powerNukkitSrc);

        log.info("Builing model for " + nukkitSrc.toAbsolutePath().normalize());
        CtModel nukkitModel = nukkitLauncher.buildModel();

        log.info("Building model for " + powerNukkitSrc.toAbsolutePath().normalize());
        CtModel powerNukkitModel = powerNukkitLauncher.buildModel();

        log.info("Caching Nukkit types");
        nukkitTypes = nukkitModel.getAllTypes().stream().collect(Collectors.toMap(CtType::getQualifiedName, Function.identity()));

        log.info("Verifying annotations...");
        Map<String, NeededClassChanges> neededClassChanges = powerNukkitModel.getAllTypes().parallelStream()
                .map(type-> checkType(type, nukkitTypes))
                .peek(NeededClassChanges::close)
                .filter(NeededClassChanges::isNotEmpty)
                .collect(Collectors.toMap(NeededClassChanges::getName, Function.identity(),
                        (a,b)-> { throw new UnsupportedOperationException("Can't combine " + a + " with " + b); },
                        ()-> new TreeMap<>(HumanStringComparator.getInstance())));

        Path jsonFile = Paths.get("dumps/needed-class-changes.json").toAbsolutePath().normalize();
        log.info("Creating ..." + jsonFile);
        neededClassChanges.values().forEach(NeededClassChanges::removeName);
        Files.write(jsonFile,
                new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                        .toJson(neededClassChanges)
                                .getBytes(StandardCharsets.UTF_8)
        );

        log.info("Process completed");
    }

    private NeededClassChanges checkType(CtType<?> powerNukkitType, Map<String, CtType<?>> nukkitTypes) {
        CtType<?> nukkitType = nukkitTypes.get(powerNukkitType.getQualifiedName());
        if (nukkitType == null) {
            return addPowerNukkitOnlyToEverything(powerNukkitType);
        } else {
            return compareTypes(powerNukkitType, nukkitType);
        }
    }

    private NeededClassChanges addPowerNukkitOnlyToEverything(CtType<?> powerNukkitType) {
        NeededClassChanges neededClassChanges = new NeededClassChanges(powerNukkitType.getQualifiedName());
        needsPowerNukkitOnly(neededClassChanges, powerNukkitType);

        Set<CtField<?>> powerNukkitOnlyFields = powerNukkitType.getFields().stream()
                .filter(AnnotationProblemScanner::isApi)
                .peek(field -> needsPowerNukkitOnly(neededClassChanges, field))
                .collect(Collectors.toSet());

        checkForMissingOverrides(neededClassChanges, powerNukkitType);

        Set<CtMethod<?>> powerNukkitOnlyMethods = powerNukkitType.getMethods().stream()
                .filter(AnnotationProblemScanner::isApi)
                .filter(method -> method.isStatic()
                        || method.getTopDefinitions().isEmpty()
                        || method.getTopDefinitions().stream().allMatch(powerNukkitMethod -> isPowerNukkitOnlyMethod(powerNukkitMethod, null)))
                .peek(method -> needsPowerNukkitOnly(neededClassChanges, method))
                .collect(Collectors.toSet());

        constructorStream(powerNukkitType)
                .filter(it -> isApi((CtModifiable) it))
                .forEachOrdered(method -> needsPowerNukkitOnly(neededClassChanges, method));

        powerNukkitType.getFields().stream()
                .filter(field -> !powerNukkitOnlyFields.contains(field))
                .forEachOrdered(field -> dontNeedsPowerNukkitOnly(neededClassChanges, field));

        powerNukkitType.getMethods().stream()
                .filter(method-> !powerNukkitOnlyMethods.contains(method))
                .forEachOrdered(method -> dontNeedsPowerNukkitOnly(neededClassChanges, method));

        //powerNukkitType.getNestedTypes().forEach(this::addPowerNukkitOnlyToEverything);
        return neededClassChanges;
    }

    private NeededClassChanges compareTypes(CtType<?> powerNukkitType, CtType<?> nukkitType) {
        final NeededClassChanges neededClassChanges = new NeededClassChanges(powerNukkitType.getQualifiedName());
        dontNeedsPowerNukkitOnly(neededClassChanges, powerNukkitType);

        List<CtField<?>> powerNukkitOnlyFields = powerNukkitType.getFields().stream()
                .filter(AnnotationProblemScanner::isApi)
                .filter(powerNukkitField -> compareFields(powerNukkitField, nukkitType))
                .peek(field -> needsPowerNukkitOnly(neededClassChanges, field))
                .collect(Collectors.toList());

        checkForMissingOverrides(neededClassChanges, powerNukkitType);

        List<CtTypedElement<?>> powerNukkitOnlyMethods =
                Stream.concat(powerNukkitType.getMethods().stream(), constructorStream(powerNukkitType))
                        .filter(it -> isApi((CtModifiable) it))
                        .filter(powerNukkitMethod -> isPowerNukkitOnlyExecutable(powerNukkitMethod, nukkitType))
                        .peek(method -> needsPowerNukkitOnly(neededClassChanges, method))
                        .collect(Collectors.toList());

        powerNukkitType.getFields().stream()
                .filter(field -> !powerNukkitOnlyFields.contains(field))
                .forEachOrdered(field -> dontNeedsPowerNukkitOnly(neededClassChanges, field));

        Stream.concat(powerNukkitType.getMethods().stream(), constructorStream(powerNukkitType))
                .filter(method-> !powerNukkitOnlyMethods.contains(method))
                .forEachOrdered(method -> dontNeedsPowerNukkitOnly(neededClassChanges, method));

        return neededClassChanges;
    }

    private Stream<CtTypedElement<?>> constructorStream(CtType<?> type) {
        if (type instanceof CtClass<?>) {
            return ((CtClass<?>) type).getConstructors().stream().map(it -> (CtTypedElement<?>) it);
        } else {
            return Stream.empty();
        }
    }

    private boolean compareFields(CtField<?> powerNukkitField, CtType<?> nukkitType) {
        CtField<?> nukkitField = nukkitType.getField(powerNukkitField.getSimpleName());
        if (nukkitField == null || !isApi(nukkitField)) {
            return true;
        }
        CtTypeReference<?> nukkitFieldType = nukkitField.getType();
        CtTypeReference<?> powerNukkitFieldType = powerNukkitField.getType();

        boolean valid = false;
        if (nukkitFieldType.isGenerics()) {
            if (powerNukkitFieldType.isGenerics()) {
                valid = nukkitFieldType.getSimpleName().equals(powerNukkitFieldType.getSimpleName()) &&
                        nukkitFieldType.getTypeErasure().getQualifiedName().equals(powerNukkitFieldType.getTypeErasure().getQualifiedName());
            }
        } else if (!powerNukkitFieldType.isGenerics()) {
            valid = nukkitFieldType.getTypeDeclaration().getQualifiedName().equals(powerNukkitFieldType.getTypeDeclaration().getQualifiedName());
        }
        if (!valid) {
            log.error("Incompatible field declared at " + powerNukkitField.getDeclaringType().getQualifiedName() + "#" + powerNukkitField.getSimpleName());
            return true;
        }
        return false;
    }

    private void checkForMissingOverrides(NeededClassChanges neededClassChanges, CtType<?> powerNukkitType) {
        powerNukkitType.getMethods().stream()
                .filter(AnnotationProblemScanner::isApi)
                .filter(method -> !method.isStatic())
                .filter(method -> !method.getTopDefinitions().isEmpty())
                .filter(method -> !method.hasAnnotation(Override.class))
                .filter(method -> !method.hasAnnotation(Generated.class))
                .map(this::missingOverride)
                .forEachOrdered(neededClassChanges.addOverrideAnnotation::add);
    }

    private String missingOverride(CtMethod<?> method) {
        String sig = methodString(method);
        log.warn("Missing @Override at " + sig);
        method.getFactory().Annotation().annotate(method, Override.class);
        return sig;
    }

    private String methodString(CtTypedElement<?> method) {
        return ((CtTypeMember) method).getDeclaringType().getQualifiedName() + "#" + ((CtNamedElement)method).getSimpleName()
                + "("
                + getParameters(method).stream()
                        .map(param -> param.getType().getSimpleName()).collect(Collectors.joining(", "))
                + ")";
    }

    private List<CtParameter<?>> getParameters(CtTypedElement<?> method) {
        if (method instanceof CtMethod<?>) {
            return ((CtMethod<?>) method).getParameters();
        } else {
            return ((CtConstructorImpl<?>) method).getParameters();
        }
    }

    private void needsPowerNukkitOnly(NeededClassChanges neededClassChanges, CtType<?> type) {
        if (!type.hasAnnotation(PowerNukkitOnly.class)) {
            log.info(NEED_TO_ADD_POWERNUKKIT_ONLY + type.getQualifiedName());
            neededClassChanges.addPowerNukkitOnlyAnnotation.add(type.getQualifiedName());
        }
    }

    private void needsPowerNukkitOnly(NeededClassChanges neededClassChanges, CtField<?> field) {
        if (!field.hasAnnotation(PowerNukkitOnly.class)) {
            String sig = field.getDeclaringType().getQualifiedName() + "#" + field.getSimpleName();
            log.info(NEED_TO_ADD_POWERNUKKIT_ONLY + sig);
            neededClassChanges.addPowerNukkitOnlyAnnotation.add(sig);
        }
    }

    private void needsPowerNukkitOnly(NeededClassChanges neededClassChanges, CtTypedElement<?> method) {
        if (!method.hasAnnotation(PowerNukkitOnly.class)) {
            String sig = methodString(method);
            log.info(NEED_TO_ADD_POWERNUKKIT_ONLY + sig);
            neededClassChanges.addPowerNukkitOnlyAnnotation.add(sig);
        }
    }

    private void dontNeedsPowerNukkitOnly(NeededClassChanges neededClassChanges, CtType<?> type) {
        if (type.hasAnnotation(PowerNukkitOnly.class)) {
            log.info(NEED_TO_REMOVE_POWERNUKKIT_ONLY + type.getQualifiedName());
            neededClassChanges.removePowerNukkitOnlyAnnotation.add(type.getQualifiedName());
        }
    }

    private void dontNeedsPowerNukkitOnly(NeededClassChanges neededClassChanges, CtField<?> field) {
        if (field.hasAnnotation(PowerNukkitOnly.class)) {
            String sig = field.getDeclaringType().getQualifiedName() + "#" + field.getSimpleName();
            log.info(NEED_TO_REMOVE_POWERNUKKIT_ONLY + sig);
            neededClassChanges.removePowerNukkitOnlyAnnotation.add(sig);
        }
    }

    private void dontNeedsPowerNukkitOnly(NeededClassChanges neededClassChanges, CtTypedElement<?> method) {
        if (method.hasAnnotation(PowerNukkitOnly.class)) {
            String sig = methodString(method);
            log.info(NEED_TO_REMOVE_POWERNUKKIT_ONLY + sig);
            neededClassChanges.removePowerNukkitOnlyAnnotation.add(sig);
        }
    }

    @Getter
    static class NeededClassChanges {
        private String name;

        private List<String> addOverrideAnnotation = new ArrayList<>();
        private List<String> addPowerNukkitOnlyAnnotation = new ArrayList<>();
        private List<String> removePowerNukkitOnlyAnnotation = new ArrayList<>();

        NeededClassChanges(String name) {
            this.name = name;
        }

        private boolean isEmpty() {
            return isEmpty(addOverrideAnnotation)
                    && isEmpty(addPowerNukkitOnlyAnnotation)
                    && isEmpty(removePowerNukkitOnlyAnnotation);
        }

        private boolean isNotEmpty() {
            return !isEmpty();
        }

        private boolean isEmpty(List<?> list) {
            return list == null || list.isEmpty();
        }

        private void close() {
            if (isEmpty(addOverrideAnnotation)) {
                addOverrideAnnotation = null;
            }
            if (isEmpty(addPowerNukkitOnlyAnnotation)) {
                addPowerNukkitOnlyAnnotation = null;
            }
            if (isEmpty(removePowerNukkitOnlyAnnotation)) {
                removePowerNukkitOnlyAnnotation = null;
            }
        }

        private void removeName() {
            name = null;
        }
    }
}

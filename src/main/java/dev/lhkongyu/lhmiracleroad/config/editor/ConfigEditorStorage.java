package dev.lhkongyu.lhmiracleroad.config.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import dev.lhkongyu.lhmiracleroad.config.ShowGuiAttributeConfig;
import dev.lhkongyu.lhmiracleroad.config.SoulItemConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

final class ConfigEditorStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final System.Logger LOGGER = System.getLogger(ConfigEditorStorage.class.getName());
    private static final Path OCCUPATIONS = Path.of("Occupation");
    private static final Path SHOW_GUI_ATTRIBUTE = Path.of(ShowGuiAttributeConfig.FILE_NAME);
    private static final Path SOUL = Path.of(SoulItemConfig.FILE_NAME);
    private static final Path STRENGTHEN = Path.of("strengthen");
    private static final Path WEAPON_TYPES = STRENGTHEN.resolve("weapon_types.json");
    private static final Path EQUIPMENT = Path.of("punishment");
    private static final Path PRESETS = EQUIPMENT.resolve("punishment.json");
    private static final Path EQUIPMENT_MARKER = EQUIPMENT.resolve(".editor-managed");
    private static final Path TROPHIES = Path.of("trophy");
    private static final Path COMMON = Path.of(CommonConfigEditor.FILE_NAME);
    private static final List<String> STRENGTHEN_KEYS = List.of("weapon", "ranged_weapon", "magic_staffs", "tool", "armor");
    private static final List<String> WEAPON_TYPE_KEYS = List.of("melee", "ranged", "magic_staff");
    private static final List<String> TROPHY_KEYS = List.of("common", "exquisite", "rare", "epic", "legend");
    private static final Pattern OCCUPATION_ID = Pattern.compile("[a-zA-Z0-9_-][a-zA-Z0-9_.-]*");
    private static final Pattern ITEM_SEGMENT = Pattern.compile("[a-z0-9_.-]+");
    private static final Pattern WINDOWS_DEVICE = Pattern.compile("(?i)(con|prn|aux|nul|com[1-9]|lpt[1-9])(?:\\..*)?");

    private final Path root;

    ConfigEditorStorage(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    synchronized Snapshot read(ConfigSection section) throws IOException {
        return snapshot(section, readFiles(section));
    }

    synchronized boolean canRestore(ConfigSection section) throws IOException {
        return fileAttributes(backupPath(section)) != null;
    }

    synchronized Snapshot previous(ConfigSection section) throws IOException {
        return snapshot(section, readBackup(section, readFile(backupPath(section)), revision(readFiles(section))));
    }

    synchronized Snapshot save(ConfigSection section, String revision, JsonObject document) throws IOException {
        return save(section, revision, document, () -> {});
    }

    synchronized Snapshot save(ConfigSection section, String revision, JsonObject document, Reload reload) throws IOException {
        Map<Path, byte[]> current = readFiles(section);
        checkRevision(revision, current);
        Map<Path, byte[]> desired = planSave(section, current, document);
        Snapshot saved = snapshot(section, desired);
        List<Change> changes = changes(current, desired);
        if (!changes.isEmpty()) {
            Path backup = backupPath(section);
            // Publish the previous file bytes before replacing the first live file.
            changes.add(0, new Change(backup, readFile(backup), encodeBackup(current, saved.revision())));
            commit(section, revision, changes, reload);
        }
        return saved;
    }

    synchronized Snapshot restore(ConfigSection section, String revision) throws IOException {
        return restore(section, revision, () -> {});
    }

    synchronized Snapshot restore(ConfigSection section, String revision, Reload reload) throws IOException {
        Map<Path, byte[]> current = readFiles(section);
        checkRevision(revision, current);
        Path backup = backupPath(section);
        byte[] previous = readFile(backup);
        Map<Path, byte[]> desired = readBackup(section, previous, revision);
        Snapshot restored = snapshot(section, desired);
        List<Change> changes = changes(current, desired);
        changes.add(new Change(backup, previous, null));
        commit(section, revision, changes, reload);
        return restored;
    }

    private Snapshot snapshot(ConfigSection section, Map<Path, byte[]> files) throws IOException {
        JsonObject document = new JsonObject();
        switch (section) {
            case OCCUPATION -> indexOccupations(document, parseOccupationFiles(files));
            case SHOW_GUI_ATTRIBUTE -> document.add(ShowGuiAttributeConfig.KEY, displayedAttributes(files));
            case STRENGTHEN -> readArrays(document, files, STRENGTHEN, STRENGTHEN_KEYS);
            case WEAPON_TYPES -> {
                JsonObject data = readObject(files, WEAPON_TYPES);
                for (String key : WEAPON_TYPE_KEYS) document.add(key, array(data.get(key), WEAPON_TYPES));
            }
            case EQUIPMENT -> {
                JsonObject items = new JsonObject();
                for (Path file : files.keySet()) {
                    if (isEquipmentFile(file)) items.add(equipmentId(file), readObject(files, file));
                }
                document.add("items", items);
                document.add("presets", array(parse(files, PRESETS), PRESETS));
            }
            case TROPHY -> readArrays(document, files, TROPHIES, TROPHY_KEYS);
            case COMMON -> { return new Snapshot(revision(files), CommonConfigEditor.snapshot(files.get(COMMON))); }
            case SOUL -> { return new Snapshot(revision(files), SoulItemConfig.toJson(SoulItemConfig.fromJson(readSoul(files)))); }
        }
        return new Snapshot(revision(files), document);
    }

    private Map<Path, byte[]> planSave(ConfigSection section, Map<Path, byte[]> current, JsonObject document) throws IOException {
        Map<Path, byte[]> desired = new TreeMap<>(current);
        switch (section) {
            case OCCUPATION -> writeOccupations(current, desired, document);
            case SHOW_GUI_ATTRIBUTE -> replaceJson(desired, SHOW_GUI_ATTRIBUTE, displayedAttributes(current),
                    array(document.get(ShowGuiAttributeConfig.KEY), SHOW_GUI_ATTRIBUTE));
            case STRENGTHEN -> writeArrays(current, desired, document, STRENGTHEN, STRENGTHEN_KEYS);
            case WEAPON_TYPES -> {
                JsonObject original = readObject(current, WEAPON_TYPES);
                JsonObject updated = original.deepCopy();
                for (String key : WEAPON_TYPE_KEYS) {
                    JsonArray value = array(document.get(key), WEAPON_TYPES);
                    if (!value.equals(array(original.get(key), WEAPON_TYPES))) updated.add(key, value);
                }
                replaceJson(desired, WEAPON_TYPES, original, updated);
            }
            case EQUIPMENT -> {
                JsonElement items = document.get("items");
                if (items == null || !items.isJsonObject()) throw new IOException("Expected equipment item configurations");
                desired.keySet().removeIf(ConfigEditorStorage::isEquipmentFile);
                for (var entry : items.getAsJsonObject().entrySet()) {
                    Path file = equipmentPath(entry.getKey());
                    JsonElement item = entry.getValue();
                    if (!item.isJsonObject()) throw new IOException("Invalid equipment entry: " + entry.getKey());
                    byte[] original = current.get(file);
                    desired.put(file, original != null && item.equals(readObject(current, file)) ? original : encode(item));
                }
                replaceJson(desired, PRESETS, array(parse(current, PRESETS), PRESETS), array(document.get("presets"), PRESETS));
                desired.putIfAbsent(EQUIPMENT_MARKER, "true\n".getBytes(StandardCharsets.UTF_8));
            }
            case TROPHY -> writeArrays(current, desired, document, TROPHIES, TROPHY_KEYS);
            case COMMON -> desired.put(COMMON, CommonConfigEditor.write(current.get(COMMON), document));
            case SOUL -> writeSoul(current, desired, document);
        }
        return desired;
    }

    private static JsonObject readSoul(Map<Path, byte[]> files) throws IOException {
        JsonElement root = parse(files, SOUL);
        if (root == null) return new JsonObject();
        if (root instanceof JsonObject object) return object;
        throw new IOException("Expected a JSON object in " + SOUL);
    }

    private static void writeSoul(Map<Path, byte[]> current, Map<Path, byte[]> desired,
                                  JsonObject document) throws IOException {
        JsonObject original = readSoul(current);
        JsonObject snapshot = SoulItemConfig.toJson(SoulItemConfig.fromJson(original));
        JsonObject updated = original.deepCopy();
        for (String key : snapshot.keySet()) {
            JsonObject before = snapshot.getAsJsonObject(key);
            JsonObject after = document.getAsJsonObject(key);
            if (before.equals(after)) continue;

            JsonObject section = updated.get(key) instanceof JsonObject existing ? existing : new JsonObject();
            for (var field : after.entrySet()) {
                if (!field.getValue().equals(before.get(field.getKey()))) {
                    section.add(field.getKey(), field.getValue());
                }
            }
            updated.add(key, section);
        }
        replaceJson(desired, SOUL, original, updated);
    }

    private static JsonArray displayedAttributes(Map<Path, byte[]> files) throws IOException {
        if (!files.containsKey(SHOW_GUI_ATTRIBUTE)) return ShowGuiAttributeConfig.defaultDocument();
        JsonElement root = parse(files, SHOW_GUI_ATTRIBUTE);
        if (!root.isJsonArray()) throw new IOException("Expected an array in " + SHOW_GUI_ATTRIBUTE);
        return root.getAsJsonArray();
    }

    private static void readArrays(JsonObject document, Map<Path, byte[]> files, Path directory, List<String> keys) throws IOException {
        for (String key : keys) {
            Path file = directory.resolve(key + ".json");
            document.add(key, array(parse(files, file), file));
        }
    }

    private static void writeArrays(Map<Path, byte[]> current, Map<Path, byte[]> desired, JsonObject document,
                                    Path directory, List<String> keys) throws IOException {
        for (String key : keys) {
            Path file = directory.resolve(key + ".json");
            replaceJson(desired, file, array(parse(current, file), file), array(document.get(key), file));
        }
    }

    private static Map<Path, JsonElement> parseOccupationFiles(Map<Path, byte[]> files) throws IOException {
        Map<Path, JsonElement> parsed = new LinkedHashMap<>();
        for (Path file : files.keySet().stream().filter(path -> path.startsWith(OCCUPATIONS))
                .sorted(Comparator.comparing(Path::toString)).toList()) {
            JsonElement data = parse(files, file);
            if (!data.isJsonNull() && !data.isJsonObject() && !data.isJsonArray()) {
                throw new IOException("Expected an occupation object or array in " + file);
            }
            parsed.put(file, data);
        }
        return parsed;
    }

    private static void addOccupation(JsonObject document, JsonElement entry) {
        String id = occupationId(entry);
        if (id != null) document.add(id, entry);
    }

    private static String occupationId(JsonElement entry) {
        if (!entry.isJsonObject()) return null;
        JsonElement id = entry.getAsJsonObject().get("id");
        if (id == null || !id.isJsonPrimitive() || !id.getAsJsonPrimitive().isString() || id.getAsString().isBlank()) return null;
        return id.getAsString();
    }

    private static void indexOccupations(JsonObject index, Map<Path, JsonElement> files) {
        // The game loads the last entry when legacy files contain duplicate IDs.
        for (JsonElement data : files.values()) {
            if (data.isJsonArray()) {
                for (JsonElement entry : data.getAsJsonArray()) addOccupation(index, entry);
            } else {
                addOccupation(index, data);
            }
        }
    }

    private static void writeOccupations(Map<Path, byte[]> current, Map<Path, byte[]> desired, JsonObject document) throws IOException {
        Map<Path, JsonElement> parsed = parseOccupationFiles(current);
        JsonObject existing = new JsonObject();
        indexOccupations(existing, parsed);
        for (var file : parsed.entrySet()) {
            JsonElement original = file.getValue();
            JsonElement update;
            if (original.isJsonArray()) {
                JsonArray entries = new JsonArray();
                for (JsonElement entry : original.getAsJsonArray()) {
                    JsonElement updated = editOccupation(entry, existing, document);
                    if (updated != null) entries.add(updated);
                }
                update = entries;
            } else {
                update = editOccupation(original, existing, document);
            }
            if (update == null) desired.remove(file.getKey());
            else replaceJson(desired, file.getKey(), original, update);
        }
        for (var entry : document.entrySet()) {
            String id = entry.getKey();
            if (!id.equals(occupationId(entry.getValue()))) throw new IOException("Occupation ID does not match: " + id);
            if (existing.has(id)) continue;
            if (!OCCUPATION_ID.matcher(id).matches()) throw new IOException("Invalid occupation ID: " + id);
            Path file = OCCUPATIONS.resolve(id + ".json");
            if (desired.containsKey(file)) throw new IOException("Occupation file already exists: " + file);
            desired.put(file, encode(entry.getValue()));
        }
    }

    private static JsonElement editOccupation(JsonElement original, JsonObject existing, JsonObject document) {
        String id = occupationId(original);
        if (id == null) return original;
        if (!document.has(id)) return null;
        return existing.get(id) == original ? document.get(id) : original;
    }

    private static String equipmentId(Path file) {
        Path relative = EQUIPMENT.relativize(file);
        String resource = pathString(relative.subpath(1, relative.getNameCount()));
        return relative.getName(0) + ":" + resource.substring(0, resource.length() - ".json".length());
    }

    private static Path equipmentPath(String id) throws IOException {
        String[] parts = id.split(":", -1);
        if (parts.length != 2 || !itemSegment(parts[0])) throw new IOException("Invalid equipment ID: " + id);
        Path file = EQUIPMENT.resolve(parts[0]);
        String[] segments = parts[1].split("/", -1);
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (!itemSegment(segment)) throw new IOException("Invalid equipment ID: " + id);
            file = file.resolve(i == segments.length - 1 ? segment + ".json" : segment);
        }
        return file;
    }

    private static boolean itemSegment(String segment) {
        return ITEM_SEGMENT.matcher(segment).matches() && !segment.equals(".") && !segment.equals("..");
    }

    private static JsonObject readObject(Map<Path, byte[]> files, Path file) throws IOException {
        JsonElement element = parse(files, file);
        if (element == null || element.isJsonNull()) return new JsonObject();
        if (!element.isJsonObject()) throw new IOException("Expected a JSON object in " + file);
        return element.getAsJsonObject();
    }

    private static JsonArray array(JsonElement element, Path file) throws IOException {
        if (element == null || element.isJsonNull()) return new JsonArray();
        if (!element.isJsonArray()) throw new IOException("Expected a JSON array in " + file);
        return element.getAsJsonArray();
    }

    private static JsonElement parse(Map<Path, byte[]> files, Path file) throws IOException {
        byte[] bytes = files.get(file);
        if (bytes == null) return null;
        try {
            return JsonParser.parseString(StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString());
        } catch (JsonParseException | IOException failure) {
            throw new IOException("Invalid JSON in " + file, failure);
        }
    }

    private static byte[] encode(JsonElement data) {
        return (GSON.toJson(data) + "\n").getBytes(StandardCharsets.UTF_8);
    }

    private static void replaceJson(Map<Path, byte[]> desired, Path file, JsonElement original, JsonElement update) {
        if (!original.equals(update)) desired.put(file, encode(update));
    }

    private Map<Path, byte[]> readFiles(ConfigSection section) throws IOException {
        Map<Path, byte[]> files = new TreeMap<>();
        switch (section) {
            case OCCUPATION -> readDirectory(section, OCCUPATIONS, files);
            case SHOW_GUI_ATTRIBUTE -> readInto(files, SHOW_GUI_ATTRIBUTE);
            case STRENGTHEN -> readKnownFiles(files, STRENGTHEN, STRENGTHEN_KEYS);
            case WEAPON_TYPES -> readInto(files, WEAPON_TYPES);
            case EQUIPMENT -> readDirectory(section, EQUIPMENT, files);
            case TROPHY -> readKnownFiles(files, TROPHIES, TROPHY_KEYS);
            case COMMON -> readInto(files, COMMON);
            case SOUL -> readInto(files, SOUL);
        }
        return files;
    }

    private void readDirectory(ConfigSection section, Path directory, Map<Path, byte[]> files) throws IOException {
        Path base = checkedPath(directory);
        BasicFileAttributes attributes = attributes(base);
        if (attributes == null) return;
        if (!attributes.isDirectory()) throw new IOException("Expected a configuration directory: " + base);
        Files.walkFileTree(base, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
                checkedPath(root.relativize(path));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                Path file = root.relativize(path);
                if (isSectionFile(section, file)) readInto(files, file);
                else if (attributes.isSymbolicLink() || attributes.isOther()) checkedPath(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static boolean isSectionFile(ConfigSection section, Path file) {
        if (section == ConfigSection.COMMON) return file.equals(COMMON);
        if (section == ConfigSection.EQUIPMENT && file.equals(EQUIPMENT_MARKER)) return true;
        String name = file.getFileName().toString();
        if (!name.endsWith(".json")) return false;
        return switch (section) {
            case OCCUPATION -> file.startsWith(OCCUPATIONS) && file.getNameCount() > 1;
            case SHOW_GUI_ATTRIBUTE -> file.equals(SHOW_GUI_ATTRIBUTE);
            case STRENGTHEN -> STRENGTHEN.equals(file.getParent()) && STRENGTHEN_KEYS.contains(name.substring(0, name.length() - 5));
            case WEAPON_TYPES -> file.equals(WEAPON_TYPES);
            case EQUIPMENT -> file.equals(PRESETS) || isEquipmentFile(file);
            case TROPHY -> TROPHIES.equals(file.getParent()) && TROPHY_KEYS.contains(name.substring(0, name.length() - 5));
            case SOUL -> file.equals(SOUL);
            case COMMON -> false;
        };
    }

    private static boolean isEquipmentFile(Path file) {
        if (!file.startsWith(EQUIPMENT) || file.getNameCount() < 3) return false;
        try {
            return equipmentPath(equipmentId(file)).equals(file);
        } catch (IOException invalidId) {
            return false;
        }
    }

    private void readKnownFiles(Map<Path, byte[]> files, Path directory, List<String> keys) throws IOException {
        for (String key : keys) readInto(files, directory.resolve(key + ".json"));
    }

    private void readInto(Map<Path, byte[]> files, Path file) throws IOException {
        byte[] data = readFile(file);
        if (data != null) files.put(file, data);
    }

    private byte[] readFile(Path file) throws IOException {
        Path path = checkedPath(file);
        if (regularFileAttributes(path) == null) return null;
        try (var input = Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS)) {
            return input.readAllBytes();
        }
    }

    private BasicFileAttributes fileAttributes(Path file) throws IOException {
        return regularFileAttributes(checkedPath(file));
    }

    private static BasicFileAttributes regularFileAttributes(Path path) throws IOException {
        BasicFileAttributes attributes = attributes(path);
        if (attributes != null && !attributes.isRegularFile()) throw new IOException("Expected a configuration file: " + path);
        return attributes;
    }

    private Path checkedPath(Path file) throws IOException {
        Path target = root.resolve(file);
        if (file.isAbsolute() || !target.normalize().equals(target) || !target.startsWith(root)) {
            throw new IOException("Invalid configuration path: " + file);
        }
        Path current = target.getRoot();
        for (Path part : target) {
            current = current.resolve(part);
            if (current.startsWith(root) && !current.equals(root) && "\\".equals(target.getFileSystem().getSeparator())
                    && (part.toString().endsWith(".") || part.toString().endsWith(" ") || WINDOWS_DEVICE.matcher(part.toString()).matches())) {
                throw new IOException("Unsafe Windows configuration path: " + current);
            }
            BasicFileAttributes attributes = attributes(current);
            if (attributes != null && (attributes.isSymbolicLink() || attributes.isOther()
                    || !current.equals(target) && !attributes.isDirectory())) {
                throw new IOException("Unsafe configuration path: " + current);
            }
        }
        return target;
    }

    private static BasicFileAttributes attributes(Path path) throws IOException {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (NoSuchFileException missing) {
            return null;
        }
    }

    private static String revision(Map<Path, byte[]> files) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException failure) {
            throw new IllegalStateException(failure);
        }
        for (var entry : files.entrySet()) {
            byte[] path = pathString(entry.getKey()).getBytes(StandardCharsets.UTF_8);
            digest.update(ByteBuffer.allocate(8).putInt(path.length).putInt(entry.getValue().length).array());
            digest.update(path);
            digest.update(entry.getValue());
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static void checkRevision(String expected, Map<Path, byte[]> current) throws ConflictException {
        if (!revision(current).equals(expected)) throw new ConflictException();
    }

    private static String pathString(Path path) {
        return path.toString().replace(path.getFileSystem().getSeparator(), "/");
    }

    private static Path backupPath(ConfigSection section) {
        Path backup = Path.of(".config-editor", section.name().toLowerCase(Locale.ROOT) + ".json");
        return section == ConfigSection.COMMON ? Path.of("lhmiracleroad").resolve(backup) : backup;
    }

    private static byte[] encodeBackup(Map<Path, byte[]> files, String replacesRevision) {
        JsonObject contents = new JsonObject();
        files.forEach((file, bytes) -> contents.addProperty(pathString(file), Base64.getEncoder().encodeToString(bytes)));
        JsonObject backup = new JsonObject();
        backup.addProperty("replacesRevision", replacesRevision);
        backup.add("files", contents);
        return encode(backup);
    }

    private Map<Path, byte[]> readBackup(ConfigSection section, byte[] bytes, String currentRevision) throws IOException {
        if (bytes == null) throw new IOException("No previous configuration for " + section);
        Map<Path, byte[]> result = new TreeMap<>();
        try {
            JsonObject backup = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
            if (backup.has("files")) {
                if (!currentRevision.equals(backup.get("replacesRevision").getAsString())) {
                    throw new IOException("Cannot restore this backup because the configuration changed after it was saved");
                }
                backup = backup.getAsJsonObject("files");
            }
            for (var entry : backup.entrySet()) {
                Path path = Path.of(entry.getKey());
                if (path.isAbsolute() || !entry.getKey().equals(pathString(path)) || !isSectionFile(section, path)) {
                    throw new IOException("Invalid configuration backup path: " + entry.getKey());
                }
                checkedPath(path);
                if (result.put(path, Base64.getDecoder().decode(entry.getValue().getAsString())) != null) {
                    throw new IOException("Duplicate configuration backup path: " + path);
                }
            }
        } catch (JsonParseException | IllegalArgumentException | IllegalStateException | UnsupportedOperationException failure) {
            throw new IOException("Invalid configuration backup for " + section, failure);
        }
        return result;
    }

    private static List<Change> changes(Map<Path, byte[]> current, Map<Path, byte[]> desired) {
        var paths = new TreeSet<>(current.keySet());
        paths.addAll(desired.keySet());
        List<Change> result = new ArrayList<>();
        for (Path path : paths) {
            byte[] before = current.get(path);
            byte[] after = desired.get(path);
            if (!Arrays.equals(before, after)) result.add(new Change(path, before, after));
        }
        return result;
    }

    private void commit(ConfigSection section, String expected, List<Change> changes, Reload reload) throws IOException {
        List<Path> temporaryFiles = new ArrayList<>();
        List<StagedChange> staged = new ArrayList<>();
        int applied = 0;
        try {
            for (Change change : changes) {
                Path parent = checkedPath(change.file()).getParent();
                Files.createDirectories(parent);
                checkedPath(change.file());
                Path before = stage(parent, change.before(), temporaryFiles);
                Path after = stage(parent, change.after(), temporaryFiles);
                staged.add(new StagedChange(change, before, after));
            }
            // Recheck after staging in case an administrator edits a file outside the game.
            checkRevision(expected, readFiles(section));
            for (StagedChange update : staged) {
                if (!Arrays.equals(update.change().before(), readFile(update.change().file()))) throw new ConflictException();
                install(update.change().file(), update.after());
                applied++;
            }
            reload.run();
        } catch (IOException | RuntimeException failure) {
            boolean rollbackFailed = false;
            for (int i = applied - 1; i >= 0; i--) {
                StagedChange update = staged.get(i);
                Path target = update.change().file();
                // Keep the complete backup if a live file could not be rolled back.
                if (rollbackFailed && target.equals(backupPath(section))) continue;
                try {
                    if (!Arrays.equals(update.change().after(), readFile(target))) throw new ConflictException();
                    install(target, update.before());
                } catch (IOException rollbackFailure) {
                    rollbackFailed = true;
                    failure.addSuppressed(new IOException("Could not roll back " + target, rollbackFailure));
                }
            }
            throw failure;
        } finally {
            for (Path file : temporaryFiles) {
                try {
                    Files.deleteIfExists(checkedPath(root.relativize(file)));
                } catch (IOException failure) {
                    LOGGER.log(System.Logger.Level.WARNING, "Could not remove staged configuration file " + file, failure);
                }
            }
        }
    }

    private static Path stage(Path directory, byte[] data, List<Path> temporaryFiles) throws IOException {
        if (data == null) return null;
        Path file = Files.createTempFile(directory, ".config-editor-", ".tmp");
        temporaryFiles.add(file);
        Files.write(file, data);
        return file;
    }

    private void install(Path file, Path staged) throws IOException {
        Path target = checkedPath(file);
        if (staged == null) {
            Files.delete(target);
        } else {
            try {
                Files.move(staged, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException fallback) {
                Files.move(staged, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @FunctionalInterface
    interface Reload {
        void run() throws IOException;
    }

    record Snapshot(String revision, JsonObject document) {}

    static final class ConflictException extends IOException {
        ConflictException() {
            super("The configuration changed while it was being edited");
        }
    }

    private record Change(Path file, byte[] before, byte[] after) {}

    private record StagedChange(Change change, Path before, Path after) {}
}

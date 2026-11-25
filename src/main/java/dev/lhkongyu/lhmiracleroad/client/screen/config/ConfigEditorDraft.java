package dev.lhkongyu.lhmiracleroad.client.screen.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

final class ConfigEditorDraft {
    JsonObject document;
    JsonObject baseline;
    String revision;
    private JsonObject current;
    private String currentRevision;

    boolean dirty() {
        return document != null && !document.equals(baseline);
    }

    boolean conflicted() {
        return dirty() && !Objects.equals(revision, currentRevision);
    }

    void receive(String revision, JsonObject snapshot, boolean acceptSnapshot) {
        current = snapshot;
        currentRevision = revision;
        if (acceptSnapshot || !dirty()) {
            reset();
        }
    }

    void reset() {
        baseline = current;
        revision = currentRevision;
        document = baseline == null ? null : baseline.deepCopy();
    }

    static List<Change> differences(JsonObject before, JsonObject after) {
        List<Change> changes = new ArrayList<>();
        compare("", before, after, changes);
        return changes;
    }

    private static void compare(String path, JsonElement before, JsonElement after, List<Change> changes) {
        if (Objects.equals(before, after)) {
            return;
        }
        if (before != null && after != null && before.isJsonObject() && after.isJsonObject()) {
            JsonObject oldObject = before.getAsJsonObject();
            JsonObject newObject = after.getAsJsonObject();
            var keys = new LinkedHashSet<>(oldObject.keySet());
            keys.addAll(newObject.keySet());
            for (String key : keys) {
                compare(path.isEmpty() ? key : path + " / " + key, oldObject.get(key), newObject.get(key), changes);
            }
            return;
        }
        if (before != null && after != null && before.isJsonArray() && after.isJsonArray()) {
            var oldArray = before.getAsJsonArray();
            var newArray = after.getAsJsonArray();
            for (int index = 0; index < Math.max(oldArray.size(), newArray.size()); index++) {
                compare(path + " / #" + (index + 1), index < oldArray.size() ? oldArray.get(index) : null,
                        index < newArray.size() ? newArray.get(index) : null, changes);
            }
            return;
        }
        changes.add(new Change(path, before, after));
    }

    record Change(String path, JsonElement before, JsonElement after) {}
}

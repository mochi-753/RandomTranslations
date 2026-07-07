package io.github.mochi_753.randomtranslations;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.*;

public class TranslationRandomizer {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Map<String, String> randomize(final Map<String, String> original, long seed) {
        Map<String, String> result = new LinkedHashMap<>(original);
        Map<FormatSignature, List<Map.Entry<String, String>>> groups = new HashMap<>();

        for (Map.Entry<String, String> entry : original.entrySet()) {
            String value = entry.getValue();

            if (value != null && !value.isBlank()) {
                FormatSignature formatSignature = FormatSignature.parse(value);
                if (formatSignature == null) continue;

                groups.computeIfAbsent(formatSignature, ignored -> new ArrayList<>()).add(entry);
            }
        }

        for (List<Map.Entry<String, String>> entries : groups.values()) {
            entries.sort(Map.Entry.comparingByKey());

            List<String> values = new ArrayList<>(entries.size());
            for (Map.Entry<String, String> entry : entries) {
                values.add(entry.getValue());
            }

            Collections.shuffle(values, new Random(seed));

            for (int i = 0; i < entries.size(); i++) {
                result.put(entries.get(i).getKey(), values.get(i));
            }
        }

        return ImmutableMap.copyOf(result);
    }

    private record FormatSignature(List<Integer> requiredArguments) {
        static FormatSignature parse(String s) {
            TreeSet<Integer> required = new TreeSet<>();

            int i = 0;
            int implicitIndex = 1;

            while (i < s.length()) {
                int percent = s.indexOf('%', i);
                if (percent < 0) break;
                if (percent + 1 >= s.length()) return null;

                int cursor = percent + 1;
                char nextChar = s.charAt(cursor);
                if (nextChar == '%') {
                    i = percent + 2;
                    continue;
                }

                int index = -1;

                int numberStart = cursor;
                while (cursor < s.length() && Character.isDigit(s.charAt(cursor))) {
                    cursor++;
                }

                if (cursor > numberStart) {
                    if (cursor >= s.length() || s.charAt(cursor) != '$') return null;

                    try {
                        index = Integer.parseInt(s.substring(numberStart, cursor));
                    } catch (NumberFormatException ignored) {
                        return null;
                    }

                    if (index <= 0) return null;

                    cursor++;
                }

                if (cursor >= s.length() || s.charAt(cursor) != 's') return null;

                required.add(index == -1 ? implicitIndex++ : index);

                i = cursor + 1;
            }

            return new FormatSignature(List.copyOf(required));
        }
    }
}

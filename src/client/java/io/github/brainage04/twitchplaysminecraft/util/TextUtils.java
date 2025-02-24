package io.github.brainage04.twitchplaysminecraft.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TextUtils {
    public static MutableText combineTextArray(Text[] texts) {
        MutableText output = Text.empty();

        for (int i = 0; i < texts.length; i++) {
            output.append(texts[i]);
            if (i < texts.length - 2) {
                output.append(", ");
            } else if (i < texts.length - 1) {
                output.append(" and ");
            }
        }

        return output;
    }

    public static MutableText getTranslatableFromId(Identifier id, String key) {
        return Text.translatable(key.formatted(id.getNamespace(), id.getPath()));
    }
}

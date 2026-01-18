// java
package com.readystatesoftware.chuck.internal.support;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Gson TypeAdapter for java.util.Date using ISO 8601 (UTC) format.
 */
public class DateTypeAdapter extends TypeAdapter<Date> {

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final ThreadLocal<DateFormat> dateFormat = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    });

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        String formatted = dateFormat.get().format(value);
        out.value(formatted);
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String str = in.nextString();
        try {
            return dateFormat.get().parse(str);
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}

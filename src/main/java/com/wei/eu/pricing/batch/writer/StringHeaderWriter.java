package com.wei.eu.pricing.batch.writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.Writer;

public class StringHeaderWriter
                implements FlatFileHeaderCallback {

    private final String header;

    public StringHeaderWriter( final String header ) {
        this.header = header;
    }

    @Override
    public void writeHeader( @NonNull final Writer writer )
                    throws IOException {
        writer.write( header );
    }
}

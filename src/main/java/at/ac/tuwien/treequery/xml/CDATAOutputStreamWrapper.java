package at.ac.tuwien.treequery.xml;

import at.ac.tuwien.treequery.annotation.InternalApi;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class wraps an OutputStream and filters all whitespace around CDATA blocks<br>
 * This addresses a bug in Java <14: https://bugs.openjdk.java.net/browse/JDK-8223291
 */
@InternalApi
class CDATAOutputStreamWrapper extends OutputStream {

    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";
    private static final String END_TAG = "</";

    private final OutputStream out;

    private byte state;
    private int numSpace = 0;
    private byte tagIndex = 0;

    public static OutputStream wrap(OutputStream out) {
        // Only wrap if running on a faulty Java version
        return Runtime.version().feature() < 14 ? new CDATAOutputStreamWrapper(out) : out;
    }

    CDATAOutputStreamWrapper(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int c) throws IOException {
        // This method delays printing whitespace until it has determined that no CDATA block follows
        switch (state) {
            case 0:
                // Just regular content so far, look for a newline
                if (c == '\n') {
                    state = 1;
                    numSpace = 0;
                    tagIndex = 0;
                } else {
                    out.write(c);
                }
                break;
            case 1:
                // Somewhere after newline, but before CDATA content
                if (c == CDATA_START.charAt(tagIndex)) {
                    // Found next character of CDATA start tag
                    tagIndex++;
                    if (tagIndex >= CDATA_START.length()) {
                        // CDATA start tag completed, print it and move on to next state
                        for (int i = 0; i < CDATA_START.length(); i++) {
                            out.write(CDATA_START.charAt(i));
                        }
                        state = 2;
                        tagIndex = 0;
                    }
                } else if (tagIndex == 0 && c == ' ') {
                    // Still before CDATA start tag and adding another whitespace
                    numSpace++;
                } else {
                    // Some other content: Output the missing whitespace and parts of CDATA start tag and reset to state 0
                    out.write('\n');
                    for (int i = 0; i < numSpace; i++) {
                        out.write(' ');
                    }
                    for (int i = 0; i < tagIndex; i++) {
                        out.write(CDATA_START.charAt(i));
                    }
                    out.write(c);
                    state = 0;
                }
                break;
            case 2:
                // Within CDATA, output everything, but look for CDATA end tag
                out.write(c);
                if (c == CDATA_END.charAt(tagIndex)) {
                    tagIndex++;
                    if (tagIndex >= CDATA_END.length()) {
                        // CDATA end tag completed, move on to next state
                        state = 3;
                        numSpace = 0;
                        tagIndex = 0;
                    }
                } else {
                    // Missing something in the end tag, start again
                    tagIndex = 0;
                }
                break;
            case 3:
                // After CDATA, but before end tag
                if (c == END_TAG.charAt(tagIndex)) {
                    // Found next character of end tag
                    tagIndex++;
                    if (tagIndex >= END_TAG.length()) {
                        // end tag, print it and return to initial state
                        for (int i = 0; i < END_TAG.length(); i++) {
                            out.write(END_TAG.charAt(i));
                        }
                        state = 0;
                    }
                } else if (tagIndex == 0 && c == ' ') {
                    // Still before end tag and adding another whitespace
                    numSpace++;
                } else if (tagIndex > 0 || c != '\n') {
                    // Some other content: Output the missing whitespace and parts of end tag and reset to state 0
                    out.write('\n');
                    for (int i = 0; i < numSpace; i++) {
                        out.write(' ');
                    }
                    for (int i = 0; i < tagIndex; i++) {
                        out.write(END_TAG.charAt(i));
                    }
                    out.write(c);
                    state = 0;
                }
                break;
        }
    }

    @Override
    public void flush() throws IOException {
        writeBuffer();
        out.flush();
    }

    @Override
    public void close() throws IOException {
        writeBuffer();
        out.close();
    }

    private void writeBuffer() throws IOException {
        // At the end of the document, a newline (but nothing else) is buffered
        // Make sure to print it when the XML transformer calls flush
        if (state == 1 && numSpace == 0 && tagIndex == 0) {
            out.write('\n');
            state = 0;
        }
    }
}

package at.ac.tuwien.treequery.xml;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class wraps an OutputStream and filters all whitespace around CDATA blocks<br>
 * This addresses a bug in Java <14: https://bugs.openjdk.java.net/browse/JDK-8223291
 */
class CDATAOutputStreamWrapper extends OutputStream {

    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";

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
                    }
                }
                break;
            case 3:
                // After CDATA, wait for non-whitespace character
                if (c != ' ' && c != '\n') {
                    out.write(c);
                    state = 0;
                }
                break;
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}

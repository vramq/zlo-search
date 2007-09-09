package org.xonix.zlo.search;

import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.ru.RussianCharsets;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

/**
 * Author: Vovan
 * Date: 09.09.2007
 * Time: 23:22:38
 */
public class RussianWithNumbersAnalyzer extends Analyzer {

    private Analyzer russianAnalyzer;

    public RussianWithNumbersAnalyzer() {
        russianAnalyzer = new RussianAnalyzer(getRussianCharSet());
    }

    private char[] getRussianCharSet()
    {
        int length = RussianCharsets.UnicodeRussian.length;
        final char[] russianChars = new char[length + 10];

        System.arraycopy(RussianCharsets.UnicodeRussian, 0, russianChars, 0, length);
        russianChars[length++] = '0';
        russianChars[length++] = '1';
        russianChars[length++] = '2';
        russianChars[length++] = '3';
        russianChars[length++] = '4';
        russianChars[length++] = '5';
        russianChars[length++] = '6';
        russianChars[length++] = '7';
        russianChars[length++] = '8';
        russianChars[length] = '9';
        return russianChars;
    }    

    public TokenStream tokenStream(String fieldName, Reader reader) {
        return russianAnalyzer.tokenStream(fieldName, reader);
    }
}

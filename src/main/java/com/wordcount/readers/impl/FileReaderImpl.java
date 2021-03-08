package com.wordcount.readers.impl;

import com.wordcount.WordCount;
import com.wordcount.readers.InputReader;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileReaderImpl implements InputReader {

    protected String _fileName;

    public FileReaderImpl(String fileName) {
        _fileName = fileName != null ? fileName : "";
    }

    @Override
    public List<String> read() {
        List<String> lines = Collections.emptyList();

        try {
            lines = readFile();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
        }

        return lines;
    }

    private List<String> readFile() throws IOException {
        List<String> lines = new ArrayList<>();
        String line;

        BufferedReader bufferedReader = getFilerReader();
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    private BufferedReader getFilerReader() throws FileNotFoundException {
        URL fileUrl = WordCount.class.getClassLoader().getResource(_fileName);
        File file = new File(fileUrl.getFile());
        return new BufferedReader(new FileReader(file));
    }

}

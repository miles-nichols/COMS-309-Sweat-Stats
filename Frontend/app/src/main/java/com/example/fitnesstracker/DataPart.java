package com.example.fitnesstracker;

/**
 * Represents a data part for multipart file uploads.
 * Encapsulates the file name, content, and MIME type of the file.
 *
 * @author Tyler Kearney
 */
public class DataPart {

    private String fileName;
    private byte[] content;
    private String type;

    /**
     * Constructs a DataPart instance with the given file name and content.
     *
     * @param name The name of the file.
     * @param data The file's content in byte array format.
     */
    public DataPart(String name, byte[] data) {
        this.fileName = name;
        this.content = data;
    }

    /**
     * Constructs a DataPart instance with the given file name, content, and MIME type.
     *
     * @param name     The name of the file.
     * @param data     The file's content in byte array format.
     * @param mimeType The MIME type of the file (e.g., "image/png", "application/pdf").
     */
    public DataPart(String name, byte[] data, String mimeType) {
        this.fileName = name;
        this.content = data;
        this.type = mimeType;
    }

    /**
     * Returns the name of the file.
     *
     * @return The file name as a String.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the content of the file.
     *
     * @return The file content as a byte array.
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Returns the MIME type of the file.
     *
     * @return The MIME type as a String, or {@code null} if not specified.
     */
    public String getType() {
        return type;
    }
}

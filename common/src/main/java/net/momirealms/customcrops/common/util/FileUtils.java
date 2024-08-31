/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customcrops.common.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for handling file and directory operations.
 */
public class FileUtils {

    private FileUtils() {}

    /**
     * Creates a file if it does not already exist.
     *
     * @param path the path to the file
     * @return the path to the file
     * @throws IOException if an I/O error occurs
     */
    public static Path createFileIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        return path;
    }

    /**
     * Creates a directory if it does not already exist.
     *
     * @param path the path to the directory
     * @return the path to the directory
     * @throws IOException if an I/O error occurs
     */
    public static Path createDirectoryIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }

        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            // ignore
        }

        return path;
    }

    /**
     * Creates directories if they do not already exist.
     *
     * @param path the path to the directories
     * @return the path to the directories
     * @throws IOException if an I/O error occurs
     */
    public static Path createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }

        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException e) {
            // ignore
        }

        return path;
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param path the path to the directory
     * @throws IOException if an I/O error occurs
     */
    public static void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return;
        }

        try (DirectoryStream<Path> contents = Files.newDirectoryStream(path)) {
            for (Path file : contents) {
                if (Files.isDirectory(file)) {
                    deleteDirectory(file);
                } else {
                    Files.delete(file);
                }
            }
        }

        Files.delete(path);
    }
}

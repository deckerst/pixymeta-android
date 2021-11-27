/*
 * Copyright (c) 2014-2021 by Wen Yu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later version.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 *
 * Change History - most recent changes go on top of previous changes
 *
 * GIFMetq.java
 *
 * Who   Date       Description
 * ====  =========  ==================================================
 * WY    07Apr2016  Rewrite insertXMPApplicationBlock() to leverage GifXMP
 * WY    16Sep2015  Added insertComment() to insert comment block
 * WY    06Jul2015  Added insertXMP(InputSream, OutputStream, XMP)
 * WY    30Mar2015  Fixed bug with insertXMP() replacing '\0' with ' '
 * WY    13Mar2015  Initial creation
 */

package pixy.meta.meta.gif;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pixy.meta.image.gif.ApplicationExtension;
import pixy.meta.io.IOUtils;
import pixy.meta.meta.Metadata;
import pixy.meta.meta.MetadataType;
import pixy.meta.meta.image.Comments;
import pixy.meta.meta.xmp.XMP;
import pixy.meta.string.XMLUtils;
import pixy.meta.util.ArrayUtils;

/**
 * GIF Metadata tool
 *
 * @author Wen Yu, yuwen_66@yahoo.com
 * @version 1.0 04/16/2014
 */
public class GIFMeta {
    // Define constants
    public static final byte IMAGE_SEPARATOR = 0x2c;
    public static final byte IMAGE_TRAILER = 0x3b;
    public static final byte EXTENSION_INTRODUCER = 0x21;
    public static final int GRAPHIC_CONTROL_LABEL = 0xf9;
    public static final int APPLICATION_EXTENSION_LABEL = 0xff;
    public static final int COMMENT_EXTENSION_LABEL = 0xfe;
    public static final byte TEXT_EXTENSION_LABEL = 0x01;
    public static final byte[] XMP_APP_ID = {'X', 'M', 'P', ' ', 'D', 'a', 't', 'a', 'X', 'M', 'P'};

    public static final int DISPOSAL_UNSPECIFIED = 0;
    public static final int DISPOSAL_LEAVE_AS_IS = 1;
    public static final int DISPOSAL_RESTORE_TO_BACKGROUND = 2;
    public static final int DISPOSAL_RESTORE_TO_PREVIOUS = 3;

    // Data transfer object for multiple thread support
    private static class DataTransferObject {
        private byte[] header;
        private byte[] logicalScreenDescriptor;
        private byte[] globalPalette;
        private byte[] imageDescriptor;
        private Map<MetadataType, Metadata> metadataMap;
        private Comments comments;
    }

    public static void insertComments(InputStream is, OutputStream os, List<String> comments) throws IOException {
        // Read and copy header and LSD
        // Create a new data transfer object to hold data
        DataTransferObject dto = new DataTransferObject();
        readHeader(is, dto);
        readLSD(is, dto);
        os.write(dto.header);
        os.write(dto.logicalScreenDescriptor);
        if ((dto.logicalScreenDescriptor[4] & 0x80) == 0x80) {
            int bitsPerPixel = (dto.logicalScreenDescriptor[4] & 0x07) + 1;
            int colorsUsed = (1 << bitsPerPixel);

            readGlobalPalette(is, colorsUsed, dto);
            os.write(dto.globalPalette);
        }
        int numOfComments = comments.size();
        for (int i = 0; i < numOfComments; i++) {
            os.write(EXTENSION_INTRODUCER);
            os.write(COMMENT_EXTENSION_LABEL);
            byte[] commentBytes = comments.get(i).getBytes();
            int numBlocks = commentBytes.length / 0xff;
            int leftOver = commentBytes.length % 0xff;
            int offset = 0;
            if (numBlocks > 0) {
                for (int block = 0; block < numBlocks; block++) {
                    os.write(0xff);
                    os.write(commentBytes, offset, 0xff);
                    offset += 0xff;
                }
            }
            if (leftOver > 0) {
                os.write(leftOver);
                os.write(commentBytes, offset, leftOver);
            }
            os.write(0);
        }
        // Copy the rest of the input stream
        byte buf[] = new byte[10240]; // 10K
        int bytesRead = is.read(buf);

        while (bytesRead != -1) {
            os.write(buf, 0, bytesRead);
            bytesRead = is.read(buf);
        }
    }

    public static void insertXMPApplicationBlock(InputStream is, OutputStream os, XMP xmp) throws IOException {
        insertXMPApplicationBlock(is, os, xmp.getData());
    }

    public static void insertXMPApplicationBlock(InputStream is, OutputStream os, byte[] xmp) throws IOException {
        // Read and copy header and LSD
        // Create a new data transfer object to hold data
        DataTransferObject dto = new DataTransferObject();
        readHeader(is, dto);
        readLSD(is, dto);
        os.write(dto.header);
        os.write(dto.logicalScreenDescriptor);

        if ((dto.logicalScreenDescriptor[4] & 0x80) == 0x80) {
            int bitsPerPixel = (dto.logicalScreenDescriptor[4] & 0x07) + 1;
            int colorsUsed = (1 << bitsPerPixel);

            readGlobalPalette(is, colorsUsed, dto);
            os.write(dto.globalPalette);
        }

        if (xmp != null) {
            byte[] buf = new byte[14];
            buf[0] = EXTENSION_INTRODUCER; // Extension introducer
            buf[1] = (byte) APPLICATION_EXTENSION_LABEL; // Application extension label
            buf[2] = 0x0b; // Block size
            buf[3] = 'X'; // Application Identifier (8 bytes)
            buf[4] = 'M';
            buf[5] = 'P';
            buf[6] = ' ';
            buf[7] = 'D';
            buf[8] = 'a';
            buf[9] = 't';
            buf[10] = 'a';
            buf[11] = 'X';// Application Authentication Code (3 bytes)
            buf[12] = 'M';
            buf[13] = 'P';
            // Create a byte array from 0x01, 0xFF - 0x00, 0x00
            byte[] magic_trailer = new byte[258];

            magic_trailer[0] = 0x01;

            for (int i = 255; i >= 0; i--)
                magic_trailer[256 - i] = (byte) i;

            // Insert XMP here
            // Write extension introducer and application identifier
            os.write(buf);
            // Write the XMP packet
            os.write(xmp);
            // Write the magic trailer
            os.write(magic_trailer);
            // End of XMP data
        }

        while (copyFrame(is, os, true)) {
        }
    }

    public static void insertXMPApplicationBlock(InputStream is, OutputStream os, String xmp) throws IOException {
        if (xmp == null) {
            insertXMPApplicationBlock(is, os, (byte[]) null);
            return;
        }

        Document doc = XMLUtils.createXML(xmp);
        XMLUtils.insertLeadingPI(doc, "xpacket", "begin='' id='W5M0MpCehiHzreSzNTczkc9d'");
        XMLUtils.insertTrailingPI(doc, "xpacket", "end='w'");
        // Serialize doc to byte array
        byte[] xmpBytes = XMLUtils.serializeToByteArray(doc);
        insertXMPApplicationBlock(is, os, xmpBytes);
    }

    private static boolean readFrame(InputStream is, DataTransferObject dto) throws IOException {
        // Need to reset some of the fields
        int disposalMethod = -1;
        // End of fields reset

        int image_separator;

        do {
            image_separator = is.read();

            if (image_separator == -1 || image_separator == IMAGE_TRAILER) { // End of stream
                return false;
            }

            if (image_separator == EXTENSION_INTRODUCER) {
                // Extension Block
                int extensionLabel = is.read();
                int len = is.read();

                if (extensionLabel == GRAPHIC_CONTROL_LABEL) {
                    // Graphic Control Label - identifies the current block as a Graphic Control Extension
                    //<<Start of graphic control block>>
                    int packedFields = is.read();
                    // Determine the disposal method
                    disposalMethod = ((packedFields & 0x1c) >> 2);
                    switch (disposalMethod) {
                        case DISPOSAL_UNSPECIFIED:
                            // Frame disposal method: UNSPECIFIED
                        case DISPOSAL_LEAVE_AS_IS:
                            // Frame disposal method: LEAVE_AS_IS
                        case DISPOSAL_RESTORE_TO_BACKGROUND:
                            // Frame disposal method: RESTORE_TO_BACKGROUND
                        case DISPOSAL_RESTORE_TO_PREVIOUS:
                            // Frame disposal method: RESTORE_TO_PREVIOUS
                            break;
                        default:
                            //throw new RuntimeException("Invalid GIF frame disposal method: " + disposalMethod);
                    }
                    // Check for transparent color flag
                    // len=0, block terminator!
                    if ((packedFields & 0x01) == 0x01) {
                        IOUtils.skipFully(is, 2);
                        // Transparent GIF
                        is.read(); // Transparent color index
                    } else {
                        IOUtils.skipFully(is, 3);
                    }
                    len = is.read();// len=0, block terminator!
                    // <<End of graphic control block>>
                } else if (extensionLabel == APPLICATION_EXTENSION_LABEL) {
                    // Application block
                    byte[] temp = new byte[0x0B];
                    IOUtils.readFully(is, temp);
                    if (Arrays.equals(XMP_APP_ID, temp)) {
                        // XMP block
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        len = is.read();
                        while (len != 0) {
                            bout.write(len);
                            temp = new byte[len];
                            IOUtils.readFully(is, temp);
                            bout.write(temp);
                            len = is.read();
                        }
                        byte[] xmp = bout.toByteArray();
                        // Remove the magic trailer - 258 bytes minus the block terminator
                        len = xmp.length - 257;
                        if (len > 0) {
                            // Put it into the Meta data map
                            dto.metadataMap.put(MetadataType.XMP, new GifXMP(ArrayUtils.subArray(xmp, 0, len)));
                        }
                        len = 0; // We're already at block terminator
                    } else {
                        len = is.read();
                    }
                } else if (extensionLabel == COMMENT_EXTENSION_LABEL) {
                    // Comment block
                    byte[] comment = new byte[len];
                    IOUtils.readFully(is, comment);
                    if (dto.comments == null) {
                        dto.comments = new Comments();
                    }
                    dto.comments.addComment(comment);
                    len = is.read();
                }
                // GIF87a specification mentions the repetition of multiple length
                // blocks while GIF89a gives no specific description. For safety, here
                // a while loop is used to check for block terminator!
                while (len != 0) {
                    IOUtils.skipFully(is, len);
                    len = is.read();// len=0, block terminator!
                }
            }
        } while (image_separator != IMAGE_SEPARATOR);

        // <<Start of new frame>>
        readImageDescriptor(is, dto);

        int colorsUsed = 1 << ((dto.logicalScreenDescriptor[4] & 0x07) + 1);

        byte[] localPalette = null;

        if ((dto.imageDescriptor[8] & 0x80) == 0x80) {
            // A local color map is present
            int bitsPerPixel = (dto.imageDescriptor[8] & 0x07) + 1;
            // Colors used in local palette
            colorsUsed = (1 << bitsPerPixel);
            localPalette = new byte[3 * colorsUsed];
            is.read(localPalette);
        }

        if (localPalette == null) {
            localPalette = dto.globalPalette;
        }
        is.read(); // LZW Minimum Code Size

        int len;
        while ((len = is.read()) > 0) {
            byte[] block = new byte[len];
            is.read(block);
        }

        return true;
    }

    private static boolean copyFrame(InputStream is, OutputStream os, boolean removeXmp) throws IOException {
        int imageSeparator;

        do {
            imageSeparator = is.read();
            if (imageSeparator == -1) {
                // End of stream
                return false;
            }

            if (imageSeparator == IMAGE_TRAILER) {
                os.write(IMAGE_TRAILER);
                return false;
            }

            if (imageSeparator == EXTENSION_INTRODUCER) {
                // Extension Block
                int extensionLabel = is.read();
                if (extensionLabel == APPLICATION_EXTENSION_LABEL) {
                    // Application Extension
                    is.read(); // always reading length of 11

                    byte[] applicationId = new byte[8];
                    IOUtils.readFully(is, applicationId);

                    byte[] authenticationCode = new byte[3];
                    IOUtils.readFully(is, authenticationCode);

                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    copyUntilBlockTerminator(is, bout);
                    byte[] data = bout.toByteArray();

                    ApplicationExtension appExtension = new ApplicationExtension(applicationId, authenticationCode, data);
                    if (!(appExtension.isXmp() && removeXmp)) {
                        appExtension.write(os);
                    }
                } else {
                    os.write(EXTENSION_INTRODUCER);
                    os.write(extensionLabel);
                    copyUntilBlockTerminator(is, os);
                }
            } else {
                os.write(imageSeparator);
            }
        } while (imageSeparator != IMAGE_SEPARATOR);

        byte[] imageDescriptor = new byte[9];
        is.read(imageDescriptor);
        os.write(imageDescriptor);

        if ((imageDescriptor[8] & 0x80) == 0x80) {
            int bitsPerPixel = (imageDescriptor[8] & 0x07) + 1;
            int colorsUsed = (1 << bitsPerPixel);
            byte[] localColorMap = new byte[3 * colorsUsed];
            is.read(localColorMap);
            os.write(localColorMap);
        }

        int lzwMinimumCodeSize = is.read();
        os.write(lzwMinimumCodeSize);

        copyUntilBlockTerminator(is, os);

        return true;
    }

    private static void copyUntilBlockTerminator(InputStream is, OutputStream os) throws IOException {
        byte[] temp;
        int len = is.read();
        while (len != 0) {
            os.write(len);
            temp = new byte[len];
            IOUtils.readFully(is, temp);
            os.write(temp);
            len = is.read();
        }
        os.write(len);
    }

    private static void readGlobalPalette(InputStream is, int num_of_color, DataTransferObject dto) throws IOException {
        dto.globalPalette = new byte[num_of_color * 3];
        is.read(dto.globalPalette);
    }

    private static void readHeader(InputStream is, DataTransferObject dto) throws IOException {
        dto.header = new byte[6]; // GIFXXa
        is.read(dto.header);
    }

    private static void readImageDescriptor(InputStream is, DataTransferObject dto) throws IOException {
        dto.imageDescriptor = new byte[9];
        is.read(dto.imageDescriptor);
    }

    private static void readLSD(InputStream is, DataTransferObject dto) throws IOException {
        dto.logicalScreenDescriptor = new byte[7];
        is.read(dto.logicalScreenDescriptor);
    }

    public static Map<MetadataType, Metadata> readMetadata(InputStream is) throws IOException {
        // Create a new data transfer object to hold data
        DataTransferObject dto = new DataTransferObject();
        // Created a Map for the Meta data
        dto.metadataMap = new HashMap<>();

        readHeader(is, dto);
        readLSD(is, dto);

        // Packed byte
        if ((dto.logicalScreenDescriptor[4] & 0x80) == 0x80) {
            // A global color map is present
            int bitsPerPixel = (dto.logicalScreenDescriptor[4] & 0x07) + 1;
            int colorsUsed = (1 << bitsPerPixel);

            readGlobalPalette(is, colorsUsed, dto);
        }

        while (readFrame(is, dto)) {
        }

        if (dto.comments != null) {
            dto.metadataMap.put(MetadataType.COMMENT, dto.comments);
        }

        return dto.metadataMap;
    }

    private GIFMeta() {
    }
}

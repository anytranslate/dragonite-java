/*
 * VECTORSIGHT CONFIDENTIAL
 * ------------------------
 * Copyright (c) [2015] - [2017]
 * VectorSight Systems Co., Ltd.
 * All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Written by Toby Huang <t@vecsight.com>, June 2017
 */

package com.vecsight.dragonite.proxy.network;

import com.vecsight.dragonite.mux.conn.MultiplexedConnection;
import com.vecsight.dragonite.mux.exception.ConnectionNotAliveException;
import com.vecsight.dragonite.mux.exception.SenderClosedException;
import com.vecsight.dragonite.proxy.misc.StreamCryptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class CryptorPipe {

    private final short bufferSize;

    private final StreamCryptor streamCryptor;

    public CryptorPipe(final short bufferSize, final StreamCryptor streamCryptor) {
        this.bufferSize = bufferSize;
        this.streamCryptor = streamCryptor;
    }

    public void pipe(final InputStream inputStream, final MultiplexedConnection connection) throws IOException, SenderClosedException, InterruptedException {
        int len;
        final byte[] buf = new byte[bufferSize];
        while ((len = inputStream.read(buf)) > 0) {
            connection.send(streamCryptor.encrypt(Arrays.copyOf(buf, len)));
        }
    }

    public void pipe(final MultiplexedConnection connection, final OutputStream outputStream) throws ConnectionNotAliveException, InterruptedException, IOException {
        byte[] buf;
        while ((buf = connection.read()) != null) {
            outputStream.write(streamCryptor.decrypt(buf));
        }
    }

}

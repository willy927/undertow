/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.undertow.protocols.spdy;

import io.undertow.server.protocol.framed.SendFrameHeader;
import io.undertow.util.ImmediatePooled;

import java.nio.ByteBuffer;

/**
 * @author Stuart Douglas
 */
class SpdyPingStreamSinkChannel extends SpdyControlFrameStreamSinkChannel {

    private final int id;

    protected SpdyPingStreamSinkChannel(SpdyChannel channel, int id) {
        super(channel);
        this.id = id;
    }

    @Override
    protected SendFrameHeader createFrameHeader() {
        ByteBuffer buf = ByteBuffer.allocate(12);

        int firstInt = SpdyChannel.CONTROL_FRAME | (getChannel().getSpdyVersion() << 16) | SpdyChannel.PING;
        SpdyProtocolUtils.putInt(buf, firstInt);
        SpdyProtocolUtils.putInt(buf, 4); //we back fill the length
        SpdyProtocolUtils.putInt(buf, id);
        return new SendFrameHeader(new ImmediatePooled<>(buf));
    }

}

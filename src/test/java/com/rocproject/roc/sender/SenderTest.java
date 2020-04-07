package com.rocproject.roc.sender;

import com.rocproject.roc.config.*;
import com.rocproject.roc.address.Address;
import com.rocproject.roc.address.Family;
import com.rocproject.roc.context.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.*;

public class SenderTest {
    static {
        System.loadLibrary("native");
    }

    private final int EXAMPLE_SAMPLE_RATE = 44100;
    private final int EXAMPLE_SINE_RATE = 440;
    private final int EXAMPLE_SINE_SAMPLES = (EXAMPLE_SAMPLE_RATE * 5);
    private final int EXAMPLE_BUFFER_SIZE = 100;
    private SenderConfig config;
    private float[] samples;
    private Context context;

    private void gensine(float[] samples) {
        double t = 0d;
        for (int i = 0; i < samples.length / 2; i++) {
            float s = (float) sin(2 * 3.14159265359 * EXAMPLE_SINE_RATE / EXAMPLE_SAMPLE_RATE * t);
            /* Fill samples for left and right channels. */
            samples[i * 2] = s;
            samples[i * 2 + 1] = -s;
            t += 1;
        }
    }

    SenderTest() {
        this.config = new SenderConfig.Builder(EXAMPLE_SAMPLE_RATE,
                                                ChannelSet.ROC_CHANNEL_SET_STEREO,
                                                FrameEncoding.ROC_FRAME_ENCODING_PCM_FLOAT)
                                                .automaticTiming(1)
                                        .build();
        this.samples = new float[EXAMPLE_BUFFER_SIZE];
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        this.context = new Context();
    }

    @AfterEach
    public void afterEach() throws Exception {
        this.context.close();
    }

    @Test
    public void TestValidSenderCreationAndDeinitialization() {
        assertDoesNotThrow(() -> {
            try (
                    Sender sender = new Sender(context, config);
            ) {}
        });
    }

    @Test
    public void TestInvalidSenderCreation() {
        assertThrows(IllegalArgumentException.class, () -> new Sender(null, config));
        assertThrows(IllegalArgumentException.class, () -> new Sender(context, null));
    }

    @Test
    public void TestValidSenderBind() throws Exception {
        try (
                Sender sender = new Sender(context, config);
        ) {
            assertDoesNotThrow(() -> sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0)));
        }
    }

    @Test
    public void TestSenderBindEphemeralPort() throws Exception {
        try (
                Sender sender = new Sender(context, config);
        ) {
            Address senderAddress = new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0);
            sender.bind(senderAddress);
            assertNotEquals(0, senderAddress.getPort());
        }
    }

    @Test
    public void TestInvalidSenderBind() throws Exception {
        try (
                Sender sender = new Sender(context, config);
        ) {
            assertThrows(IllegalArgumentException.class, () -> sender.bind(null));
            assertThrows(IOException.class, () -> {
                sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
                sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
            });
        }
    }

    @Test
    public void TestValidSenderConnect() throws Exception {
        try (
                Sender sender = new Sender(context, config);
        ) {
            assertDoesNotThrow(() -> {
                sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
                sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
                sender.connect(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10002));
            });
        }
    }

    @Test
    public void TestInvalidSenderConnect() throws Exception {
        try (
            Sender sender = new Sender(context, config);
        ) {
            sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
            assertThrows(IllegalArgumentException.class, () -> {
                sender.connect(null, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
            });
            assertThrows(IllegalArgumentException.class, () -> {
                sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, null, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
            });
            assertThrows(IllegalArgumentException.class, () -> {
                sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, null);
            });
        }
    }

    @Test
    public void TestValidSenderWriteSingleFloat() throws Exception {
        try (
                Sender sender = new Sender(context, config);
        ) {
            sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
            sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
            sender.connect(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10002));
            assertDoesNotThrow(() -> sender.write(2.0f));
        }
    }

    @Test
    public void TestValidSenderWriteFloatArray() throws Exception {
        try (
            Sender sender = new Sender(context, config);
        ) {
            sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
            sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
            sender.connect(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10002));
            for (int i = 0; i < EXAMPLE_SINE_SAMPLES / EXAMPLE_BUFFER_SIZE; i++) {
                gensine(samples);
                assertDoesNotThrow(() -> sender.write(samples));
            }
        }
    }

    @Test
    public void TestInvalidSenderWriteFloatArray() throws Exception {
        try (
                Sender sender = new Sender(context, config);
        ) {
            gensine(samples);
            assertThrows(IOException.class, () -> sender.write(samples)); // write before bind
            sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
            assertThrows(IOException.class, () -> sender.write(samples)); // write before connect
            sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
            sender.connect(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10002));
            assertThrows(IllegalArgumentException.class, () -> sender.write(null));
        }
    }

    @Test
    public void TestInvalidConnectAfterWrite() throws Exception {
        try (
                Sender sender = new Sender(context, config);
        ) {
            sender.bind(new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0));
            assertThrows(IOException.class, () -> {
                sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
                sender.connect(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10002));
                sender.write(2.0f);
                sender.connect(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "127.0.0.1", 10001));
            });
        }
    }
}
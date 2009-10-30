package sessionj.runtime.session;

import static sessionj.runtime.util.SJRuntimeUtils.SJ_SERIALIZED_INT_LENGTH;
import static sessionj.runtime.util.SJRuntimeUtils.deserializeInt;
import static sessionj.runtime.session.SJAbstractSerializer.*;
import sessionj.runtime.SJRuntimeException;

import java.nio.ByteBuffer;

/**
 * TODO Reduce duplication between this and {@link SJManualSerializer}
 */
public class OngoingReadImpl implements OngoingRead {
    private byte flag = -1;
    private int dataRead = 0;
    private int dataExpected = -1;
    private int lengthRead = 0;
    private final byte[] lengthBytes = new byte[SJ_SERIALIZED_INT_LENGTH];
    private byte[] data = null;

    public void updatePendingInput(ByteBuffer bytes) {
        if (bytes.remaining() > 0) {
            if (flag == -1) readFlagAndMaybeSetExpected(bytes);

            if (dataExpected != -1 && data == null)
                data = new byte[dataExpected];

            if (data != null && bytes.remaining() > 0) {
                int offset = dataRead - SJ_SERIALIZED_INT_LENGTH;
                dataRead += bytes.remaining();
                bytes.get(data, offset, bytes.remaining());
            }
        }
    }

    private void readFlagAndMaybeSetExpected(ByteBuffer bytes) {
        flag = bytes.get();
        switch (flag) {
            case SJ_BYTE:
            case SJ_BOOLEAN:
                dataExpected = 1;
                break;
            case SJ_INT:
                dataExpected = SJ_SERIALIZED_INT_LENGTH;
                break;
            case SJ_OBJECT:
            case SJ_CONTROL:
                break; // nothing to do in this case, will read count from socket
            default:
                throw new SJRuntimeException("[OngoingRead] Unsupported flag: " + flag);
        }

        if (dataExpected == -1) {
            while (bytes.remaining() != 0 && lengthRead < SJ_SERIALIZED_INT_LENGTH) {
                lengthBytes[lengthRead] = bytes.get();
                lengthRead++;
            }
            if (lengthRead == SJ_SERIALIZED_INT_LENGTH) {
                dataExpected = deserializeInt(lengthBytes);
            }
        }
    }

    public boolean finished() {
        return dataRead == dataExpected;
    }

    public byte[] getCompleteInput() {
        // +1 for the initial flag. lengthRead is 0 by default, so works fine if reading a primitive type
        byte[] completed = new byte[1 + dataExpected + lengthRead];
        completed[0] = flag; 
        // no-op for primitives: lengthRead will be 0
        System.arraycopy(lengthBytes, 0, completed, 0, lengthRead);
        System.arraycopy(data, 0, completed, lengthRead, data.length);
        return completed;
    }
}

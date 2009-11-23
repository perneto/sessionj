package sessionj.runtime.session;

import static sessionj.runtime.util.SJRuntimeUtils.SJ_SERIALIZED_INT_LENGTH;
import static sessionj.runtime.util.SJRuntimeUtils.deserializeInt;
import static sessionj.runtime.session.SJAbstractSerializer.*;
import sessionj.runtime.SJRuntimeException;

import java.nio.ByteBuffer;
import static java.lang.Math.min;

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
    private static final boolean DEBUG = false;

    public void updatePendingInput(ByteBuffer bytes) {
        if (bytes.remaining() > 0 && flag == -1) 
            readFlagAndMaybeSetExpected(bytes);
        
        if (bytes.remaining() > 0 && dataExpected == -1)
            readDataExpected(bytes);

        if (dataExpected != -1 && data == null)
            data = new byte[dataExpected];

        if (bytes.remaining() > 0 && data != null) {
            readData(bytes);
        }
    }

    private void readData(ByteBuffer bytes) {
        int offset = dataRead;
        int length = min(data.length - offset, bytes.remaining());
        bytes.get(data, offset, length);
        dataRead += length;
    }

    private void readDataExpected(ByteBuffer bytes) {
        while (bytes.remaining() != 0 && lengthRead < SJ_SERIALIZED_INT_LENGTH) {
            lengthBytes[lengthRead] = bytes.get();
            lengthRead++;
        }
        if (lengthRead == SJ_SERIALIZED_INT_LENGTH) {
            dataExpected = deserializeInt(lengthBytes);
        }
    }

    private void readFlagAndMaybeSetExpected(ByteBuffer bytes) {
        flag = bytes.get();
        switch (flag) {
            case SJ_BYTE:
            case SJ_BOOLEAN:
                debug("Reading byte or boolean");
                dataExpected = 1;
                break;
            case SJ_INT:
                debug("Reading int");
                dataExpected = SJ_SERIALIZED_INT_LENGTH;
                break;
            case SJ_OBJECT:
            case SJ_CONTROL:
                debug("Reading object or control signal");
                break; // nothing to do in this case, will read count from socket
            default:
                debug("Unsupported flag in:" + bytes);
                throw new SJRuntimeException("[OngoingRead] Unsupported flag: " + flag);
        }
    }

    private static void debug(String s) {
        if (DEBUG) System.out.println(s);
    }

    public boolean finished() {
        return dataRead == dataExpected;
    }

    public ByteBuffer getCompleteInput() {
        // +1 for the initial flag. lengthRead is 0 by default, so works fine if reading a primitive type
        byte[] completed = new byte[1 + dataExpected + lengthRead];
        completed[0] = flag; 
        // no-op for primitives: lengthRead will be 0
        System.arraycopy(lengthBytes, 0, completed, 1, lengthRead);
        System.arraycopy(data, 0, completed, lengthRead+1, data.length);
        
        ByteBuffer wrapped = ByteBuffer.wrap(completed);
        debug("completed input: " + wrapped);
        return wrapped;
    }
}

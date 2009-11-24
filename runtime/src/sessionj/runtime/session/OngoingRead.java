package sessionj.runtime.session;

import java.nio.ByteBuffer;

/**
 */
public interface OngoingRead {
    void updatePendingInput(ByteBuffer bytes, boolean eof);

    boolean finished();

    ByteBuffer getCompleteInput();
}

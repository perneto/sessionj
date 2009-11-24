package sessionj.runtime.net;

import sessionj.runtime.session.SJDeserializer;
import sessionj.runtime.session.SJCustomMessageFormatter;
import sessionj.runtime.session.OngoingRead;
import sessionj.runtime.SJIOException;

import java.nio.ByteBuffer;

public class CustomMessageFormatterFactory implements SJDeserializer {
    private final SJSessionParameters params;

    public CustomMessageFormatterFactory(SJSessionParameters params) {
        this.params = params;
    }

    @Override
    public OngoingRead newOngoingRead() throws SJIOException {
        return new MessageFormatterOngoingRead(params.createCustomMessageFormatter());
    }

    private class MessageFormatterOngoingRead implements OngoingRead {
        private final SJCustomMessageFormatter messageFormatter;
        private Object parsed = null;
        private MessageFormatterOngoingRead(SJCustomMessageFormatter messageFormatter) {
            this.messageFormatter = messageFormatter;
        }

        @Override
        public void updatePendingInput(ByteBuffer bytes, boolean eof) throws SJIOException {
            parsed = messageFormatter.parseMessage(bytes, eof);
        }

        @Override
        public boolean finished() {
            return parsed != null;
        }

        @Override
        public ByteBuffer getCompleteInput() throws SJIOException {
            // FIXME: ugly hack
            if (parsed != null)
                return ByteBuffer.wrap(messageFormatter.formatMessage(parsed));
            else
                return null;
        }
    }
}

package sessionj.runtime.net;

import sessionj.runtime.SJIOException;
import sessionj.runtime.session.OngoingRead;
import sessionj.runtime.session.SJCustomMessageFormatter;
import sessionj.runtime.session.SJDeserializer;
import sessionj.runtime.util.SJRuntimeUtils;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class CustomMessageFormatterFactory implements SJDeserializer {
    private static final Logger log = SJRuntimeUtils.getLogger(CustomMessageFormatterFactory.class);
    private final MessageFormatterOngoingRead messageFormatterOngoingRead;

    public CustomMessageFormatterFactory(SJCustomMessageFormatter cmf) {
        messageFormatterOngoingRead = new MessageFormatterOngoingRead(cmf);
    }

    @Override
    public OngoingRead newOngoingRead() throws SJIOException {
        // Need to reuse the same message formatter
        return messageFormatterOngoingRead;
    }

    private class MessageFormatterOngoingRead implements OngoingRead {
        private final SJCustomMessageFormatter messageFormatter;
        private Object parsed = null;
        private MessageFormatterOngoingRead(SJCustomMessageFormatter messageFormatter) {
            this.messageFormatter = messageFormatter;
        }

        @Override
        public void updatePendingInput(ByteBuffer bytes, boolean eof) throws SJIOException {
            log.finest("Consuming input, messageFormatter = " + messageFormatter);
            parsed = messageFormatter.parseMessage(bytes, eof);
            log.finer("Consumed input, parsed = " + parsed);
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

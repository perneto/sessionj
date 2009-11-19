//$ bin/sessionjc -cp tests/classes/ tests/src/esmtp/sj/messages/SmtpParseable.sj -d tests/classes/ 

package esmtp.sj.messages;

interface SmtpParseable
{
	boolean isParseable(String m);
	SmtpParseable parse(String m);	
}

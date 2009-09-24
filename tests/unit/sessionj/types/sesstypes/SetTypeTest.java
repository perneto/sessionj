package sessionj.types.sesstypes;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import sessionj.types.SJTypeSystem_c;
import sessionj.types.SJTypeSystem;

import java.util.List;
import java.util.LinkedList;

import polyglot.types.*;

public class SetTypeTest {
    private SJSessionType set;
    private List<SJSessionType_c> members;
    private SJTypeSystem ts;
    private SJSendType_c sendBool;

    @BeforeTest
    public void createSet() throws SemanticException {
        members = new LinkedList<SJSessionType_c>();
        ts = new SJTypeSystem_c();
        sendBool = new SJSendType_c(ts, new PrimitiveType_c(ts, PrimitiveType.BOOLEAN));
        members.add(sendBool);
        members.add(new SJSendType_c(ts, new PrimitiveType_c(ts, PrimitiveType.INT)));
        set = new SJSetType_c(ts, members);
    }

    @Test
    public void subtypeOfSameSetType() throws SemanticException {
        Type otherSet = new SJSetType_c(ts, members);

        assert set.isSubtype(otherSet);
        assert otherSet.isSubtype(set);
    }

    @Test
    public void subtypeOfMemberOfSet() {
        assert sendBool.isSubtype(set);
        assert !set.isSubtype(sendBool);
    }
}

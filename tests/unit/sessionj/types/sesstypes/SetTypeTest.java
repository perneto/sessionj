package sessionj.types.sesstypes;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import sessionj.types.SJTypeSystem_c;
import sessionj.types.SJTypeSystem;
import sessionj.Version;

import java.util.List;
import java.util.LinkedList;

import polyglot.types.*;
import polyglot.types.reflect.ClassFileLoader;
import polyglot.frontend.ExtensionInfo;

public class SetTypeTest {
    private SJSessionType set;
    private List<SJSessionType_c> members;
    private SJTypeSystem ts;
    private SJSendType_c sendBool;
    private SJSessionType_c sendString;
    private SJSessionType_c receiveString;
    private SJSessionType_c sendObject;
    private SJSessionType_c receiveObject;

    @BeforeTest
    public void createSet() throws SemanticException {
        ts = new SJTypeSystem_c();
        String classpath = System.getProperty("java.class.path");
        ExtensionInfo ext = new sessionj.ExtensionInfo();
        TopLevelResolver resolver = new LoadedClassResolver
            (ts, classpath, new ClassFileLoader(ext), new Version(), true);
        ts.initialize(resolver, ext);
        sendBool = sendType(PrimitiveType.BOOLEAN);
        sendString = new SJSendType_c(ts, ts.String());
        sendObject = new SJSendType_c(ts, ts.Object());
        receiveObject = new SJReceiveType_c(ts, ts.Object());
        receiveString = new SJReceiveType_c(ts, ts.String());
        members = new LinkedList<SJSessionType_c>() {{
            add(sendBool);
            add(sendType(PrimitiveType.INT));
        }};
        set = new SJSetType_c(ts, members);
    }

    private SJSendType_c sendType(PrimitiveType.Kind primitive) throws SemanticException {
        return new SJSendType_c(ts, new PrimitiveType_c(ts, primitive));
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

    @Test
    public void notSubtypeNotMemberOfSet() throws SemanticException {
        Type sendFloat = sendType(PrimitiveType.FLOAT);
        assert !sendFloat.isSubtype(set);
        assert !set.isSubtype(sendFloat);
    }

    @Test
    public void subtypeSmallerSetType() {
        List<SJSessionType_c> smaller = new LinkedList<SJSessionType_c>();
        smaller.add(sendBool);
        Type smallerSet = new SJSetType_c(ts, smaller);

        assert smallerSet.isSubtype(set);
        assert !set.isSubtype(smallerSet);
    }

    @Test 
    public void setElementsAreSubtypes() {
        List<SJSessionType_c> listSubtypes = new LinkedList<SJSessionType_c>() {{
            add(sendObject);
            add(receiveString);
        }};
        List<SJSessionType_c> listSupertypes = new LinkedList<SJSessionType_c>() {{
            add(sendString);
            add(receiveObject);
            add(sendBool);
        }};
        Type setWithSubtypes = new SJSetType_c(ts, listSubtypes);
        Type setWithSupertypes = new SJSetType_c(ts, listSupertypes);
        assert !setWithSupertypes.isSubtype(setWithSubtypes);
        assert setWithSubtypes.isSubtype(setWithSupertypes);
    }

    //@Test
    public void subsumeSetAndMemberOfSet() throws SemanticException {
        SJSessionType result = set.subsume(sendBool);
        assert result instanceof SJSetType;
        // a singleton with just sendBool in it
        assert result.isSubtype(sendBool);
        assert sendBool.isSubtype(result);
    }

    //@Test
    public void subsumeSetAndSubtypeOfMemberOfSet() throws SemanticException {
        SJSessionType setWithSupertype = new SJSetType_c(ts, new LinkedList<SJSessionType_c>() {{
            add(sendString);
            add(sendBool);
        }});

        SJSessionType result = setWithSupertype.subsume(sendObject);
        assert result instanceof SJSetType;
        assert result.isSubtype(sendObject);
        assert sendObject.isSubtype(result);
    }

    //@Test
    public void subsumeFoo() throws SemanticException {
        SJSessionType set1 = new SJSetType_c(ts, new LinkedList<SJSessionType_c>() {{
            add(sendString);
            add(sendObject);
        }});

        SJSessionType set2 = new SJSetType_c(ts, new LinkedList<SJSessionType_c>() {{
            add(sendString);
        }});
        SJSessionType result = set1.subsume(set2);
        assert result.isSubtype(sendString);
        assert sendString.isSubtype(result);
    }

    //@Test(expectedExceptions = SemanticException.class)
    public void unsuccessfulSubsuption() {
        
    }
}

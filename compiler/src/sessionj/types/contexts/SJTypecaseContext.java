package sessionj.types.contexts;

import sessionj.ast.sessops.compoundops.SJTypecase;
import sessionj.ast.sessops.compoundops.SJCompoundOperation;
import sessionj.types.sesstypes.SJSetType;
import sessionj.types.sesstypes.SJSessionType;
import polyglot.types.SemanticException;

import java.util.List;
import java.util.Collections;

public class SJTypecaseContext extends SJContextElement_c implements SJSessionContext {
    public final String sjname;
    private final SJTypecase typecase;

    public SJTypecaseContext(SJContextElement current, SJTypecase typecase, String sjname) {
        super(current);
        this.typecase = typecase;
        assert sjname != null;
        this.sjname = sjname;
    }

    public SJSetType getActiveSetType() throws SemanticException {
        SJSessionType active = getActive(this.sjname);
        if (active == null || ! (active instanceof SJSetType))
            throw new SemanticException(this.sjname + ": Expected set type, but got: " + active);
        return (SJSetType) active;
    }

    public SJSetType getActiveSetTypeIfMatchingName(String sjname) throws SemanticException {
        if (this.sjname.equals(sjname))
            return getActiveSetType();
        else
            return null;
    }

    public SJCompoundOperation node() {
        return typecase;
    }

    public List<String> targets() {
        return Collections.singletonList(sjname);
    }
}

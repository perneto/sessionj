package sessionj.runtime.session;

import sessionj.util.SJLabel;
import sessionj.types.sesstypes.SJRecursionType;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;

public class TypeVariableScope {
    private final Map<SJLabel, Stack<SJRecursionType>> labels = new HashMap<SJLabel, Stack<SJRecursionType>>();

    public Map<SJLabel, SJRecursionType> inScope() {
        Map<SJLabel, SJRecursionType> result = new HashMap<SJLabel, SJRecursionType>();
        for (Map.Entry<SJLabel, Stack<SJRecursionType>> e : labels.entrySet()) {
            result.put(e.getKey(), e.getValue().peek());
        }
        return result;
    }

    public void enterScope(SJLabel sjLabel, SJRecursionType rt) {
        Stack<SJRecursionType> s = labels.get(sjLabel);
        if (s == null) {
            s = new Stack<SJRecursionType>();
            labels.put(sjLabel, s);
        }
        s.push(rt);
    }

    public void exitScope(SJLabel lab) {
        Stack<SJRecursionType> stack = labels.get(lab);
        stack.pop();
        if (stack.isEmpty()) labels.remove(lab);
    }
}

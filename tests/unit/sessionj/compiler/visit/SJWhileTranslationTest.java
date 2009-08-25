package sessionj.compiler.visit;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.Position;
import polyglot.frontend.*;
import polyglot.frontend.Compiler;
import polyglot.frontend.goals.Goal;
import polyglot.main.Options;
import sessionj.ExtensionInfo;
import sessionj.SJConstants;
import sessionj.ast.sessops.compoundops.*;
import sessionj.ast.sessvars.SJVariable;
import sessionj.ast.sessvars.SJLocalSocket_c;
import sessionj.visit.SJCompoundOperationTranslator;

import java.util.*;
import java.util.regex.Pattern;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SJWhileTranslationTest {
    private Position dummyPos;
    private Expr trueLit;
    private ExtensionInfo extInfo;
    private SJCompoundOperationTranslator visitor;
    private Stmt emptyBlock;
    private List targets;
    private List sources;

    @Test
    public void translateOutwhile() throws SemanticException, IOException {
        verifyBlock(
                visitor.leaveCall(
                    null, null,
                    new SJOutwhile_c(dummyPos, trueLit, emptyBlock, targets),
                    null),
                Pattern.quote(
                "{\n" +
                "    sessionj.runtime.net.LoopCondition ") +
                        javaIdentifier() +
                        Pattern.quote(" =\n" +
                "      sessionj.runtime.net.SJRuntime.negociateOutsync(\n" +
                "        false, new sessionj.runtime.net.SJSocket[] { tgtSock });\n" +
                "    while (") +
                                "\\1" +
                        Pattern.quote(".call(true)) {  }\n" +
                "}")
        );
    }

    private void verifyBlock(Node n, String expectedCode) throws IOException {
        assert n instanceof Block;
        assert n.isTypeChecked();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        n.prettyPrint(os);
        assert os.toString().matches(expectedCode);
        os.close();
    }

    @Test
    public void translateInwhile() throws SemanticException, IOException {
        verifyBlock(
            visitor.leaveCall(null, null,
                    new SJInwhile_c(dummyPos, emptyBlock, sources),
                    null),
            Pattern.quote(
                "{\n" +
                "    sessionj.runtime.net.SJRuntime.negotiateNormalInwhile(\n" +
                "      new sessionj.runtime.net.SJSocket[] { srcSock });\n" +
                "    while (sessionj.runtime.net.SJRuntime.insync(\n" +
                "             new sessionj.runtime.net.SJSocket[] { srcSock })) {\n" +
                "        \n" +
                "    }\n" +
                "}"
            )
        );
    }

    @Test
    public void translateOutinwhile() throws SemanticException, IOException {
        verifyBlock(
                visitor.leaveCall(null, null,
                        new SJOutInwhile_c(dummyPos, trueLit, emptyBlock, targets, sources),
                        null),
                Pattern.quote(
                "{\n" +
                "    sessionj.runtime.net.LoopCondition ") +
                        javaIdentifier() +
                        Pattern.quote(" =\n" +
                "      sessionj.runtime.net.SJRuntime.negotiateOutsync(\n" +
                "        false, new sessionj.runtime.net.SJSocket[] { tgtSock });\n" +
                "    boolean ") +
                        javaIdentifier() +
                        Pattern.quote(" =\n" +
                "      sessionj.runtime.net.SJRuntime.negotiateInterruptingInwhile(\n" +
                "        new sessionj.runtime.net.SJSocket[] { srcSock });\n" +
                "    while (") +
                                "\\1" +
                        Pattern.quote(".call(\n" +
                "             sessionj.runtime.net.SJRuntime.interruptingInsync(\n" +
                "               true, ") +
                                "\\2" +
                        Pattern.quote(",\n" +
                "               new sessionj.runtime.net.SJSocket[] { srcSock }))) {\n" +
                "        \n" +
                "    }\n" +
                "}")
        );
    }

    private String javaIdentifier() {
        return "([\\$\\w]+)";
    }

    @BeforeMethod
    protected void setUp() throws SemanticException {
        dummyPos = new Position("", "");
        trueLit = new BooleanLit_c(dummyPos, true);

        extInfo = new ExtensionInfo() {
            @Override
            public Scheduler createScheduler() {
                return new SJScheduler(this) {
                    @Override
                    public Goal currentGoal() {
                        return new DummyGoal();
                    }
                };
            }
        };
        Compiler compiler = new Compiler(extInfo);
        extInfo.initCompiler(compiler);
        Options.global = new Options(extInfo);
        TypeSystem ts = extInfo.typeSystem();
        SJConstants.SJ_SOCKET_INTERFACE_TYPE = ts
                .typeForName(SJConstants.SJ_SOCKET_INTERFACE);
        Job job = new Job(extInfo, extInfo.jobExt(), new Source("FakeFile.sj", "", new Date()), null);
        visitor = new SJCompoundOperationTranslator
                (job, ts, extInfo.nodeFactory());
        visitor.begin();
        visitor = (SJCompoundOperationTranslator) visitor.context
                (visitor.context().pushSource(new ImportTable(ts, new Package_c(ts, ""))));
        ParsedClassType scope = ts.createClassType();
        scope.name("TestClass");
        scope.kind(ClassType.TOP_LEVEL);
        visitor = (SJCompoundOperationTranslator) visitor.context(
                visitor.context().pushClass(scope, scope)
        );
        emptyBlock = new Block_c(dummyPos, new LinkedList());
        SJVariable sockVar = new SJLocalSocket_c(dummyPos, new Id_c(dummyPos, "tgtSock"));
        targets = new LinkedList();
        targets.add(sockVar);
        sources = new LinkedList();
        SJVariable sockVar2 = new SJLocalSocket_c(dummyPos, new Id_c(dummyPos, "srcSock"));
        sources.add(sockVar2);
    }

}

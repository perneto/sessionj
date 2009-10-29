package sessionj.runtime.net;

public interface SJSelectorInternal extends SJSelectorBase {
    boolean registerAccept(SJServerSocket ss) throws Exception;
    boolean registerInput(SJSocket s) throws Exception;
}

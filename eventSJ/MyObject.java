import java.io.*;

public class MyObject implements Serializable {
    private byte[] byteArray;
    public boolean count;
    public static int DEFAULT_SIZE = 1024;
    
    public MyObject() {
        super();
        byteArray = (new byte[DEFAULT_SIZE]);
    }
    
    public MyObject(boolean count) {
        super();
        byteArray = (new byte[DEFAULT_SIZE]);
        this.count = count;
    }
    
    public byte[] getByteArray() { return byteArray; }
    
    final public static String jlc$CompilerVersion$jl = "2.3.0";
    final public static long jlc$SourceLastModified$jl = 1260361707000L;
    final public static String jlc$ClassType$jl =
      ("H4sIAAAAAAAAAJVXXWwUVRS+s9tu/xbbXUshbWkL1EgjbI0G/OkD1NKG0kXW" +
       "bktkCS53Z+9up52d\nGefe3U4JIaCJIA8mBvAvKi8mRsODQtQHjfoAavyJCS" +
       "bgC7xglEQx8qLEoPHcO7N/M9sKTebu/Tn3\n3HvO+c53T09fR/XURBFKKFV0" +
       "bTbCFgxCRaunZonMaCS+PYZNStIjKqZ0ChaS8r5/lV3vh57Z5kNS\nAoU1fV" +
       "hVMJ2aMfV8dmZqRqGWifoMXV3IqjpzNHp0PLr21vy3R7Z3+VFrArUqWpxhps" +
       "gjusaIxRIo\nmCO5FDHpcDpN0gkU0ghJx4mpYFXZD4K6BgdTJathljcJnSRU" +
       "VwtcMEzzBjHFmcXJKArKukaZmZeZ\nblKG2qKzuIAH80xRB6MKZUNRFMgoRE" +
       "3Tp9FB5Iui+oyKsyDYES1aMSg0Do7xeRBvVuCaZgbLpLil\nbk7R0gz1uneU" +
       "LO6fAAHY2pAjbEYvHVWnYZhAYftKKtayg3FmKloWROv1PJzCUOeiSkGo0cDy" +
       "HM6S\nJEMr3XIxewmkmoRb+BaGlrvFhCaIWacrZhXR2hkI/nMs9lcfRBzunC" +
       "ayyu8fgE09rk2TJENMosnE\n3ngzHzkxvjvf7UMIhJe7hG2Z4Xs+mo5e+6zX" +
       "lumqIbNTYDEp39rUverC8E9Nfn6NRkOnCodCleUi\nqjFnZcgyAN0dJY18MV" +
       "Jc/Hzyi92H3iW/+lDjOArIuprPaeOoiWjpEaffAP2oohF7dmcmQwkbR3Wq\n" +
       "mAroYgzuyCgq4e6oh76B2YzoWwZCqAE+Cb4OZP/5ecNQy44F26IIneXCIYu3" +
       "d81LEty22505KsBs\nm66miZmU37769YHRieeP2nHg2HEOY6ixqBRJklB0N4" +
       "eUbfKwaeIFDnXr8IVVr36J3wAHgiFU2U/E\nPX3zdbyFTQ8syQQj5Twahx6G" +
       "MCfl9kPXOl/74Z3zPuSvyQbR0uSYbuawygNahH/YOc69Ajjod6Ox\n1tm/H9" +
       "tx9uI3l9eVcclQvyddvDs53Ne4HW3qMkkDnZTVv/z3tj+O1z/ygQ/VQQ4Biz" +
       "AMsYeU7HGf\nUQX7oSKFcFv8UdSS8RjezMBB8xUG8zYo+q0QiiYHLC0OcPgY" +
       "CahU4AXC1e6yQRDUzWcD91/6pOW8\ncEqRy1orSC9OmJ0ZoTJEpkxCYP7yK7" +
       "HjJ68f2QOHW4YhYCExFDDyKVWRLdiyojqZuAVpjqzfzgy1\nvbCBfihQ0KTk" +
       "cnmGUyoB8sWqqs+TdJIJ9glVMJ0gGHBWMAVEBZyXVEGR7Q5DKgCCa1BBZGX7" +
       "iZcG\nXr/EycIQ/lrOfSNuiiwx0S/xdp1nkY+7eWa0l82G9J6zDQgOxPdu33" +
       "d0jbC8Ut36ysEKT4janBAt\nqxUi3vTVvJRk+5Z6GTRmKjkgqILDoC/2vPXz" +
       "2auT7TbE7WdmrYfpK/fYT424QIvBY7Z6qROE9Ln7\nVp8+OHklZVNwuDrGo1" +
       "o+t/HUj2RgS1CuQTwNKV1XCdb+3/mDUoVDhdxGyztjlfb6xbQfLFi/JC2N\n" +
       "8ce7nLXKgT8Pn/n+ZNCHfAnUoNAxRcMqByV93OahGs+WS8X+T6ffvPkduyLM" +
       "LSc+v1iX5eXoXbiC\nkx6+WAgF3juV86GGBGoTpQfW2C6s5nmCJaB4oCPOZB" +
       "Qtq1qvLgRsOh8qEVu3O+oVx7oppxwi6HNp\n3m90sUwzfN3w1TsQrndBWJIM" +
       "3tkqNvSIdnWJExoMUylgXq2hptQCI+KNod4qQszbUD7j77/h+7ij\nX9BpXQ" +
       "pT+1bu8stbXVUVTcKIZsOGyYPO7yYGCuEWnvzi4y2GJSFhSsxrio+B5zhC+N" +
       "xasCagEi3L\nZtzq/XAr3p0o47P4bPJxmDkPLg8dlE66Rjj/FddsylH0SKmI" +
       "hUWrBhv0uty3Q1heRleo0PWEf0b5\nyieibAPDU3VWbxqqhkOzSaBo1qaqQN" +
       "FpiJ+JxQnrtil2HzhU5vYXbW8r+8WGtM2NCAmBzVWMugK+\ngAPHQG04ivqq" +
       "jx+i5+2QbLT1SS59nJnXOVUYKv569c3VwITAAkCBiv9KABTBraNjw9PRqWR8" +
       "PDHK\nV6edM60Kj6GyUyoYbQ+n4FWL1daCfo88eSP4HD631+e48CHIKaYbG1" +
       "RSIGoNkGxYkg7dkPHHNw/c\nu77lF0i7RQq0kDM5WY0MUbnhO6zcePtUVUx7" +
       "4Qs7MQjf2St526DjIcoS9liRiASYhfyE9xEvDqyK\nqtkQWMhY6D/PXH0+Ew" +
       "8AAA==");
}

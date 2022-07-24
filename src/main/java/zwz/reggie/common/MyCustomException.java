package zwz.reggie.common;

//  自定义异常
public class MyCustomException extends RuntimeException{

    public MyCustomException(String msg) {
        super(msg);
    }

}

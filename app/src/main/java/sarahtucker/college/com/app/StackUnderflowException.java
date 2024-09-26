package sarahtucker.college.com.app;

/**
 * Created by android on 7/9/2018.
 */
public class StackUnderflowException extends Exception{
    private static final long serialVersionUID = 1L;

    public StackUnderflowException(){
        super();
    }

    public StackUnderflowException(String msg){
        super(msg);
    }

    public StackUnderflowException(Throwable t){
        super(t);
    }

    public StackUnderflowException(String msg, Throwable t){
        super(msg, t);
    }
}
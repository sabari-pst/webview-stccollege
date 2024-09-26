package sarahtucker.college.com.app;

import java.util.ArrayList;

/**
 * Created by android on 7/9/2018.
 */

public class Stack {
     ArrayList<String> stack = null;

    public Stack()
    {
        stack = new ArrayList<String>();
    }

    public void push(String value) {
        System.out.println("Push " + value + " in stack");
        stack.add(value);
    }

    public void pop() throws StackUnderflowException
    {
        if (!isEmpty()) {
            System.out.println("Pop " + stack.get(stack.size() - 1) + " from stack");
            stack.remove(stack.size() - 1);
        } else {
            throw new StackUnderflowException("Stack is empty !");
        }
    }

    public boolean isEmpty()
    {
        if (stack.size() == 0) return true;
        else return false;
    }

    public String top() throws StackUnderflowException
    {
        if (!isEmpty())
        {
            return stack.get(stack.size() - 1);
        } else
        {
            throw new StackUnderflowException("Stack is empty !");
        }
    }
}
import java.util.EmptyStackException;
import datastructures.list.LinkedList;
import datastructures.stack.LinkedStack;
import lexer.Token;
import lexer.TokenType;

/** Evaluates postfix expressions and stores the previous successful result. */
public class PostfixCalculator
{
  private double prevVal;
  private boolean prevValValid;
  private LinkedStack<Double> stack;

  public PostfixCalculator()
  {
    prevVal = 0.0;
    prevValValid = false;
    stack = new LinkedStack<>();
  }

  public boolean evaluate(LinkedList<Token> tokList)
  {
    if (tokList == null)
    {
      prevValValid = false;
      return false;
    }

    stack.clear();
    try
    {
      for (int i = 1; i <= tokList.getLength(); i++)
      {
        Token token = tokList.getEntry(i);
        if (token == null)
          return fail();

        if (token.getType() == TokenType.NUM)
        {
          stack.push(Double.parseDouble(token.getValue()));
        }
        else if (token.getType() == TokenType.MEMORY)
        {
          if (!prevValValid)
            return fail();
          stack.push(prevVal);
        }
        else if (token.getType() == TokenType.OP)
        {
          if (!processOperation(token.getValue()))
            return fail();
        }
        else
          return fail();
      }

      if (stack.isEmpty())
        return fail();

      double result = stack.pop();
      if (!stack.isEmpty() || !Double.isFinite(result))
        return fail();

      prevVal = result;
      prevValValid = true;
      return true;
    }
    catch (RuntimeException ex)
    {
      return fail();
    }
  }

  public double getResult()
  {
    if (!prevValValid)
      throw new IllegalStateException("No valid previous result.");
    return prevVal;
  }

  private boolean processOperation(String op)
  {
    if (stack == null || stack.isEmpty())
      return false;

    double right = stack.pop();
    if (stack.isEmpty())
      return false;
    double left = stack.pop();
    double result;

    switch (op)
    {
      case "+": result = left + right; break;
      case "-": result = left - right; break;
      case "*": result = left * right; break;
      case "/":
        if (right == 0.0) return false;
        result = left / right;
        break;
      case "^": result = Math.pow(left, right); break;
      default: return false;
    }

    if (!Double.isFinite(result))
      return false;
    stack.push(result);
    return true;
  }

  private boolean fail()
  {
    stack.clear();
    prevValValid = false;
    return false;
  }
}

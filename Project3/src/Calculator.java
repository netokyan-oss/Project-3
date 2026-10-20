import java.util.Scanner;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import datastructures.list.LinkedList;
import datastructures.stack.LinkedStack;

/**
 * This class provides the interface to the calculator.
 * @author Wendy-Beth Minton, YOUR-NAME-HERE
 */
public class Calculator
{

  /**
   * This method returns the relative precedence of the operators.
   * @param tok the token to check the precedence of.
   * @return a value between -1 and 3.
   */
  public static int getPrec(Token tok)
  {
    if (tok == null)
      return -1;

    switch (tok.getType())
    {
      case OP:
        String op = tok.getValue();
        if (op.equals("+") || op.equals("-"))
          return 1;
        if (op.equals("*") || op.equals("/"))
          return 2;
        if (op.equals("^"))
          return 3;
        return -1;
      case NUM:
      case MEMORY:
      case LPAREN:
      case RPAREN:
        return 0;
      default:
        return -1;
    }
  }

  /**
   * Converts an infix expression to postfix.
   *
   * @param tokenizedInput the list of tokens in infix.
   * @return the postfix list or null if unsuccessful.
   */
  public static LinkedList<Token> convertToPostfix(
      LinkedList<Token> tokenizedInput)
  {
    if (tokenizedInput == null)
      return null;

    LinkedList<Token> postfixExpr = new LinkedList<>();
    LinkedStack<Token> operators = new LinkedStack<>();

    for (int i = 1; i <= tokenizedInput.getLength(); i++)
    {
      Token currTok = tokenizedInput.getEntry(i);
      if (currTok == null)
        return null;

      switch (currTok.getType())
      {
        case NUM:
        case MEMORY:
          postfixExpr.insert(postfixExpr.getLength() + 1, currTok);
          break;
        case LPAREN:
          operators.push(currTok);
          break;
        case RPAREN:
          boolean foundLeftParen = false;
          while (!operators.isEmpty())
          {
            Token top = operators.pop();
            if (top.getType() == TokenType.LPAREN)
            {
              foundLeftParen = true;
              break;
            }
            postfixExpr.insert(postfixExpr.getLength() + 1, top);
          }
          if (!foundLeftParen)
            return null;
          break;
        case OP:
          while (!operators.isEmpty()
              && operators.peek().getType() == TokenType.OP
              && getPrec(operators.peek()) >= getPrec(currTok))
          {
            postfixExpr.insert(postfixExpr.getLength() + 1, operators.pop());
          }
          operators.push(currTok);
          break;
        case END:
          break;
        default:
          return null;
      }
    }

    while (!operators.isEmpty())
    {
      Token top = operators.pop();
      if (top.getType() == TokenType.LPAREN || top.getType() == TokenType.RPAREN)
        return null;
      postfixExpr.insert(postfixExpr.getLength() + 1, top);
    }

    return postfixExpr;
  }

  /**
   * Runs the very simple Read, Evaluate, Print, Loop (REPL). This
   * continually prompts the user for a string (a math expression) and
   * outputs the result of the calculation.
   */
  public static void doREPL()
  {
    String expression = "";
    Scanner scan = new Scanner(System.in);
    Lexer lex = new Lexer();
    LinkedList<Token> tokenizedInput;
    PostfixCalculator calc = new PostfixCalculator();

    // Run the standard read, parse, evaluate loop.
    while (!expression.equals("quit"))
    {
      System.out.print("> ");
      expression = scan.nextLine();

      // If the user didn't ask to quit, try to process the expression.
      if (!expression.equals("") && !expression.equals("quit"))
      {
        // Perform lexical analysis, convert the expression to postfix, and
        // evaluate.
        if (lex.doLexicalAnalysis(expression))
        {
          tokenizedInput = convertToPostfix(lex.getTokenList());
          if (tokenizedInput == null)
            System.out.println("Failed to convert to postfix");
          else
          {
            if (calc.evaluate(tokenizedInput))
              System.out.println(calc.getResult());
            else
              System.out.println("Failed to evaluate expression\n");
          }
        }
        else
        {
          System.out.println("Error: Invalid expression.");
        }
      }
    }
  }

  public static void main(String[] args)
  {
    System.out.println("Welcome to the Stack Calculator");
    System.out.println("Type quit to exit.\n");
    doREPL();
  }
}

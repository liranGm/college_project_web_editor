package analyzer.graphes;

import analyzer.reader.CodeLine;

import java.util.List;

public class Condition
{
    static String ifRegex = "\\s*\\bif\\b\\s*";
    private static String whileRegex = "\\s*\\bwhile\\b\\s*";
    private static String forRegex = "\\s*\\bfor\\b\\s*";

    private static String noName = "No name";
    String line;
    ParamterItem parameter1;
    ParamterItem parameter2;
    String operator;
    CodeLine codeLine;
    List<ParamterItem> parameters;
    List<VariableItem> vars;
    analyzer.reader.Enums.LineType codeLineType;

    private Condition( List<ParamterItem> params, List<VariableItem> vars, CodeLine codeLine, String line,ParamterItem parameter1,ParamterItem parameter2,String operator )
    {
        this.parameters = params;
        this.vars = vars;
        this.codeLine = codeLine;
        this.line = line;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.operator = operator;
        this.codeLineType = codeLine.getType();
    }

    public static Condition Create(CodeLine code_line, List<VariableItem> variables, List<ParamterItem> params)
    {
        String condition = code_line.getText();
        analyzer.reader.Enums.LineType type = code_line.getType();

        condition = RemoveSpaces(condition);
        condition = RemoveConditionText(type, condition);
        condition = RemoveBracket(condition);

        if(type == analyzer.reader.Enums.LineType.For)
        {
            // The first time is to decrease the before the condition part and decrease spaces in the line
            // The second time is to decrease the after the condition part
            condition = condition.substring(condition.indexOf(";")+1,condition.length()-1)
                .replaceAll(" ","");
            condition = condition.substring(0,condition.indexOf(";"));
        }

        String[] results = SplitConditionParamsAndOperator(condition);
        ParamterItem p1 = ParseParameter(results[0], variables, params);
        String operator = results[1];
        ParamterItem p2 = ParseParameter(results[2], variables, params);

        return new Condition(params, variables, code_line, condition, p1, p2, operator);
    }

    private static String RemoveSpaces(String text)
    {
        return text.replaceAll(" ","");
    }

    private static String RemoveConditionText(analyzer.reader.Enums.LineType type, String text)
    {
        if(type == analyzer.reader.Enums.LineType.If)
        {
            return text.replaceAll(ifRegex,"");
        }
        if(type == analyzer.reader.Enums.LineType.While)
        {
            return text.replaceAll(whileRegex,"");
        }
        if(type == analyzer.reader.Enums.LineType.For)
        {
            return text.replaceAll(forRegex,"");
        }
        return text;
    }

    private static String RemoveBracket(String text)
    {
        while(text.startsWith(" "))     // Remove spaces if exist
        {
            text = text.substring(1);
        }

        text = text.substring(1);      // Remove the '('

        while(text.endsWith(" "))       // Remove spaces from the end
        {
            text = text.substring(0, text.length() - 1);
        }
        text = text.substring(0, text.length() - 1);       // Remove the ')'
        return text;
    }

    private static String[] SplitConditionParamsAndOperator(String condition)
    {
        String[] result = new String[3];
        String ConditionOparator="";
        String [] arr =condition.split("(==)|(!=)|(<=)|(>=)|(<)|(>)");

        if (condition.contains("=="))          //check string for measure
            ConditionOparator = "==";      //split string at those points
        else if (condition.contains("!="))     //a==2 -> ["a", "2"]
            ConditionOparator = "!=";
        else if (condition.contains(">="))
            ConditionOparator = ">=";
        else if (condition.contains("<="))
            ConditionOparator = "<=";
        else if (condition.contains(">"))
            ConditionOparator = ">";
        else if (condition.contains("<"))
            ConditionOparator = "<";

        result[0] = arr[0];
        result[1] = ConditionOparator;
        result[2] = arr[1];

        return result;

    }

    private static ParamterItem ParseParameter(String parameter, List<VariableItem> variables, List<ParamterItem> params)
    {
        String name = noName;
        Object value = parameter;
        analyzer.graphes.Enums.Variables type = ExtractType(parameter);

        if(type == null)        // parameter is from params or local variables
        {
            if(params != null)
                for (ParamterItem item: params)
                {
                    if(item.getName().equals(parameter))
                        return item;
                }
            if(variables != null)
                for (VariableItem item: variables)
                {
                    if(item.getName().equals(parameter))
                        return new ParamterItem(item.getName(), item.getType(), item.getValue());
                }
        }

        return new ParamterItem(name, type, value);
    }


    private String[] getParamsFromExpression(String line){
        String[] retParams = new String[2];
        if(codeLineType == analyzer.reader.Enums.LineType.If){


        }else if(codeLineType == analyzer.reader.Enums.LineType.For){

        }else if(codeLineType == analyzer.reader.Enums.LineType.While){

        }

        return retParams;
    }

    private static analyzer.graphes.Enums.Variables ExtractType(String parameter)
    {
        if(parameter.startsWith("\"")||(parameter.startsWith("'")))    // "a" == "b"
        {
            return analyzer.graphes.Enums.Variables.String;
        }
        else if(Character.isDigit(parameter.charAt(0)))         // 6 == 7
        {
            return analyzer.graphes.Enums.Variables.Double;
        }
        return null;      // as default         x == y      => local var or param
    }


    public boolean CanRun(List<VariableItem> Vars)
    {
        if (operator.equals("=="))
        {
            return parameter1.getValue().equals(parameter2.getValue());
        }
        if (operator.equals("!="))
        {
            return !parameter1.getValue().equals(parameter2.getValue());
        }

        this.vars = Vars;
        double[] calculatedParams = GetCalculatedParameters();
        double param1 =calculatedParams[0];
        double param2 =calculatedParams[1];

        if (operator.equals(">="))
            return param1 >= param2 ;
        if (operator.equals("<="))
            return param1 <= param2 ;
        if (operator.equals(">"))
            return param1 > param2 ;
        if (operator.equals("<"))
            return param1 < param2 ;

        return false;
    }


    public double[] GetCalculatedParameters()
    {
        String[] SplittedCondition = SplitConditionParamsAndOperator(this.line);

        double param1 = MathResolver.Resolve(SplittedCondition[0], vars, parameters);//getParameterValue(SplittedCondition[0]);
        double param2 = MathResolver.Resolve(SplittedCondition[2], vars, parameters);

        return new double[]{param1, param2};
    }

    public void UpdateParameters(List<ParamterItem> parameters, List<VariableItem> variables)
    {
        if(parameter1.getName() == noName)
            parameter1 = ParseParameter(parameter1.getValue().toString(), variables, parameters);
        else
            parameter1 = ParseParameter(parameter1.getName(), variables, parameters);

        if(parameter2.getName() == noName)
            parameter2 = ParseParameter(parameter2.getValue().toString(), variables, parameters);
        else
            parameter2 = ParseParameter(parameter2.getName(), variables, parameters);
    }
}

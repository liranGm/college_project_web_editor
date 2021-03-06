package analyzer.graphes;

import analyzer.reader.CodeLine;
import analyzer.reader.CodeReader;
import java.util.List;

public class VariableItem extends BaseItem implements INameValue
{
    private Enums.Variables type;
    private Object value;
    private String name;



    private boolean isUsed = false;

    private String doubleRegex = "(?s).*\\bdouble\\b.*";
    private String intRegex = "(?s).*\\bint\\b.*";
    private String charRegex = "(?s).*\\bchar\\b.*";
    private String boolRegex = "(?s).*\\bbool\\b.*";
    private String floatRegex = "(?s).*\\bfloat\\b.*";
    private String stringRegex = "(?s).*\\bstring\\b.*";

    public CodeLine getLine() {
        return Line;
    }

    public void setLine(CodeLine line) {
        Line = line;
    }

    public VariableItem(CodeLine line, CodeReader reader, List<VariableItem> vars)
    {
        super(line, reader, vars);
        Initialize(line.getText());
    }

    private void Initialize(String line)
    {
        type = ExtractType(line);
        value = ExtractValue(line);
        name = ExtractName(line);
    }

    public void setValue(Object newValue) {
        this.value = newValue;
    }

    private String ExtractName(String line)
    {
        String[] splitLine = line.split(" ");
        // splitLine[0] => Type - int, double, etc
        //splitLine[1] => Name
        String lineWithoutType = "";
        for(int i=1; i<splitLine.length;i++)
            lineWithoutType += splitLine[i];

        lineWithoutType = lineWithoutType.replaceAll(" ", "");

        splitLine = lineWithoutType.split("=");

        return splitLine[0];
    }

    private Enums.Variables ExtractType(String line)
    {
        if(line.matches(doubleRegex))
            return Enums.Variables.Double;
        else if(line.matches(intRegex))
            return Enums.Variables.Int;
        else if(line.matches(charRegex))
            return Enums.Variables.Char;
        else if(line.matches(boolRegex))
            return Enums.Variables.Bool;
        else if(line.matches(floatRegex))
            return Enums.Variables.Float;
        else /*if(line.matches(stringRegex))*/
            return Enums.Variables.String;
    }

    private Object ExtractValue(String line/*Will be removed*/)
    {
        if(line.contains("="))
            return line.substring(line.indexOf("=")+1,line.indexOf(";")).replaceAll(" ","");
        else
            return null;
    }


    @Override
    protected void CreateInternalItems() {
        return;
    }

    @Override
    public List<IGraphItem> getItems() {
        return null;
    }

    @Override
    public IGraphResult Execute(List<ParamterItem> parameters) {
        IGraphResult result = new GraphResult();
//       TODO
//        value = ExtractValue(parameters);
        if(!executed)
        {
            result.setRowsCover(1);
            result.AddInternalCodeLine(this.Line);
            executed = true;
        }
        result.setRowsCount(1);
        return result;
    }

    @Override
    public boolean CanExecute(List<ParamterItem> parameters) {
        return true;
    }

    public String getName() {
        return name;
    }

    public Enums.Variables getType() {
        return type;
    }

    @Override
    public boolean IsValueNull() {
        return value == null;
    }

    public Object getValue()
    {
        isUsed = true;
        return value;
    }

    public Object getValueWithoutUnusedFlag()
    {
        return value;
    }


    public boolean getIsUsed() {
        return isUsed;
    }
}

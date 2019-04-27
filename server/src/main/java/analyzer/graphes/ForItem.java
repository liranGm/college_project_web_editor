package analyzer.graphes;

import analyzer.reader.CodeLine;
import analyzer.reader.CodeReader;

import java.util.List;

public class ForItem extends BaseItem
{

    public ForItem(CodeLine line, CodeReader reader, List<VariableItem> vars)
    {
        super(line, reader, vars);
    }

    @Override
    public int Execute(List<ParamterItem> parameters) {
        int sum = 0;
        Condition condition = Condition.Create(Line, Vars, parameters);

        while (condition.CanRun())
        {
            for (IGraphItem item: Items)
            {
                sum += item.Execute(parameters);
            }

            condition.UpdateParameters(parameters, Vars);
        }
        // while Condition.CanRun()

        // Update condition and while...
        return sum;
    }

    @Override
    public boolean CanExecute(CodeLine line) {
        return false;
    }
}
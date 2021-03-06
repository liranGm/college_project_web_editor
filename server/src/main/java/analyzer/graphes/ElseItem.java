package analyzer.graphes;

import analyzer.reader.CodeLine;
import analyzer.reader.CodeReader;
import java.util.List;

public class ElseItem extends BaseItem
{
    private IfItem ifItem;

    public ElseItem(CodeLine line , CodeReader reader, List<VariableItem> vars)
    {
        super(line, reader, vars);
    }

    @Override
    public IGraphResult Execute(List<ParamterItem> parameters) {
        GraphResult result = new GraphResult();

        if (ifItem.executed)
        {
            return result;
        }
        if(!executed)
        {
            result.setRowsCover(result.getRowsCover() + 1 );
            result.AddInternalCodeLine(this.Line);
            executed = true;
        }

        result.setRowsCount(result.getRowsCount() + 1);

        for (IGraphItem item: Items)
        {
            IGraphResult internalResult = item.Execute(parameters);

            if(CheckInfinityResult(internalResult))
                return internalResult;

            result.setRowsCount(result.getRowsCount() + internalResult.getRowsCount());
            result.setRowsCover(result.getRowsCover() + internalResult.getRowsCover());

            result.AddInternalResult(internalResult);
        }

        return result;
    }

    @Override
    public boolean CanExecute(List<ParamterItem> parameters) {
        return !ifItem.executed;
    }

    public void setIfItem(IfItem ifItem) {
        this.ifItem = ifItem;
    }
}

package lang2.interpreter.ruleplugins;

import lang2.interpreter.Context2;
import lombok.NonNull;

import java.util.List;

public interface PluginRule
{
	String declareIdentifier();
	List<String> declareScope();
	void executeDirectly(@NonNull Context2 context);
}

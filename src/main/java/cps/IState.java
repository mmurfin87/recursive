package cps;

import lombok.NonNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IState
{
	boolean eq(@NonNull final String key, @NonNull final String expected);

	Set<String> keys();

	String get(@NonNull final String key);

	Map<String, String> map();
}

package com.efsol.util;

import java.util.HashSet;
import java.util.Set;

public class Utils {
	public static final boolean same(Object a, Object b) {
		if (null == a && null == b) return true;
		if (null == a || null == b) return false;
		return a.equals(b);
	}

	public static <T> Set<T> difference(final Set<T> setOne, final Set<T> setTwo) {
	     Set<T> result = new HashSet<T>(setOne);
	     result.removeIf(setTwo::contains);
	     return result;
	}
}

/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2015 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script.function;

import com.riversoft.core.script.annotation.ScriptSupport;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 数学函数
 * 
 * @author woden
 *
 */
@ScriptSupport("math")
public class MathUtil {

	/**
	 * 汇总
	 * 
	 * @param list
	 * @return
	 */
	public Number sum(Number... list) {
		return sum(Arrays.asList(list));
	}

	/**
	 * 汇总
	 * 
	 * @param list
	 * @return
	 */
	public Number sum(List<?> list) {
		BigDecimal result = new BigDecimal(0);
		if (list != null) {
			for (Object o : list) {
				if (o instanceof Number) {
					result = result.add(new BigDecimal(((Number) o).doubleValue()));
				}
			}
		}
		return result;
	}

	/**
	 * 平均
	 * 
	 * @param list
	 * @return
	 */
	public Number avg(Number... list) {
		return avg(Arrays.asList(list));
	}

	/**
	 * 平均
	 * 
	 * @param list
	 * @return
	 */
	public Number avg(List<?> list) {
		if (list == null || list.size() < 1) {
			return 0;
		}

		return ((BigDecimal) sum(list)).divide(new BigDecimal(list.size()));
	}

	/**
	 * 最大
	 * 
	 * @param list
	 * @return
	 */
	public Number max(Number... list) {
		return max(Arrays.asList(list));
	}

	/**
	 * 最大
	 * 
	 * @param list
	 * @return
	 */
	public Number max(List<?> list) {
		if (list == null || list.size() < 1) {
			return 0;
		}

		double result = Double.MIN_VALUE;
		if (list != null) {
			for (Object o : list) {
				if (o instanceof Number) {
					result = Math.max(result, ((Number) o).doubleValue());
				}
			}
		}
		return result;
	}

	/**
	 * 最小
	 * 
	 * @param list
	 * @return
	 */
	public Number min(Number... list) {
		return min(Arrays.asList(list));
	}

	/**
	 * 最大
	 * 
	 * @param list
	 * @return
	 */
	public Number min(List<?> list) {
		if (list == null || list.size() < 1) {
			return 0;
		}

		double result = Double.MAX_VALUE;
		if (list != null) {
			for (Object o : list) {
				if (o instanceof Number) {
					result = Math.min(result, ((Number) o).doubleValue());
				}
			}
		}
		return result;
	}
}

/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.mysema.query.types.Expression;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.Predicate;

/**
 * QueryMetadata defines query metadata such as query sources, filtering
 * conditions and the projection
 *
 * @author tiwe
 */
public interface QueryMetadata extends Serializable {

    /**
     * Add the given group by expressions
     *
     * @param o
     */
    QueryMetadata addGroupBy(Expression<?>... o);

    /**
     * Add the given having expressions
     *
     * @param o
     */
    QueryMetadata addHaving(Predicate... o);

    /**
     * Add the given query join
     *
     * @param joinType
     * @param expr
     */
    QueryMetadata addJoin(JoinType joinType, Expression<?> expr);

    /**
     * Add the given query join
     *
     */
    QueryMetadata addJoin(JoinExpression join);

    /**
     * Add the given join condition to the last given join
     *
     * @param o
     */
    QueryMetadata addJoinCondition(Predicate o);

    /**
     * Add the given order specifiers
     *
     * @param o
     */
    QueryMetadata addOrderBy(OrderSpecifier<?>... o);

    /**
     * Add the given projections
     *
     * @param o
     */
    QueryMetadata addProjection(Expression<?>... o);

    /**
     * Add the given where expressions
     *
     * @param o
     */
    QueryMetadata addWhere(Predicate... o);

    /**
     * Clear the order expressions
     */
    QueryMetadata clearOrderBy();

    /**
     * Clear the projection
     */
    QueryMetadata clearProjection();

    /**
     * Clear the where expressions
     */
    QueryMetadata clearWhere();

    /**
     * Clone this QueryMetadata instance
     *
     * @return
     */
    QueryMetadata clone();

    /**
     * Get the group by expressions
     *
     * @return
     */
    List<? extends Expression<?>> getGroupBy();

    /**
     * Get the having expressions
     *
     * @return
     */
    @Nullable
    Predicate getHaving();

    /**
     * Get the query joins
     *
     * @return
     */
    List<JoinExpression> getJoins();

    /**
     * Get the QueryModifiers
     *
     * @return
     */
    @Nullable
    QueryModifiers getModifiers();

    /**
     * Get the OrderSpecifiers
     *
     * @return
     */
    List<OrderSpecifier<?>> getOrderBy();

    /**
     * Get the projection
     *
     * @return
     */
    List<? extends Expression<?>> getProjection();

    /**
     * Get the parameters
     *
     * @return
     */
    Map<ParamExpression<?>,Object> getParams();

    /**
     * Get the expressions aggregated into a single boolean expression or null,
     * if none where defined
     *
     * @return
     */
    @Nullable
    Predicate getWhere();

    /**
     * Get whether the projection is distinct
     *
     * @return
     */
    boolean isDistinct();

    /**
     * Get whether the projection is unique
     *
     * @return
     */
    boolean isUnique();

    /**
     * Reset the projection
     */
    QueryMetadata reset();

    /**
     * @param distinct
     */
    QueryMetadata setDistinct(boolean distinct);

    /**
     * @param limit
     */
    QueryMetadata setLimit(@Nullable Long limit);

    /**
     * @param restriction
     */
    QueryMetadata setModifiers(QueryModifiers restriction);

    /**
     * @param offset
     */
    QueryMetadata setOffset(@Nullable Long offset);

    /**
     * @param unique
     */
    QueryMetadata setUnique(boolean unique);

    /**
     * @param <T>
     * @param param
     * @param value
     */
    <T> QueryMetadata setParam(ParamExpression<T> param, T value);

    /**
     * @param flag
     */
    QueryMetadata addFlag(QueryFlag flag);

    /**
     * @param flag
     * @return
     */
    boolean hasFlag(QueryFlag flag);

    /**
     * @return
     */
    Set<QueryFlag> getFlags();
}

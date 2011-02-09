/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.mysema.query.types.Expression;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;

/**
 * DefaultQueryMetadata is the default implementation of the {@link QueryMetadata} interface
 *
 * @author tiwe
 */
public class DefaultQueryMetadata implements QueryMetadata, Cloneable {

    private static final long serialVersionUID = 317736313966701232L;

    private boolean distinct;

    private Set<Expression<?>> exprInJoins = new HashSet<Expression<?>>();

    private List<Expression<?>> groupBy = new ArrayList<Expression<?>>();

    private BooleanBuilder having = new BooleanBuilder();

    private List<JoinExpression> joins = new ArrayList<JoinExpression>();

    @Nullable
    private QueryModifiers modifiers = new QueryModifiers();

    private List<OrderSpecifier<?>> orderBy = new ArrayList<OrderSpecifier<?>>();

    private List<Expression<?>> projection = new ArrayList<Expression<?>>();

    // NOTE : this is not necessarily serializable
    private Map<ParamExpression<?>,Object> params = new HashMap<ParamExpression<?>,Object>();

    private boolean unique;

    private BooleanBuilder where = new BooleanBuilder();

    private Set<QueryFlag> flags = new LinkedHashSet<QueryFlag>();

    @Override
    public QueryMetadata addGroupBy(Expression<?>... o) {
        groupBy.addAll(Arrays.<Expression<?>> asList(o));
        return this;
    }

    @Override
    public QueryMetadata addHaving(Predicate... o) {
        for (Predicate e : o){
            if (!BooleanBuilder.class.isInstance(e) || ((BooleanBuilder)e).hasValue()){
                having.and(e);
            }
        }
        return this;
    }

    @Override
    public QueryMetadata addJoin(JoinType joinType, Expression<?> expr) {
        if (!exprInJoins.contains(expr)) {
            if (expr instanceof Path<?> && joinType == JoinType.DEFAULT){
                ensureRoot((Path<?>) expr);
            }
            joins.add(new JoinExpression(joinType, expr));
            exprInJoins.add(expr);
        }
        return this;
    }

    @Override
    public QueryMetadata addJoin(JoinExpression join){
        Expression<?> expr = join.getTarget();
        if (!exprInJoins.contains(expr)) {
            if (expr instanceof Path<?> && join.getType() == JoinType.DEFAULT){
                ensureRoot((Path<?>) expr);
            }
            joins.add(join);
            exprInJoins.add(expr);
        }
        return this;
    }

    @Override
    public QueryMetadata addJoinCondition(Predicate o) {
        if (!joins.isEmpty()) {
            joins.get(joins.size() - 1).addCondition(o);
        }
        return this;
    }

    @Override
    public QueryMetadata addOrderBy(OrderSpecifier<?>... o) {
        orderBy.addAll(Arrays.asList(o));
        return this;
    }

    @Override
    public QueryMetadata addProjection(Expression<?>... o) {
        projection.addAll(Arrays.asList(o));
        return this;
    }

    @Override
    public QueryMetadata addWhere(Predicate... o) {
        for (Predicate e : o){
            if (!BooleanBuilder.class.isInstance(e) || ((BooleanBuilder)e).hasValue()){
                where.and(e);
            }
        }
        return this;
    }

    public QueryMetadata clearOrderBy(){
        orderBy = new ArrayList<OrderSpecifier<?>>();
        return this;
    }

    public QueryMetadata clearProjection(){
        projection = new ArrayList<Expression<?>>();
        return this;
    }

    public QueryMetadata clearWhere(){
        where = new BooleanBuilder();
        return this;
    }

    @Override
    public QueryMetadata clone(){
        try {
            DefaultQueryMetadata clone = (DefaultQueryMetadata) super.clone();
            clone.exprInJoins = new HashSet<Expression<?>>(exprInJoins);
            clone.groupBy = new ArrayList<Expression<?>>(groupBy);
            clone.having = having.clone();
            clone.joins = new ArrayList<JoinExpression>(joins);
            clone.modifiers = new QueryModifiers(modifiers);
            clone.orderBy = new ArrayList<OrderSpecifier<?>>(orderBy);
            clone.projection = new ArrayList<Expression<?>>(projection);
            clone.params = new HashMap<ParamExpression<?>,Object>(params);
            clone.where = where.clone();
            clone.flags = new LinkedHashSet<QueryFlag>(flags);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new QueryException(e);
        }

    }

    private void ensureRoot(Path<?> path){
        if (path.getMetadata().getParent() != null){
            throw new IllegalArgumentException("Only root paths are allowed for joins : " + path);
        }
    }

    @Override
    public List<? extends Expression<?>> getGroupBy() {
        return Collections.unmodifiableList(groupBy);
    }

    @Override
    public Predicate getHaving() {
        return having.hasValue() ? having.getValue() : null;
    }

    @Override
    public List<JoinExpression> getJoins() {
        return Collections.unmodifiableList(joins);
    }

    @Override
    @Nullable
    public QueryModifiers getModifiers() {
        return modifiers;
    }

    public Map<ParamExpression<?>,Object> getParams(){
        return Collections.unmodifiableMap(params);
    }

    @Override
    public List<OrderSpecifier<?>> getOrderBy() {
        return Collections.unmodifiableList(orderBy);
    }

    @Override
    public List<? extends Expression<?>> getProjection() {
        return Collections.unmodifiableList(projection);
    }

    @Override
    public Predicate getWhere() {
        return where.hasValue() ? where.getValue() : null;
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public QueryMetadata reset() {
        clearProjection();
        params = new HashMap<ParamExpression<?>,Object>();
        modifiers = new QueryModifiers();
        return this;
    }

    @Override
    public QueryMetadata setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    @Override
    public QueryMetadata setLimit(Long limit) {
        if (modifiers == null || modifiers.getOffset() == null){
            modifiers = QueryModifiers.limit(limit);
        }else{
            modifiers = new QueryModifiers(limit, modifiers.getOffset());
        }
        return this;
    }

    @Override
    public QueryMetadata setModifiers(@Nullable QueryModifiers restriction) {
        this.modifiers = restriction;
        return this;
    }

    @Override
    public QueryMetadata setOffset(Long offset) {
        if (modifiers == null || modifiers.getLimit() == null){
            modifiers = QueryModifiers.offset(offset);
        }else{
            modifiers = new QueryModifiers(modifiers.getLimit(), offset);
        }
        return this;
    }

    @Override
    public QueryMetadata setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    @Override
    public <T> QueryMetadata setParam(ParamExpression<T> param, T value) {
        params.put(param, value);
        return this;
    }

    @Override
    public QueryMetadata addFlag(QueryFlag flag) {
        flags.add(flag);
        return this;
    }

    @Override
    public Set<QueryFlag> getFlags() {
        return flags;
    }

    @Override
    public boolean hasFlag(QueryFlag flag) {
        return flags.contains(flag);
    }

}

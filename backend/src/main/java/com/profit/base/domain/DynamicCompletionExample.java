package com.profit.base.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DynamicCompletionExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DynamicCompletionExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andSerialNumberIsNull() {
            addCriterion("SERIAL_NUMBER is null");
            return (Criteria) this;
        }

        public Criteria andSerialNumberIsNotNull() {
            addCriterion("SERIAL_NUMBER is not null");
            return (Criteria) this;
        }

        public Criteria andSerialNumberEqualTo(String value) {
            addCriterion("SERIAL_NUMBER =", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberNotEqualTo(String value) {
            addCriterion("SERIAL_NUMBER <>", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberGreaterThan(String value) {
            addCriterion("SERIAL_NUMBER >", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberGreaterThanOrEqualTo(String value) {
            addCriterion("SERIAL_NUMBER >=", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberLessThan(String value) {
            addCriterion("SERIAL_NUMBER <", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberLessThanOrEqualTo(String value) {
            addCriterion("SERIAL_NUMBER <=", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberLike(String value) {
            addCriterion("SERIAL_NUMBER like", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberNotLike(String value) {
            addCriterion("SERIAL_NUMBER not like", value, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberIn(List<String> values) {
            addCriterion("SERIAL_NUMBER in", values, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberNotIn(List<String> values) {
            addCriterion("SERIAL_NUMBER not in", values, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberBetween(String value1, String value2) {
            addCriterion("SERIAL_NUMBER between", value1, value2, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andSerialNumberNotBetween(String value1, String value2) {
            addCriterion("SERIAL_NUMBER not between", value1, value2, "serialNumber");
            return (Criteria) this;
        }

        public Criteria andDatetimeIsNull() {
            addCriterion("`DATETIME` is null");
            return (Criteria) this;
        }

        public Criteria andDatetimeIsNotNull() {
            addCriterion("`DATETIME` is not null");
            return (Criteria) this;
        }

        public Criteria andDatetimeEqualTo(Date value) {
            addCriterion("`DATETIME` =", value, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeNotEqualTo(Date value) {
            addCriterion("`DATETIME` <>", value, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeGreaterThan(Date value) {
            addCriterion("`DATETIME` >", value, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("`DATETIME` >=", value, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeLessThan(Date value) {
            addCriterion("`DATETIME` <", value, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeLessThanOrEqualTo(Date value) {
            addCriterion("`DATETIME` <=", value, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeIn(List<Date> values) {
            addCriterion("`DATETIME` in", values, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeNotIn(List<Date> values) {
            addCriterion("`DATETIME` not in", values, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeBetween(Date value1, Date value2) {
            addCriterion("`DATETIME` between", value1, value2, "datetime");
            return (Criteria) this;
        }

        public Criteria andDatetimeNotBetween(Date value1, Date value2) {
            addCriterion("`DATETIME` not between", value1, value2, "datetime");
            return (Criteria) this;
        }

        public Criteria andSupervisorIsNull() {
            addCriterion("SUPERVISOR is null");
            return (Criteria) this;
        }

        public Criteria andSupervisorIsNotNull() {
            addCriterion("SUPERVISOR is not null");
            return (Criteria) this;
        }

        public Criteria andSupervisorEqualTo(String value) {
            addCriterion("SUPERVISOR =", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorNotEqualTo(String value) {
            addCriterion("SUPERVISOR <>", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorGreaterThan(String value) {
            addCriterion("SUPERVISOR >", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorGreaterThanOrEqualTo(String value) {
            addCriterion("SUPERVISOR >=", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorLessThan(String value) {
            addCriterion("SUPERVISOR <", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorLessThanOrEqualTo(String value) {
            addCriterion("SUPERVISOR <=", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorLike(String value) {
            addCriterion("SUPERVISOR like", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorNotLike(String value) {
            addCriterion("SUPERVISOR not like", value, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorIn(List<String> values) {
            addCriterion("SUPERVISOR in", values, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorNotIn(List<String> values) {
            addCriterion("SUPERVISOR not in", values, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorBetween(String value1, String value2) {
            addCriterion("SUPERVISOR between", value1, value2, "supervisor");
            return (Criteria) this;
        }

        public Criteria andSupervisorNotBetween(String value1, String value2) {
            addCriterion("SUPERVISOR not between", value1, value2, "supervisor");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanIsNull() {
            addCriterion("EARLY_WATCHMAN is null");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanIsNotNull() {
            addCriterion("EARLY_WATCHMAN is not null");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanEqualTo(String value) {
            addCriterion("EARLY_WATCHMAN =", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanNotEqualTo(String value) {
            addCriterion("EARLY_WATCHMAN <>", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanGreaterThan(String value) {
            addCriterion("EARLY_WATCHMAN >", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanGreaterThanOrEqualTo(String value) {
            addCriterion("EARLY_WATCHMAN >=", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanLessThan(String value) {
            addCriterion("EARLY_WATCHMAN <", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanLessThanOrEqualTo(String value) {
            addCriterion("EARLY_WATCHMAN <=", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanLike(String value) {
            addCriterion("EARLY_WATCHMAN like", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanNotLike(String value) {
            addCriterion("EARLY_WATCHMAN not like", value, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanIn(List<String> values) {
            addCriterion("EARLY_WATCHMAN in", values, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanNotIn(List<String> values) {
            addCriterion("EARLY_WATCHMAN not in", values, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanBetween(String value1, String value2) {
            addCriterion("EARLY_WATCHMAN between", value1, value2, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andEarlyWatchmanNotBetween(String value1, String value2) {
            addCriterion("EARLY_WATCHMAN not between", value1, value2, "earlyWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanIsNull() {
            addCriterion("NIGHT_WATCHMAN is null");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanIsNotNull() {
            addCriterion("NIGHT_WATCHMAN is not null");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanEqualTo(String value) {
            addCriterion("NIGHT_WATCHMAN =", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanNotEqualTo(String value) {
            addCriterion("NIGHT_WATCHMAN <>", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanGreaterThan(String value) {
            addCriterion("NIGHT_WATCHMAN >", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanGreaterThanOrEqualTo(String value) {
            addCriterion("NIGHT_WATCHMAN >=", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanLessThan(String value) {
            addCriterion("NIGHT_WATCHMAN <", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanLessThanOrEqualTo(String value) {
            addCriterion("NIGHT_WATCHMAN <=", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanLike(String value) {
            addCriterion("NIGHT_WATCHMAN like", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanNotLike(String value) {
            addCriterion("NIGHT_WATCHMAN not like", value, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanIn(List<String> values) {
            addCriterion("NIGHT_WATCHMAN in", values, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanNotIn(List<String> values) {
            addCriterion("NIGHT_WATCHMAN not in", values, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanBetween(String value1, String value2) {
            addCriterion("NIGHT_WATCHMAN between", value1, value2, "nightWatchman");
            return (Criteria) this;
        }

        public Criteria andNightWatchmanNotBetween(String value1, String value2) {
            addCriterion("NIGHT_WATCHMAN not between", value1, value2, "nightWatchman");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.provider.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to call JDBC with lambda expressions.
 */
public class JdbcTemplate {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);
    private DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes a query on JDBC and return the result as a list of domain objects.
     *
     * @param query the SQL query with the parameter placeholders.
     * @param rowMapper Row mapper functional interface
     * @return List of domain objects of required type.
     * @see #executeQuery(String, RowMapper, QueryFilter)
     */
    public <T extends Object> List<T> executeQuery(String query, RowMapper<T> rowMapper) throws DataAccessException {
        return executeQuery(query, rowMapper, null);
    }

    /**
     * Executes a query on JDBC and return the result as a list of domain objects.
     *
     * @param query the SQL query with the parameter placeholders.
     * @param rowMapper Row mapper functional interface
     * @param queryFilter parameters for the SQL query parameter replacement.
     * @return List of domain objects of required type.
     */
    public <T extends Object> List<T> executeQuery(String query, RowMapper<T> rowMapper, QueryFilter queryFilter)
            throws DataAccessException {
        List<T> result = new ArrayList();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (queryFilter != null) {
                queryFilter.filter(preparedStatement);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            int i = 0;
            while (resultSet.next()) {
                T row = rowMapper.mapRow(resultSet, i);
                result.add(row);
                i++;
            }
        } catch (SQLException e) {
            logDebugInfo(
                    "There has been an error performing the database query. The query is {}, and the Parameters are {}",
                    e, query, queryFilter);
            throw new DataAccessException("Error in performing Database query: " + query, e);
        }
        return result;
    }

    /**
     * Executes a query on JDBC and return the result as a domain object.
     *
     * @param query the SQL query with the parameter placeholders.
     * @param rowMapper Row mapper functional interface
     * @param queryFilter parameters for the SQL query parameter replacement.
     * @return domain object of required type.
     */
    public <T extends Object> T fetchSingleRecord(String query, RowMapper<T> rowMapper, QueryFilter queryFilter)
            throws DataAccessException {
        T result = null;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (queryFilter != null) {
                queryFilter.filter(preparedStatement);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = rowMapper.mapRow(resultSet, 0);
            }
            if (resultSet.next()) {
                logDebugInfo("There are more records than one found for query: {} for the parameters {}", query,
                        queryFilter);
                throw new DataAccessException("There are more records than one found for query: " + query);
            }
        } catch (SQLException e) {
            logDebugInfo(
                    "There has been an error performing the database query. The query is {}, and the parameters are {}",
                    e, query, rowMapper, queryFilter);
            throw new DataAccessException("Error in performing database query: " + query, e);
        }
        return result;
    }

    public void executeUpdate(String query, QueryFilter queryFilter) throws DataAccessException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (queryFilter != null) {
                queryFilter.filter(preparedStatement);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logDebugInfo("Error in performing database update: {} with parameters {}", query, queryFilter);
            throw new DataAccessException("Error in performing database update: " + query, e);
        }
    }

    /**
     * Executes the jdbc insert/update query.
     *
     * @param query The SQL for insert/update.
     * @param <T>
     */
    public <T extends Object> void executeUpdate(String query) throws DataAccessException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            doInternalUpdate(null, preparedStatement);
        } catch (SQLException e) {
            logDebugInfo("Error in performing database update: {}", query);
            throw new DataAccessException("Error in performing database update: " + query, e);
        }
    }

    /**
     * Executes the jdbc insert/update query.
     *
     * @param query The SQL for insert/update.
     * @param queryFilter Query filter to prepared statement parameter binding.
     * @param bean the Domain object to be inserted/updated.
     * @param <T>
     */
    public <T extends Object> int executeInsert(String query, QueryFilter queryFilter, T bean, boolean fetchInsertedId)
            throws DataAccessException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            doInternalUpdate(queryFilter, preparedStatement);
            if (fetchInsertedId) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Mapping generated key (Auto Increment ID) to the object");
                }
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int resultId = generatedKeys.getInt(1);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Newly inserted ID (Auto Increment ID) is {} for the bean {} ",
                                    resultId, bean);
                        }
                        return resultId;
                    } else {
                        throw new SQLException("Creating the record failed with Auto-Generated ID, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            logDebugInfo("Error in performing database insert: {} with parameters {}", query, queryFilter);
            throw new DataAccessException("Error in performing database insert: " + query, e);
        }
        return 0;
    }

    private <T extends Object> void doInternalUpdate(QueryFilter queryFilter, PreparedStatement preparedStatement)
            throws SQLException, DataAccessException {
        if (queryFilter != null) {
            queryFilter.filter(preparedStatement);
        }
        preparedStatement.executeUpdate();
    }

    private void logDebugInfo(String s, Object... params) {
        logDebugInfo(s, null, params);
    }

    private void logDebugInfo(String s, Exception e, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(MessageFormatter.arrayFormat(s, params).getMessage(), e);
        }
    }
}

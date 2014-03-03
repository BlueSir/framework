package com.sohu.smc.core.jdbc;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import scala.runtime.AbstractFunction0;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static com.sohu.smc.core.jdbc.TraceUtil.update_obj;

/**
 * 封装{@link org.springframework.jdbc.core.JdbcTemplate} ,加入监控
 * User: shijinkui
 * Date: 12-8-7
 * Time: 下午2:57
 * To change this template use File | Settings | File Templates.
 */
public class SmcJdbcTemplate {
    private final JdbcTemplate jdbcTemplate;

    public SmcJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public SmcJdbcTemplate(DataSource dataSource, boolean lazyInit) {
        this.jdbcTemplate = new JdbcTemplate(dataSource, lazyInit);
    }


    public int update(final String sql, final Object... args) {
        Integer ret = TraceUtil.trace(sql, new AbstractFunction0<Integer>() {
            public Integer apply() {
                return jdbcTemplate.update(sql, args);
            }
        });

        return ret;
    }


    public void setFetchSize(int fetchSize) {
        jdbcTemplate.setFetchSize(fetchSize);
    }

    public int getFetchSize() {
        return jdbcTemplate.getFetchSize();
    }

    /**
     * Set the maximum number of rows for this JdbcTemplate. This is important
     * for processing subsets of large result sets, avoiding to read and hold
     * the entire result set in the database or in the JDBC driver if we're
     * never interested in the entire result in the first place (for example,
     * when performing searches that might return a large number of matches).
     * <p>Default is 0, indicating to use the JDBC driver's default.
     *
     * @see java.sql.Statement#setMaxRows
     */
    public void setMaxRows(int maxRows) {
        jdbcTemplate.setMaxRows(maxRows);
    }

    /**
     * Return the maximum number of rows specified for this JdbcTemplate.
     */
    public int getMaxRows() {
        return jdbcTemplate.getMaxRows();
    }

    /**
     * Set the query timeout for statements that this JdbcTemplate executes.
     * <p>Default is 0, indicating to use the JDBC driver's default.
     * <p>Note: Any timeout specified here will be overridden by the remaining
     * transaction timeout when executing within a transaction that has a
     * timeout specified at the transaction level.
     *
     * @see java.sql.Statement#setQueryTimeout
     */
    public void setQueryTimeout(int queryTimeout) {
        jdbcTemplate.setQueryTimeout(queryTimeout);
    }

    /**
     * Return the query timeout for statements that this JdbcTemplate executes.
     */
    public int getQueryTimeout() {
        return jdbcTemplate.getQueryTimeout();
    }

    /**
     * Set whether results processing should be skipped.  Can be used to optimize callable
     * statement processing when we know that no results are being passed back - the processing
     * of out parameter will still take place.  This can be used to avoid a bug in some older
     * Oracle JDBC drivers like 10.1.0.2.
     */
    public void setSkipResultsProcessing(boolean skipResultsProcessing) {
        jdbcTemplate.setSkipResultsProcessing(skipResultsProcessing);
    }

    /**
     * Return whether results processing should be skipped.
     */
    public boolean isSkipResultsProcessing() {
        return jdbcTemplate.isSkipResultsProcessing();
    }

    /**
     * Set whether undelared results should be skipped.
     */
    public void setSkipUndeclaredResults(boolean skipUndeclaredResults) {
        jdbcTemplate.setSkipUndeclaredResults(skipUndeclaredResults);
    }

    /**
     * Return whether undeclared results should be skipped.
     */
    public boolean isSkipUndeclaredResults() {
        return jdbcTemplate.isSkipUndeclaredResults();
    }

    /**
     * Set whether execution of a CallableStatement will return the results in a Map
     * that uses case insensitive names for the parameters.
     */
    public void setResultsMapCaseInsensitive(boolean resultsMapCaseInsensitive) {
        jdbcTemplate.setResultsMapCaseInsensitive(resultsMapCaseInsensitive);
    }

    /**
     * Return whether execution of a CallableStatement will return the results in a Map
     * that uses case insensitive names for the parameters.
     */
    public boolean isResultsMapCaseInsensitive() {
        return jdbcTemplate.isResultsMapCaseInsensitive();
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.query(sql, rowMapper);
            }
        });
    }

    public Map<String, Object> queryForMap(final String sql) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Map<String, Object>>() {
            @Override
            public Map<String, Object> apply() {
                return jdbcTemplate.queryForMap(sql);
            }
        });

    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, rowMapper);
            }
        });
    }

    public <T> T queryForObject(final String sql, final Class<T> requiredType) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, requiredType);
            }
        });
    }

    public long queryForLong(final String sql) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Long>() {
            @Override
            public Long apply() {
                return jdbcTemplate.queryForLong(sql);
            }
        });
    }

    public int queryForInt(final String sql) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Integer>() {
            @Override
            public Integer apply() {
                return jdbcTemplate.queryForInt(sql);
            }
        });
    }

    public <T> List<T> queryForList(final String sql, final Class<T> elementType) throws DataAccessException {
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.queryForList(sql, elementType);
            }
        });
    }

    public List<Map<String, Object>> queryForList(final String sql) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> apply() {
                return jdbcTemplate.queryForList(sql);
            }
        });
    }

    public SqlRowSet queryForRowSet(final String sql) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<SqlRowSet>() {
            @Override
            public SqlRowSet apply() {
                return jdbcTemplate.queryForRowSet(sql);
            }
        });
    }

    public int update(final String sql) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Integer>() {
            @Override
            public Integer apply() {
                return jdbcTemplate.update(sql);
            }
        });
    }

    public int[] batchUpdate(final String[] sql) throws DataAccessException {
        
        return TraceUtil.trace(sql[0], new AbstractFunction0<int[]>() {
            @Override
            public int[] apply() {
                return jdbcTemplate.batchUpdate(sql);
            }
        });
    }

    public <T> List<T> query(final String sql, final Object[] args, final int[] argTypes, final RowMapper<T> rowMapper) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.query(sql, args, argTypes, rowMapper);
            }
        });
    }

    public <T> List<T> query(final String sql, final Object[] args, final RowMapper<T> rowMapper) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.query(sql, args, rowMapper);
            }
        });
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.query(sql, rowMapper, args);
            }
        });
    }

    public <T> T queryForObject(final String sql, final Object[] args, final int[] argTypes, final RowMapper<T> rowMapper)
            throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, args, argTypes, rowMapper);
            }
        });
    }

    public <T> T queryForObject(final String sql, final Object[] args, final RowMapper<T> rowMapper) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, args, rowMapper);
            }
        });
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, rowMapper, args);
            }
        });
    }

    public <T> T queryForObject(final String sql, final Object[] args, final int[] argTypes, final Class<T> requiredType)
            throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, args, argTypes, requiredType);
            }
        });
    }

    public <T> T queryForObject(final String sql, final Object[] args, final Class<T> requiredType) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, args, requiredType);
            }
        });
    }

    public <T> T queryForObject(final String sql, final Class<T> requiredType, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<T>() {
            @Override
            public T apply() {
                return jdbcTemplate.queryForObject(sql, requiredType, args);
            }
        });
    }

    public Map<String, Object> queryForMap(final String sql, final Object[] args, final int[] argTypes) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Map<String, Object>>() {
            @Override
            public Map<String, Object> apply() {
                return jdbcTemplate.queryForMap(sql, args, argTypes);
            }
        });
    }

    public Map<String, Object> queryForMap(final String sql, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Map<String, Object>>() {
            @Override
            public Map<String, Object> apply() {
                return jdbcTemplate.queryForMap(sql, args);
            }
        });
    }

    public long queryForLong(final String sql, final Object[] args, final int[] argTypes) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Long>() {
            @Override
            public Long apply() {
                return jdbcTemplate.queryForLong(sql, args, argTypes);
            }
        });
    }

    public long queryForLong(final String sql, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Long>() {
            @Override
            public Long apply() {
                return jdbcTemplate.queryForLong(sql, args);
            }
        });
    }

    public int queryForInt(final String sql, final Object[] args, final int[] argTypes) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Integer>() {
            @Override
            public Integer apply() {
                return jdbcTemplate.queryForInt(sql, args, argTypes);
            }
        });
    }

    public int queryForInt(final String sql, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<Integer>() {
            @Override
            public Integer apply() {
                return jdbcTemplate.queryForInt(sql, args);
            }
        });
    }

    public <T> List<T> queryForList(final String sql, final Object[] args, final int[] argTypes, final Class<T> elementType) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.queryForList(sql, args, argTypes, elementType);
            }
        });
    }

    public <T> List<T> queryForList(final String sql, final Object[] args, final Class<T> elementType) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.queryForList(sql, args, elementType);
            }
        });
    }

    public <T> List<T> queryForList(final String sql, final Class<T> elementType, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<T>>() {
            @Override
            public List<T> apply() {
                return jdbcTemplate.queryForList(sql, elementType, args);
            }
        });
    }

    public List<Map<String, Object>> queryForList(final String sql, final Object[] args, final int[] argTypes) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> apply() {
                return jdbcTemplate.queryForList(sql, args, argTypes);
            }
        });
    }

    public List<Map<String, Object>> queryForList(final String sql, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> apply() {
                return jdbcTemplate.queryForList(sql, args);
            }
        });
    }

    public SqlRowSet queryForRowSet(final String sql, final Object[] args, final int[] argTypes) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<SqlRowSet>() {
            @Override
            public SqlRowSet apply() {
                return jdbcTemplate.queryForRowSet(sql, args, argTypes);
            }
        });
    }

    public SqlRowSet queryForRowSet(final String sql, final Object... args) throws DataAccessException {
        
        return TraceUtil.trace(sql, new AbstractFunction0<SqlRowSet>() {
            @Override
            public SqlRowSet apply() {
                return jdbcTemplate.queryForRowSet(sql, args);
            }
        });
    }

    public int update(final String sql, final Object[] args, final int[] argTypes) throws DataAccessException {
        
        return TraceUtil.trace(update_obj, new AbstractFunction0<Integer>() {
            @Override
            public Integer apply() {
                return jdbcTemplate.update(sql, args, argTypes);
            }
        });

    }

    public int[] batchUpdate(final String sql, final List<Object[]> batchArgs) {
        return TraceUtil.trace(update_obj, new AbstractFunction0<int[]>() {
            @Override
            public int[] apply() {
                return jdbcTemplate.batchUpdate(sql, batchArgs);
            }
        });
    }

    public int[] batchUpdate(final String sql, final List<Object[]> batchArgs, final int[] argTypes) {
        return TraceUtil.trace(update_obj, new AbstractFunction0<int[]>() {
            @Override
            public int[] apply() {
                return jdbcTemplate.batchUpdate(sql, batchArgs, argTypes);
            }
        });
    }
}

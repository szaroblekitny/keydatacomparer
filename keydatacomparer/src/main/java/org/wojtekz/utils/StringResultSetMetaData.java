package org.wojtekz.utils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class StringResultSetMetaData implements ResultSetMetaData {
	
	private String[][] resultSet = {
			{"RowId", "GlownyID", "Imie", "Nazwisko"},
			{"AAA", "1", "Jan", "Kowalski"},
			{"AAB", "2", "Stanisław", "Nowak"},
			{"AAC", "3", "Kazimiera", "Brzoza"}
	};

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public int getColumnCount() throws SQLException {
		return StringResultSet.RESULT_SET_WIDTH;
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		return true;
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		return false;
	}

	@Override
	public int isNullable(int column) throws SQLException {
		return columnNullableUnknown;
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		return false;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		return 100;
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		if (column >= 1 && column <= StringResultSet.RESULT_SET_WIDTH) {
			return resultSet[0][column];
		}
		throw new SQLException("Wrong column number");
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		if (column >= 1 && column <= StringResultSet.RESULT_SET_WIDTH) {
			return resultSet[0][column];
		}
		throw new SQLException("Wrong column number");
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		return null;
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		return 0;
	}

	@Override
	public int getScale(int column) throws SQLException {
		return 0;
	}

	@Override
	public String getTableName(int column) throws SQLException {
		return null;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		return null;
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		return Types.VARCHAR;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		return "VARCHAR2";
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return false;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		return "java.lang.String";
	}

}

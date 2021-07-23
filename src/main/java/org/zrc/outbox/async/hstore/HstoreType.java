package org.zrc.outbox.async.hstore;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HstoreType implements UserType {

    private static final String HSTORE_SEPARATOR = "=>";

    @Override
    public int[] sqlTypes() {
        return new int[] { Types.OTHER };
    }

    @Override
    public Class returnedClass() {
        return Map.class;
    }

    @Override
    public boolean equals(Object objectA, Object objectB) throws HibernateException {
        return objectA.equals(objectB);
    }

    @Override
    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        String col = names[0];
        String val = rs.getString(col);
        return convertStringToHstore(val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        st.setObject(index, convertHstoreToString((Map) value), Types.OTHER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deepCopy(Object value) throws HibernateException {
        return new HashMap(Map.copyOf( (Map) value));
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    private String convertHstoreToString(Map<String, String> hstore) {
        if (hstore.isEmpty()) {
            return "";
        }
        return hstore.entrySet().stream()
                .map((entry) -> "\"" + entry.getKey() + "\"" + HSTORE_SEPARATOR + "\"" + entry.getValue() + "\"")
                .collect(Collectors.joining(", "));
    }

    public Map<String, String> convertStringToHstore(String input) {
        var hstore = new HashMap<String, String>();
        if (! StringUtils.hasText(input)) {
            return hstore;
        }

        Stream.of(input.split(", "))
                .map(str -> str.split(HSTORE_SEPARATOR))
                .forEach(pair -> {
                    hstore.put(cleanValue(pair[0]), cleanValue(pair[1]));
                });
        return hstore;
    }

    private String cleanValue(String input) {
        return input.trim().substring(1, input.length() - 1);
    }
}

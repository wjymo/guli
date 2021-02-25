package com.zzn.estest.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zzn.estest.dao.DeviceWarnMapper;
import com.zzn.estest.dao.TestMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class OrgFilter {
    protected Set<String> includedMapper = Sets.newHashSet();
    protected Set<String> includedSql = Sets.newHashSet();

    public OrgFilter() {
        this.includedMapper.add(TestMapper.class.getName());


        this.includedSql.add(DeviceWarnMapper.class.getName()+".getEsResultById");
        this.includedSql.add(DeviceWarnMapper.class.getName()+".getAllForEs");

    }

    public boolean containsMapper(String mapper) {
        return includedMapper.contains(mapper);
    }
    public boolean containsSql(String sql) {
        return includedSql.contains(sql);
    }
}
